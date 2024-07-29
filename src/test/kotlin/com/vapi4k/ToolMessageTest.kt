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

package com.vapi4k

import com.vapi4k.dsl.assistant.AssistantDsl.squad
import com.vapi4k.dsl.model.enums.OpenAIModelType
import com.vapi4k.utils.JsonFilenames
import com.vapi4k.utils.TestUtils.withTestApplication
import com.vapi4k.utils.get
import com.vapi4k.utils.stringValue
import kotlinx.serialization.json.jsonArray
import org.junit.Test
import kotlin.test.assertEquals

class ToolMessageTest {
  @Test
  fun `toolMessageStart test`() {
    val (response, jsonElement) = withTestApplication(JsonFilenames.JSON_ASSISTANT_REQUEST) { request ->
      squad(request) {
        members {
          member {
            assistant {
              name = "assistant 1"
              firstMessage = "I'm assistant 1"

              openAIModel {
                modelType = OpenAIModelType.GPT_4_TURBO
                tools {
                  tool("tool 1") {
                    requestStartMessage {
                      content = "tool 1 start message"
                    }
                    requestCompleteMessage {
                      content = "tool 1 complete message"
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    assertEquals(200, response.status.value)
    val assistantTools = jsonElement["data"]["assistant"]["tools"].jsonArray.toList()
    assertEquals(
      "tool 1 start message",
      assistantTools[0]["toolMessages"]["requestStartMessage"]["content"].stringValue
    )
  }
}
