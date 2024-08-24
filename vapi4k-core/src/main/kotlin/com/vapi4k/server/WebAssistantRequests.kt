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
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.dsl.vapi4k.WebApplicationImpl
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponse.Companion.getToolCallResponse
import com.vapi4k.server.AdminJobs.invokeRequestCallbacks
import com.vapi4k.server.AdminJobs.invokeResponseCallbacks
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.common.Utils.lambda
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import com.vapi4k.utils.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.utils.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import io.ktor.server.application.call
import io.ktor.server.response.respond
import kotlinx.serialization.json.JsonElement
import kotlin.time.measureTimedValue

internal object WebAssistantRequests {
  suspend fun KtorCallContext.webAssistantRequests(
    config: Vapi4kConfigImpl,
    application: WebApplicationImpl,
    request: JsonElement,
  ) {
    if (isProduction) {
      processWebAssistantRequest(config, application, request)
    } else {
      runCatching {
        processWebAssistantRequest(config, application, request)
      }.onFailure { e ->
        logger.error(e) { "Error processing request: ${e.errorMsg}" }
      }
    }
  }

  private suspend fun KtorCallContext.processWebAssistantRequest(
    config: Vapi4kConfigImpl,
    application: WebApplicationImpl,
    request: JsonElement,
  ) {
    val requestType = request.requestType
    invokeRequestCallbacks(config, application.applicationId, requestType, request)

    val (response, duration) = measureTimedValue {
      when (request.requestType) {
        ASSISTANT_REQUEST -> {
          val response = application.getAssistantResponse(request)
          call.respond(response.messageResponse)
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

        else -> {
          val response = SimpleMessageResponse("$requestType received")
          call.respond(response)
          lambda { response.toJsonElement() }
        }
      }
    }

    invokeResponseCallbacks(config, application.applicationId, requestType, response, duration)
  }
}
