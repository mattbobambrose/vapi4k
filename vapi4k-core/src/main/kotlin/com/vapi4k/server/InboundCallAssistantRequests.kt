/*
 * Copyright © 2024 Matthew Ambrose (mattbobambrose@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.vapi4k.server

import com.vapi4k.common.CoreEnvVars.isProduction
import com.vapi4k.dsl.vapi4k.InboundCallApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponse.Companion.getToolCallResponse
import com.vapi4k.server.AdminJobs.invokeRequestCallbacks
import com.vapi4k.server.AdminJobs.invokeResponseCallbacks
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.common.Utils.lambda
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import com.vapi4k.utils.enums.ServerRequestType.END_OF_CALL_REPORT
import com.vapi4k.utils.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.utils.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.utils.enums.ServerRequestType.TRANSFER_DESTINATION_REQUEST
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlin.time.measureTimedValue

internal object InboundCallAssistantRequests {
  suspend fun KtorCallContext.inboundCallAssistantRequests(
    config: Vapi4kConfigImpl,
    application: InboundCallApplicationImpl,
  ) {
    if (!isValidSecret(application.serverSecret)) {
      call.respond(HttpStatusCode.Forbidden, "Invalid secret")
    } else {
      if (isProduction) {
        processInboundCallAssistantRequest(config, application)
      } else {
        runCatching {
          processInboundCallAssistantRequest(config, application)
        }.onFailure { e ->
          logger.error(e) { "Error processing POST request: ${e.errorMsg}" }
        }
      }
    }
  }

  private suspend fun KtorCallContext.processInboundCallAssistantRequest(
    config: Vapi4kConfigImpl,
    application: InboundCallApplicationImpl,
  ) {
    val request = call.receive<String>().toJsonElement()
    val requestType = request.requestType
    invokeRequestCallbacks(config, application.applicationId, requestType, request)

    val (response, duration) = measureTimedValue {
      when (requestType) {
        ASSISTANT_REQUEST -> {
          val response = application.getAssistantResponse(request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        FUNCTION_CALL -> {
          val response = getFunctionCallResponse(application, request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        TOOL_CALL -> {
          val response = getToolCallResponse(application, request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        TRANSFER_DESTINATION_REQUEST -> {
          logger.info { "Transfer destination request received: ${request.toJsonString()}" }
          val response = application.getTransferDestinationResponse(request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        END_OF_CALL_REPORT -> {
          if (application.eocrCacheRemovalEnabled) {
            val sessionCacheId = request.sessionCacheId
            with(application) {
              serviceToolCache.removeFromCache(sessionCacheId) { funcInfo ->
                logger.info { "EOCR removed ${funcInfo.functions.size} serviceTool cache items [${funcInfo.ageSecs}] " }
              } ?: logger.warn { "EOCR unable to find and remove serviceTool cache entry [$sessionCacheId]" }
              functionCache.removeFromCache(sessionCacheId) { funcInfo ->
                logger.info { "EOCR removed ${funcInfo.functions.size} function cache items [${funcInfo.ageSecs}] " }
              } ?: logger.warn { "EOCR unable to find and remove function cache entry [$sessionCacheId]" }
            }
          }

          val response = SimpleMessageResponse("End of call report received")
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        else -> {
          val response = SimpleMessageResponse("$requestType received")
          call.respond(response)
          lambda { response.toJsonElement() }
        }
      }
    }

    invokeResponseCallbacks(config, application.applicationId, requestType, response, duration)
  }

  private suspend fun KtorCallContext.isValidSecret(configPropertiesSecret: String): Boolean {
    val secret = call.request.headers["x-vapi-secret"].orEmpty()
    return if (configPropertiesSecret.isNotEmpty() && secret != configPropertiesSecret) {
      logger.info { "Invalid secret: [$secret] [$configPropertiesSecret]" }
      false
    } else {
      true
    }
  }
}
