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

import com.vapi4k.dsl.call.VapiApiImpl
import com.vapi4k.dsl.call.VapiApiImpl.Companion.vapiApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.Test

class ApiCalls {
  @Test
  fun `multiple Assistant blocks`() {
    val api = vapiApi("123-445-666") as VapiApiImpl
    assertThrows(IllegalStateException::class.java) {
      api.test {
        outboundCall {
//          assistantId {
//            id = "123-445-666"
//          }
//          assistantId {
//            id = "345-445-666"
//          }
        }
      }
    }.also {
      assertEquals("assistantId{} already called", it.message)
    }
  }

  @Test
  fun `multiple AssistantId blocks`() {
    val api = vapiApi("123-445-666") as VapiApiImpl
    assertThrows(IllegalStateException::class.java) {
      api.test {
        outboundCall {
//          assistant {
//            firstMessage = "Hi there. I am here to help."
//          }
//          assistant {
//            firstMessage = "Hi there. I am here to help."
//          }
        }
      }
    }.also {
      assertEquals("An assistant{} requires a model{} decl", it.message)
    }
  }

  @Test
  fun `combination of Assistant and AssistantId blocks`() {
    val api = vapiApi("123-445-666") as VapiApiImpl
    assertThrows(IllegalStateException::class.java) {
      api.test {
        outboundCall {
//          assistantId {
//            id = "123-445-666"
//          }
//          assistant {
//            firstMessage = "Hi there. I am here to help."
//          }
        }
      }
    }.also {
      assertEquals("assistantId{} already called", it.message)
    }
  }

  @Test
  fun `multiple AssistantOverrides blocks`() {
    val api = vapiApi("123-445-666") as VapiApiImpl
    assertThrows(IllegalStateException::class.java) {
      api.test {
        outboundCall {
//          assistantOverrides {
//            firstMessage = "Hi there. I am here to help."
//          }
//          assistantOverrides {
//            firstMessage = "Hi there. I am here to help."
//          }
        }
      }
    }.also {
      assertEquals("assistant{} or assistantId{} must be called before assistantOverrides{}", it.message)
    }
  }

  @Test
  fun `declare AssistantOverrides without an Assistant or AssistantId Decl`() {
    val api = vapiApi("123-445-666") as VapiApiImpl
    assertThrows(IllegalStateException::class.java) {
      api.test {
        outboundCall {
//          assistantOverrides {
//            firstMessage = "Hi there. I am here to help."
//          }
        }
      }
    }.also {
      assertEquals("assistant{} or assistantId{} must be called before assistantOverrides{}", it.message)
    }
  }
}
