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

import com.vapi4k.common.EnvVar.DEFAULT_SERVER_PATH
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.utils.Utils.dropLeading
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import kotlin.test.Test

class ApplicationTest {
  @Test
  fun `test for serverPath and serverSecret`() {
    val str = "/something_else"
    val application =
      with(Vapi4kConfigImpl()) {
        vapi4kApplication {
          serverPath = str
          serverSecret = "12345"
        }
      }
    assertEquals(str, application.serverPath)
    assertEquals("12345", application.serverSecret)
  }

  @Test
  fun `test for default serverPath`() {
    val application =
      with(Vapi4kConfigImpl()) {
        vapi4kApplication {
        }
      }
    assertEquals(DEFAULT_SERVER_PATH.value.dropLeading("/"), application.serverPath)
    assertEquals("", application.serverSecret)
  }

  @Test
  fun `test for duplicate default serverPaths`() {
    val str = "/something_else"
    assertThrows(IllegalStateException::class.java) {
      val application =
        with(Vapi4kConfigImpl()) {
          vapi4kApplication {
          }
          vapi4kApplication {
          }
        }
    }.also {
      assertTrue(it.message.orEmpty().contains("already exists"))
    }
  }

  @Test
  fun `test for duplicate serverPaths`() {
    val str = "/something_else"
    assertThrows(IllegalStateException::class.java) {
      val application =
        with(Vapi4kConfigImpl()) {
          vapi4kApplication {
            serverPath = str
          }
          vapi4kApplication {
            serverPath = str
          }
        }
    }.also {
      assertTrue(it.message.orEmpty().contains("already exists"))
    }
  }
}
