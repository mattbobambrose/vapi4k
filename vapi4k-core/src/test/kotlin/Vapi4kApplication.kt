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

import com.vapi4k.api.buttons.ButtonColor
import com.vapi4k.api.buttons.enums.ButtonPosition
import com.vapi4k.api.buttons.enums.ButtonType
import com.vapi4k.api.destination.enums.AssistantTransferMode
import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.api.vapi4k.AssistantRequestUtils.hasStatusUpdateError
import com.vapi4k.api.vapi4k.AssistantRequestUtils.statusUpdateError
import com.vapi4k.api.voice.enums.ElevenLabsVoiceIdType
import com.vapi4k.api.voice.enums.ElevenLabsVoiceModelType
import com.vapi4k.server.Vapi4k
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.server.defaultKtorConfig
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import com.vapi4k.utils.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.utils.enums.ServerRequestType.STATUS_UPDATE
import com.vapi4k.utils.enums.ServerRequestType.TOOL_CALL
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun main() {
  embeddedServer(
    factory = CIO,
    port = 8080,
    host = "0.0.0.0",
    module = Application::module,
  ).start(wait = true)
}

fun Application.module() {
  val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  defaultKtorConfig(appMicrometerRegistry)

  install(Vapi4k) {
    webApplication {
      // serverPath = "/inboundRequest"

      onAssistantRequest { args ->
        assistant {
          openAIModel {
            modelType = OpenAIModelType.GPT_4_TURBO
            systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."
          }
          elevenLabsVoice {
            voiceIdType = ElevenLabsVoiceIdType.PAULA
            modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2
          }

          firstMessage = "Hi, I am Beth how can I assist you today?"
        }

        buttonConfig {
          position = ButtonPosition.BOTTOM_LEFT
          offset = "40px"
          width = "50px"
          height = "50px"

          idle {
            color = ButtonColor(93, 254, 202)
            type = ButtonType.PILL
            title = "Have a quick question?"
            subtitle = "Talk with our AI assistant"
            icon = "https://unpkg.com/lucide-static@0.321.0/icons/phone.svg"
          }

          loading {
            color = ButtonColor(93, 124, 202)
            type = ButtonType.PILL
            title = "Connecting..."
            subtitle = "Please wait"
            icon = "https://unpkg.com/lucide-static@0.321.0/icons/loader-2.svg"
          }

          active {
            color = ButtonColor(255, 0, 0)
            type = ButtonType.PILL
            title = "Call is in progress..."
            subtitle = "End the call."
            icon = "https://unpkg.com/lucide-static@0.321.0/icons/phone-off.svg"
          }
        }
      }
    }

    inboundCallApplication {
      serverPath = "/inboundRequest1"
      serverSecret = "12345"

      onAssistantRequest { request ->
        myAssistantRequest(request)
      }
    }

    inboundCallApplication {
      serverPath = "/inboundRequest2"

      onAssistantRequest { request ->
        myAssistantRequest(request)
      }

      onTransferDestinationRequest { request ->
        assistantDestination {
          assistantName = "Assistant"
          transferMode = AssistantTransferMode.ROLLING_HISTORY
          message = "Message"
          description = "Description"
        }
      }

      onAllRequests { request ->
        logger.info { "All requests: ${request.requestType}" }
//        if (isProduction)
//          insertRequest(request)
//        logObject(request)
//        printObject(request)
      }

      onRequest(TOOL_CALL) { request ->
//        logger.info { "Tool call: $request" }
      }

      onRequest(FUNCTION_CALL) { request ->
//        logger.info { "Function call: $request" }
      }

      onRequest(STATUS_UPDATE) { request ->
        logger.info { "Status update: STATUS_UPDATE" }
      }

      onRequest(STATUS_UPDATE) { request ->
        if (request.hasStatusUpdateError()) {
          logger.info { "Status update error: ${request.statusUpdateError}" }
        }
      }

      onAllResponses { requestType, response, elapsedTime ->
//        logger.info { "All responses: $response" }
//        logObject(response)
//        logger.info { response.toJsonString() }
//        if (isProduction)
//          insertResponse(requestType, response, elapsedTime)
      }

      onResponse(ASSISTANT_REQUEST) { requestType, response, elapsed ->
//      logger.info { "Response: $response" }
      }
    }
  }
}
