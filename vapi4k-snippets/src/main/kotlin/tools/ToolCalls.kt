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

package tools

import com.vapi4k.api.tools.Param
import com.vapi4k.api.tools.ToolCall
import kotlin.math.absoluteValue

object ToolCalls {
  class AddTwoNumbers {
    fun addTwoNumbers(
      a: Int,
      b: Int,
    ): Int {
      return a + b
    }
  }

  class MultiplyTwoNumbers {
    @ToolCall("Multiply two numbers")
    fun add(
      @Param("First number to multiply")
      a: Int,
      @Param("Second number to Multiply")
      b: Int,
    ): Int {
      return a + b
    }
  }

  object AbsoluteValue {
    @ToolCall("Absolute value of a number")
    fun absolute(
      @Param("Number")
      a: Int,
    ): Int {
      return a.absoluteValue
    }
  }
}
