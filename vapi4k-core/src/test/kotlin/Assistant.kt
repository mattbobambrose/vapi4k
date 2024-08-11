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

import com.vapi4k.WeatherLookupService0
import com.vapi4k.WeatherLookupService1
import com.vapi4k.WeatherLookupService2
import com.vapi4k.WeatherLookupService3
import com.vapi4k.api.assistant.AssistantResponse
import com.vapi4k.api.assistant.enums.AssistantServerMessageType
import com.vapi4k.api.conditions.eq
import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.phoneNumber
import kotlinx.serialization.json.JsonElement

fun AssistantResponse.myAssistantRequest(request: JsonElement) =
  when (request.phoneNumber) {
    "+14156721042" ->
      assistantId {
        id = "44792a91-d7f9-4915-9445-0991aeef97bc"

        assistantOverrides {
          firstMessage = "This is the first message override"

          openAIModel {
            modelType = OpenAIModelType.GPT_4_TURBO
          }
        }
      }

    else -> getAssistant("")
  }

fun AssistantResponse.getSquad(request: JsonElement) =
  squad {
    name = "Squad Name"
    members {
      member {
        assistantId {
          id = "paul"
        }
        assistant {
          name = "Assistant Name"
          openAIModel {
            modelType = OpenAIModelType.GPT_4_TURBO
          }
        }
      }
      member {
        assistant {
          name = "Assistant Name"
          openAIModel {
            modelType = OpenAIModelType.GPT_4_TURBO
          }
        }
      }
    }
  }

fun AssistantResponse.getAssistant(callerName: String = "Bill") =
  assistant {
    assistantOverrides {
      firstMessage = "This is the first message override"
    }
    firstMessage =
      """
            Hi there. My name is Ellen. I'd like to collect some information from you
            today. Is that alright?
            """
    serverMessages -= setOf(
      AssistantServerMessageType.CONVERSATION_UPDATE,
      AssistantServerMessageType.SPEECH_UPDATE,
    )

    openAIModel {
      modelType = OpenAIModelType.GPT_4_TURBO

      systemMessage = """
            [Identity]
            You are the friendly and helpful voice of EO Care. Your goal is to collect the name of
            the user.

            [Style]
            - Be friendly and concise.

            [Response Guideline]
            - Present dates in a clear format (e.g., January 15, 2024).

            [Task]
            1. Ask for the user's first and last name. If they only give one, ask which name that was
            and ask for the other as well.
            <wait for user response>
            2. Confirm the user's name.
            3. Ask for the user's age, favorite color, and favorite food.
            <wait for user response>
            4. If they do not give all three, ask for the missing information. Keep asking until
            they give all of the information.
        """

      functions {
        function(WeatherLookupService0())
        function(WeatherLookupService3())
      }

//      tools {
//        tool(NameService())
//      }
//
//      functions {
//        function(ManyInfoService())
//      }

      tools {
        serviceTool(WeatherLookupService1()) {
          condition("city" eq "Chicago", "state" eq "Illinois") {
            requestStartMessage {
              content = "This is the Chicago Illinois start message"
            }
            requestCompleteMessage {
              content = "This is the Chicago Illinois complete message"
            }
            requestFailedMessage {
              content = "This is the Chicago Illinois failed message"
            }

            requestDelayedMessage {
              content = "This is the Chicago Illinois delayed message"
              timingMilliseconds = 2000
            }
          }
          requestStartMessage {
            content = "This is the default start message"
          }
          requestCompleteMessage {
            content = "This is the default complete message"
          }
          requestFailedMessage {
            content = "This is the default failed message"
          }
          requestDelayedMessage {
            content = "This is the default delayed message"
            timingMilliseconds = 1000
          }

          condition("city" eq "Chicago") {
            requestStartMessage {
              content = "This is the Chicago start message"
            }
          }
          condition("city" eq "Houston") {
            requestStartMessage {
              content = "This is the Houston start message"
            }
          }
        }

        serviceTool(WeatherLookupService2()) {
          condition("city" eq "Chicago") {
            requestStartMessage {
              content = "This is the Chicago start message"
            }
          }
          condition("city" eq "Houston") {
            requestStartMessage {
              content = "This is the Houston start message"
            }
          }
          requestCompleteMessage {
            content = "This is the default complete message"
          }
          requestFailedMessage {
            content = "This is the default failed message"
          }
          requestDelayedMessage {
            content = "This is the default delayed message"
            timingMilliseconds = 1000
          }
        }
      }
    }
  }
