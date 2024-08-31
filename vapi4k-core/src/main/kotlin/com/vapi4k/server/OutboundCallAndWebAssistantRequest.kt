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

import com.vapi4k.common.AssistantId
import com.vapi4k.common.CoreEnvVars.isProduction
import com.vapi4k.common.Headers.VALIDATE_HEADER
import com.vapi4k.common.Headers.VALIDATE_VALUE
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.OutboundCallApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.dsl.vapi4k.WebApplicationImpl
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponseDto.Companion.getToolCallResponse
import com.vapi4k.server.AdminJobs.invokeRequestCallbacks
import com.vapi4k.server.AdminJobs.invokeResponseCallbacks
import com.vapi4k.server.ValidateApplication.isValidSecret
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.common.Utils.lambda
import com.vapi4k.utils.common.Utils.toErrorString
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import com.vapi4k.utils.enums.ServerRequestType.END_OF_CALL_REPORT
import com.vapi4k.utils.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.utils.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.utils.enums.ServerRequestType.TRANSFER_DESTINATION_REQUEST
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlinx.serialization.json.JsonElement
import kotlin.time.measureTimedValue

internal object OutboundCallAndWebAssistantRequest {
  suspend fun KtorCallContext.outboundCallAndWebAssistantRequest(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    request: JsonElement,
    sessionId: SessionId,
    assistantId: AssistantId,
  ) {
    if (!isValidSecret(application.serverSecret)) {
      call.respond(HttpStatusCode.Forbidden, "Invalid secret")
    } else {
      val validateCall = call.request.headers[VALIDATE_HEADER].orEmpty()
      if (isProduction || validateCall != VALIDATE_VALUE) {
        processOutboundCallAndWebAssistantRequest(config, application, request, sessionId, assistantId)
      } else {
        runCatching {
          processOutboundCallAndWebAssistantRequest(config, application, request, sessionId, assistantId)
        }.onFailure { e ->
          logger.error(e) { "Error processing web assistant request" }
          call.respondText(e.toErrorString(), status = HttpStatusCode.InternalServerError)
        }
      }
    }
  }

  private suspend fun KtorCallContext.processOutboundCallAndWebAssistantRequest(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    request: JsonElement,
    sessionId: SessionId,
    assistantId: AssistantId,
  ) {
    val requestType = request.requestType
    invokeRequestCallbacks(config, application.applicationId, requestType, request)

    val (response, duration) = measureTimedValue {
      when (requestType) {
        ASSISTANT_REQUEST -> {
          val response =
            when (application) {
              is OutboundCallApplicationImpl -> application.getAssistantResponse(request, sessionId)
              is WebApplicationImpl -> application.getAssistantResponse(request, sessionId)
              else -> error("Invalid application type: ${application.applicationType}")
            }

          // Drop the messageResponse prefix property
          call.respond(response.messageResponse)
          lambda { response.toJsonElement() }
        }

        FUNCTION_CALL -> {
          val response = getFunctionCallResponse(application, request, sessionId, assistantId)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        TOOL_CALL -> {
          val response = getToolCallResponse(application, request, sessionId, assistantId)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        TRANSFER_DESTINATION_REQUEST -> {
          val response = application.getTransferDestinationResponse(request, sessionId, assistantId)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        END_OF_CALL_REPORT -> {
          application.processEOCRMessage(sessionId, assistantId)
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
}
