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

import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.dsl.tools.enums.ToolMessageRoleType
import com.vapi4k.dsl.toolservice.ToolCallService
import kotlinx.serialization.json.JsonElement

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

object SimpleAssistant {
  fun simpleAssistantRequest(request: JsonElement) =
    assistant(request) {
      firstMessage = "Hi there!"

      openAIModel {
        // modelType = OpenAIModelType.GPT_4_TURBO
        systemMessage = "You're a weather lookup service"

        tools {
          tool(ADWeatherLookupService()) {
            requestStartMessage {
              content = "Default request start weather lookup"
            }
//                    requestCompleteMessage {
//                        content = "Default request complete weather lookup"
//                    }
//                    condition("city" eq "Chicago", "state" eq "Illinois") {
//                        requestCompleteMessage {
//                            content = "Default Chicago request complete weather lookup"
//                        }
//                    }
          }
        }
      }

      serverMessages -= setOf(
        AssistantServerMessageType.CONVERSATION_UPDATE,
        AssistantServerMessageType.SPEECH_UPDATE,
      )
    }

  class ADWeatherLookupService : ToolCallService() {
    @ToolCall("Look up the weather for a city")
    fun getWeatherByCity(
      city: String,
      state: String,
    ) = "The weather in city $city and state $state is windy"

    override fun onToolCallComplete(
      toolCallRequest: JsonElement,
      result: String,
    ) = requestCompleteMessages {
      condition("city" eq "Chicago", "state" eq "Illinois") {
        requestCompleteMessage {
          content = "The Chicago override request complete weather lookup"
        }
      }
      requestCompleteMessage {
        role = ToolMessageRoleType.ASSISTANT
        content = "The override request Complete Message weather has arrived"
      }
    }
  }
}
