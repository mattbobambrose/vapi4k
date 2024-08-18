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

import com.vapi4k.api.prompt.Prompt.Companion.prompt
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest {
  @Test
  fun `unaryPlus single test`() {
    val str =
      prompt {
        +"test text"
      }

    assertEquals("test text\n", str)
  }

  @Test
  fun `unaryPlus multi test`() {
    val str =
      prompt {
        +"test text"
        +"test text"
      }

    val goal =
      """test text
test text
"""

    assertEquals(goal, str)
  }

  @Test
  fun `singleLine test`() {
    val str =
      prompt {
        singleLine(
          """Welcome
         team
         later
      """
        )
      }

    assertEquals("Welcome team later", str)
  }

  @Test
  fun `trimPrefix single line test`() {
    val str =
      prompt {
        trimPrefix("    test text")
      }

    assertEquals("test text", str)
  }

  @Test
  fun `trimPrefix multi line test`() {
    val str =
      prompt {
        trimPrefix(
          """Welcome
         team
         later"""
        )
      }
    val goal = """Welcome
team
later"""
    assertEquals(goal, str)
  }


}
