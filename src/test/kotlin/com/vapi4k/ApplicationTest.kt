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
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.utils.Utils.dropLeading
import org.junit.Assert.assertEquals
import kotlin.test.Test

class ApplicationTest {

  @Test
  fun `test for serverPath and serverSecret`() {
    val str = "/something_else"
    val config = Vapi4kConfig()
    val application =
      with(config) {
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
    val config = Vapi4kConfig()
    val application =
      with(config) {
        vapi4kApplication {
        }
      }
    assertEquals(DEFAULT_SERVER_PATH.value.dropLeading("/"), application.serverPath)
  }
}
