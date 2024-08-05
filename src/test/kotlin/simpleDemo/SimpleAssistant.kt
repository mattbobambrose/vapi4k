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

package simpleDemo
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

import com.vapi4k.dsl.assistant.AssistantDsl.assistantResponse
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.dsl.model.enums.OpenAIModelType
import com.vapi4k.dsl.tools.enums.ToolMessageRoleType
import com.vapi4k.dsl.toolservice.ToolCallService
import com.vapi4k.dsl.vapi4k.RequestContext
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
  fun simpleAssistantRequest(requestContext: RequestContext) =
    assistantResponse(requestContext) {
      assistant {
        firstMessage = "Hi there!"

        openAIModel {
          modelType = OpenAIModelType.GPT_4_TURBO
          systemMessage = "You're a weather lookup service"

          tools {
            tool(WeatherLookupService1()) {
              requestStartMessage {
                content = "Default request start weather lookup"
              }
            }

            tool(WeatherLookupService2(), WeatherLookupService2::getWeatherByCity2) {
              requestStartMessage {
                content = "Default request start weather lookup"
              }
            }

            tool(
              WeatherLookupService2(),
              // WeatherLookupService2::getWeatherByCity2,
              WeatherLookupService2::getWeatherByZipCode,
            ) {
              requestStartMessage {
                content = "Default request start weather lookup"
              }
            }
          }
        }

        serverMessages -= setOf(
          AssistantServerMessageType.CONVERSATION_UPDATE,
          AssistantServerMessageType.SPEECH_UPDATE,
        )
      }
    }

  class WeatherLookupService1 : ToolCallService() {
    @ToolCall("Look up the weather for a city")
    fun getWeatherByCity1(
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

  class WeatherLookupService2 : ToolCallService() {
    @ToolCall("Look up the weather for a city")
    fun getWeatherByCity2(
      city: String,
      state: String,
    ) = "The weather in city $city and state $state is windy"

    @ToolCall("Look up the weather for a zip code")
    fun getWeatherByZipCode(zipCode: String) = "The weather in zip code $zipCode is rainy"

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
