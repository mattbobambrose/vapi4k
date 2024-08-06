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

import com.vapi4k.AssistantTest.Companion.newRequestContext
import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.stringValue
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonElement
import com.vapi4k.utils.assistantResponse
import com.vapi4k.utils.tools
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelTests {
  @Test
  fun `tool server details`() {
    val response =
      assistantResponse(newRequestContext()) {
        assistant {
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

            tools {
              dtmf(WeatherLookupService1()) {
                server {
                  url = "zzz"
                  secret = "123"
                  timeoutSeconds = 10
                }
              }
            }
          }
        }
      }

    val je = response.toJsonElement()
    val server = je.tools().first().jsonObject.get("server") ?: error("server not found")
    assertEquals("zzz", server.stringValue("url"))
    assertEquals("123", server.stringValue("secret"))
    assertEquals("10", server.stringValue("timeoutSeconds"))
  }

  @Test
  fun `duplicate server decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(newRequestContext()) {
        assistant {
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

            tools {
              dtmf(WeatherLookupService1()) {
                server {
                  url = "zzz"
                  secret = "123"
                  timeoutSeconds = 10
                }
                server {
                  url = "yyy"
                  secret = "456"
                  timeoutSeconds = 5
                }
              }
            }
          }
        }
      }
    }.also {
      assertEquals(
        "tool{} already has a server{} decl",
        it.message,
      )
    }
  }
}
