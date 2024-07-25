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

import com.vapi4k.dsl.assistant.api.VapiApi.Companion.vapiApi
import com.vapi4k.dsl.assistant.model.enums.OpenAIModelType
import com.vapi4k.utils.HttpUtils.bodyAsJsonElement
import org.junit.Assert.assertThrows
import org.junit.Test

class ApiCalls {

  @Test
  fun `multiple Assistant Decls`() {
    val api = vapiApi("123-445-666")
    assertThrows(IllegalStateException::class.java) {
      api.test {
        call {
          assistantId {
            id = "123-445-666"
          }
          assistantId {
            id = "345-445-666"
          }
        }
      }
    }
  }

  @Test
  fun `multiple AssistantId Decls`() {
    val api = vapiApi("123-445-666")
    assertThrows(IllegalStateException::class.java) {
      api.test {
        call {
          assistant {
            firstMessage = "Hi there. I am here to help."
          }
          assistant {
            firstMessage = "Hi there. I am here to help."
          }
        }
      }
    }
  }

  @Test
  fun `combination of Assistant and AssistantId Decls`() {
    val api = vapiApi("123-445-666")
    assertThrows(IllegalStateException::class.java) {
      api.test {
        call {
          assistantId {
            id = "123-445-666"
          }
          assistant {
            firstMessage = "Hi there. I am here to help."
          }
        }
      }
    }
  }

  @Test
  fun `multiple AssistantOverrides Decls`() {
    val api = vapiApi("123-445-666")
    assertThrows(IllegalStateException::class.java) {
      api.test {
        call {
          assistantOverrides {
            firstMessage = "Hi there. I am here to help."
          }
          assistantOverrides {
            firstMessage = "Hi there. I am here to help."
          }
        }
      }
    }
  }

  @Test
  fun `declare AssistantOverrides without an Assistant or AssistantId Decl`() {
    val api = vapiApi("123-445-666")
    assertThrows(IllegalStateException::class.java) {
      api.test {
        call {
          assistantOverrides {
            firstMessage = "Hi there. I am here to help."
          }
        }
      }
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val api = vapiApi("123-445-666")
      val callResp =
        api.phone {
          call {
            assistant {
              firstMessage = "Hi there. I am here to help."
              openAIModel {
                modelType = OpenAIModelType.GPT_4_TURBO
                systemMessage = "Answer questions."
              }
            }

            customer {
              number = "+14156721042"
            }

            phoneNumberId = api.config.property("phoneNumberId").getString()
          }
        }
      println("Call status: ${callResp.status}")
      println("Call response:> ${callResp.bodyAsJsonElement()}")


//    val listResp = api.list(ASSISTANTS)
//    println("List response: ${listResp.jsonElement}")


//
//
//    val saveResp =
//      api.save {
//        call {}
//      }
//
//    val je = saveResp.jsonElement
//
//    val delResp = api.delete("123-445-666")
//
////
////    api.create(assistant)
////
////    api.create {
////      assistant {
////
////      }
////    }
////
////    api.list(ASSISTANT)
////    api.delete(ASSISTANT, "123-445-666")
////
    }
  }

}
