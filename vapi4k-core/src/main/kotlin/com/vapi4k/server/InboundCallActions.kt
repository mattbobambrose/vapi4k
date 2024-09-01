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
import com.vapi4k.common.Headers.VALIDATE_HEADER
import com.vapi4k.common.Headers.VALIDATE_VALUE
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.plugin.KtorCallContext
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponseDto.Companion.getToolCallResponse
import com.vapi4k.server.AdminJobs.invokeRequestCallbacks
import com.vapi4k.server.AdminJobs.invokeResponseCallbacks
import com.vapi4k.utils.common.Utils.lambda
import com.vapi4k.utils.common.Utils.toErrorString
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import com.vapi4k.utils.enums.ServerRequestType.END_OF_CALL_REPORT
import com.vapi4k.utils.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.utils.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.utils.enums.ServerRequestType.TRANSFER_DESTINATION_REQUEST
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.validate.ValidateApplication.isValidSecret
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlin.time.measureTimedValue

internal object InboundCallActions {
  suspend fun KtorCallContext.inboundCallRequest(
    config: Vapi4kConfigImpl,
    requestContext: RequestContext,
  ) {
    if (!isValidSecret(requestContext.application.serverSecret)) {
      call.respond(HttpStatusCode.Forbidden, "Invalid secret")
    } else {
      val validateCall = call.request.headers[VALIDATE_HEADER].orEmpty()
      if (isProduction || validateCall != VALIDATE_VALUE) {
        processInboundCallRequest(config, requestContext)
      } else {
        runCatching {
          processInboundCallRequest(config, requestContext)
        }.onFailure { e ->
          logger.error(e) { "Error processing inbound call assistant request" }
          call.respondText(e.toErrorString(), status = HttpStatusCode.InternalServerError)
        }
      }
    }
  }

  private suspend fun KtorCallContext.processInboundCallRequest(
    config: Vapi4kConfigImpl,
    requestContext: RequestContext,
  ) {
    val requestType = requestContext.request.requestType
    invokeRequestCallbacks(config, requestContext)

    val (response, duration) =
      measureTimedValue {
        with(requestContext) {
          when (requestType) {
            ASSISTANT_REQUEST -> {
              val response = application.getAssistantResponse(requestContext)
              call.respond(response)
              lambda { response.toJsonElement() }
            }

            FUNCTION_CALL -> {
              val response = getFunctionCallResponse(requestContext)
              call.respond(response)
              lambda { response.toJsonElement() }
            }

            TOOL_CALL -> {
              val response = getToolCallResponse(requestContext)
              call.respond(response)
              lambda { response.toJsonElement() }
            }

            TRANSFER_DESTINATION_REQUEST -> {
              val response = application.getTransferDestinationResponse(requestContext)
              call.respond(response)
              lambda { response.toJsonElement() }
            }

            END_OF_CALL_REPORT -> {
              application.processEOCRMessage(requestContext)
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
      }

    invokeResponseCallbacks(config, requestContext, response, duration)
  }
}
