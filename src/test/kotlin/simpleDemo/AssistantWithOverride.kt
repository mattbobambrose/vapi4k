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

import com.vapi4k.dsl.assistant.AssistantDsl
import com.vapi4k.dsl.assistant.AssistantDsl.assistantResponse
import com.vapi4k.dsl.model.enums.GroqModelType
import com.vapi4k.dsl.model.enums.OpenAIModelType
import com.vapi4k.dsl.vapi4k.RequestContext
import simpleDemo.Coasts.EAST
import simpleDemo.Coasts.WEST

class AssistantWithOverrides {
  fun getAssistantWithOverride(requestContext: RequestContext) =
    AssistantDsl.assistantResponse(requestContext) {
      assistantResponse(requestContext) {
        assistant {
          name = "assistant1"
          firstMessage = "Hi there! I'm assistant1"

          groqModel {
            modelType = GroqModelType.LLAMA3_70B
            tools {
              tool(TimeLookupService(EAST))
            }
          }

          assistantOverrides {
            name = "Jeremy"
            firstMessage = "Hi there! I'm Jeremy"
            openAIModel {
              modelType = OpenAIModelType.GPT_4_TURBO
              tools {
                tool(TimeLookupService(WEST))
                tool(WeatherLookupService())
              }
            }
          }
        }
      }
    }
}

enum class Coasts {
  EAST,
  WEST,
}
