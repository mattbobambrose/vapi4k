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

import TalkPage.talkPage
import com.vapi4k.FavoriteFoodService
import com.vapi4k.WeatherLookupByAreaCodeService
import com.vapi4k.WeatherLookupService1
import com.vapi4k.api.buttons.ButtonColor
import com.vapi4k.api.buttons.enums.ButtonPosition
import com.vapi4k.api.buttons.enums.ButtonType
import com.vapi4k.api.destination.enums.AssistantTransferMode
import com.vapi4k.api.functions.Functions
import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.api.squad.Member
import com.vapi4k.api.tools.Tools
import com.vapi4k.api.vapi4k.AssistantRequestUtils.hasStatusUpdateError
import com.vapi4k.api.vapi4k.AssistantRequestUtils.statusUpdateError
import com.vapi4k.api.voice.enums.ElevenLabsVoiceIdType
import com.vapi4k.api.voice.enums.ElevenLabsVoiceModelType
import com.vapi4k.plugin.Vapi4k
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.server.defaultKtorConfig
import com.vapi4k.utils.enums.ServerRequestType
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import com.vapi4k.utils.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.utils.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
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

  routing {
    get("/talk") {
      call.respondText(talkPage(), contentType = ContentType.Text.Html)
    }
  }

  install(Vapi4k) {
    webApplication {
      serverPath = "/talkapp"
      serverSecret = "12345"

      onRequest(ASSISTANT_REQUEST, FUNCTION_CALL, TOOL_CALL) { request ->
        logger.info { "Assistant requests: ${request.requestType} \n${request.toJsonString()}" }
      }

      onAssistantRequest { args ->
        assistant {
          firstMessage = "Hi, I am Beth how can I assist you today?"

          openAIModel {
            modelType = OpenAIModelType.GPT_4_TURBO
            systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."

            functions {
              function(FavoriteFoodService())
            }

            tools {
              serviceTool(WeatherLookupByAreaCodeService())
              manualTool {
                name = "manualWeatherLookup"
                description = "Look up the weather for a city and state"

                parameters {
                  parameter {
                    name = "city"
                    description = "The city to look up"
                  }
                  parameter {
                    name = "state"
                    description = "The state to look up"
                  }
                }

                requestStartMessage {
                  content = "This is the manual weather lookup start message"
                }

                onInvoke { args ->
                  val city = args.stringValue("city")
                  val state = args.stringValue("state")
                  result = "The weather in $city, $state is sunny"
                  requestCompleteMessages {
                    requestCompleteMessage {
                      content = "This is the manual weather lookup complete message"
                    }
                  }

                  requestFailedMessages {
                    requestFailedMessage {
                      content = "This is the manual weather lookup failed message"
                    }
                  }
                }
              }
            }
          }

          elevenLabsVoice {
            voiceIdType = ElevenLabsVoiceIdType.PAULA
            modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2
          }
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

    webApplication {
      serverPath = "/talkSquad"
      // serverSecret = "12345"

      onRequest(ASSISTANT_REQUEST, FUNCTION_CALL, TOOL_CALL) { request ->
        logger.info { "Assistant requests: ${request.requestType} \n${request.toJsonString()}" }
      }

      onAssistantRequest { args ->
        squad {
          members {
            member {
              memberAssistant("Jane")
            }
            member {
              memberAssistant("Sarah")
            }
          }
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

    webApplication {
      serverPath = "/assistantId"

      onAssistantRequest { args ->
        assistantId {
          id = "123-456-789"

          assistantOverrides {
            firstMessage = "This is the first message"

            openAIModel {
              modelType = OpenAIModelType.GPT_4_TURBO
              systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."

              functions {
                function(FavoriteFoodService())
                function(WeatherLookupByAreaCodeService())
              }

              tools {
                serviceTool(WeatherLookupByAreaCodeService())
                manualTooDecl()
              }
            }
          }
        }
      }
    }

    webApplication {
      serverPath = "/squadId"

      onAssistantRequest { args ->
        squadId {
          id = "123-456-789"
        }
      }
    }

    outboundCallApplication {
      serverPath = "/outboundRequest1"

      onRequest(ASSISTANT_REQUEST, FUNCTION_CALL, TOOL_CALL) { request ->
        logger.info { "Assistant requests: ${request.requestType} \n${request.toJsonString()}" }
      }

      onAssistantRequest { args ->
        assistant {
          firstMessage = "Hi, I am Beth how can I assist you today?"

          openAIModel {
            modelType = OpenAIModelType.GPT_4_TURBO
            systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."

            functions {
              function(FavoriteFoodService())
              function(WeatherLookupByAreaCodeService())
            }

            tools {
              serviceTool(WeatherLookupByAreaCodeService())
              manualTooDecl()

              endCallTool {
                name = "endCallTool"
                description = "End the call"

                server {
                  url = "https://ba44-73-71-109-237.ngrok-free.app/emailTool"
                }
              }

              voiceMailTool {
                name = "voiceMailTool"
                description = "Leave a voice mail"

                parameters {
                  parameter {
                    name = "message"
                    description = "The message to leave"
                  }
                }
                requestStartMessage {
                  content = "This is the voicemail start message"
                }
              }
            }
          }

          elevenLabsVoice {
            voiceIdType = ElevenLabsVoiceIdType.PAULA
            modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2
          }
        }
      }
    }

    outboundCallApplication {
      serverPath = "/squadOutbound"

      onRequest(ASSISTANT_REQUEST, FUNCTION_CALL, TOOL_CALL) { request ->
        logger.info { "Assistant requests: ${request.requestType} \n${request.toJsonString()}" }
      }

      onAssistantRequest { args ->
        squad {
          members {
            member {
              squadAssistant(
                "assist1",
                {
                  serviceTool(WeatherLookupByAreaCodeService("[assistant 1]"))
                  manualTooDecl()
                },
                {
                  function(FavoriteFoodService())
                  function(WeatherLookupByAreaCodeService())
                },
              )
            }
            member {
              squadAssistant(
                "assist2",
                {
                  serviceTool(WeatherLookupByAreaCodeService("[assistant 2]"))
                  manualTooDecl()
                },
                {
                  function(FavoriteFoodService())
                  function(WeatherLookupByAreaCodeService())
                },
              )
            }
          }
        }
      }
    }

    outboundCallApplication {
      serverPath = "/squadMemberOverride"

      onRequest(ASSISTANT_REQUEST, FUNCTION_CALL, TOOL_CALL) { request ->
        //  logger.info { "Assistant requests: ${request.requestType} \n${request.toJsonString()}" }
      }

      onAssistantRequest { args ->
        squad {
          members {
            member {
              squadAssistant(
                "assist1",
                {
                  serviceTool(WeatherLookupByAreaCodeService("[assistant 1]"))
                  manualTooDecl()
                },
                {
                  function(FavoriteFoodService())
                  function(WeatherLookupByAreaCodeService())
                },
              )
            }
            member {
              squadAssistant(
                "assist2",
                {
                  serviceTool(WeatherLookupByAreaCodeService("[assistant 2]"))
                  manualTooDecl()
                },
                {
                  function(FavoriteFoodService())
                  function(WeatherLookupByAreaCodeService())
                },
              )
            }
          }
          memberOverrides {
            firstMessage = "This is the first message from memberOverrides"

            this.name = name
            firstMessage = "Hi, I am Beth how can I assist you today?"

            openAIModel {
              modelType = OpenAIModelType.GPT_4_TURBO
              systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."

              functions {
                function(FavoriteFoodService())
                function(WeatherLookupByAreaCodeService())
              }

              tools {
                serviceTool(WeatherLookupByAreaCodeService("[assistant 1]"))
                manualTooDecl()
              }
            }

            elevenLabsVoice {
              voiceIdType = ElevenLabsVoiceIdType.PAULA
              modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2
            }
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
      serverPath = "/talkApp"

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

      onRequest(ServerRequestType.STATUS_UPDATE) { request ->
        logger.info { "Status update: STATUS_UPDATE" }
      }

      onRequest(ServerRequestType.STATUS_UPDATE) { request ->
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

private fun Member.memberAssistant(name: String) {
  assistant {
    this.name = name
    firstMessage = "Hi, I am $name how can I assist you today?"

    openAIModel {
      modelType = OpenAIModelType.GPT_4_TURBO
      systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."

      functions {
        function(FavoriteFoodService())
      }

      tools {
        serviceTool(WeatherLookupService1())
        serviceTool(WeatherLookupByAreaCodeService())
        manualTool {
          this.name = "manualWeatherLookup"
          description = "Look up the weather for a city and state"

          parameters {
            parameter {
              this.name = "city"
              description = "The city to look up"
            }
            parameter {
              this.name = "state"
              description = "The state to look up"
            }
          }

          requestStartMessage {
            content = "This is the manual weather lookup start message"
          }

          onInvoke { args ->
            val city = args.stringValue("city")
            val state = args.stringValue("state")
            result = "The weather in $city, $state is sunny"
            requestCompleteMessages {
              requestCompleteMessage {
                content = "This is the manual weather lookup complete message"
              }
            }

            requestFailedMessages {
              requestFailedMessage {
                content = "This is the manual weather lookup failed message"
              }
            }
          }
        }
      }
    }

    elevenLabsVoice {
      voiceIdType = ElevenLabsVoiceIdType.PAULA
      modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2
    }
  }
}

private fun Member.squadAssistant(
  name: String,
  toolsBlock: Tools.() -> Unit,
  functionsBlock: Functions.() -> Unit,
) {
  assistant {
    this.name = name
    firstMessage = "Hi, I am Beth how can I assist you today?"

    openAIModel {
      modelType = OpenAIModelType.GPT_4_TURBO
      systemMessage = "You're a versatile AI assistant named Vapi who is fun to talk with."

      functions {
        functionsBlock()
      }

      tools {
        toolsBlock()
      }
    }

    elevenLabsVoice {
      voiceIdType = ElevenLabsVoiceIdType.PAULA
      modelType = ElevenLabsVoiceModelType.ELEVEN_TURBO_V2
    }
  }
}

private fun Tools.manualTooDecl() {
  manualTool {
    name = "manualWeatherLookup"
    description = "Look up the weather for a city and state"

    parameters {
      parameter {
        name = "city"
        description = "The city to look up"
      }
      parameter {
        name = "state"
        description = "The state to look up"
      }
    }

    requestStartMessage {
      content = "This is the manual weather lookup start message"
    }

    onInvoke { args ->
      println(args.toJsonString())
      val city = args.stringValue("city")
      val state = args.stringValue("state")
      result = "The weather in $city, $state is sunny"
      requestCompleteMessages {
        requestCompleteMessage {
          content = "This is the manual weather lookup complete message"
        }
      }

      requestFailedMessages {
        requestFailedMessage {
          content = "This is the manual weather lookup failed message"
        }
      }
    }
  }
}
