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

package com.vapi4k.dsl.functions

import com.vapi4k.common.AssistantCacheId
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.toolCallAnnotation
import kotlin.reflect.KFunction

class ToolCallInfo(
  private val assistantCacheId: AssistantCacheId,
  private val function: KFunction<*>,
) {
  private val toolCall get() = function.toolCallAnnotation!!
  private val toolHasName get() = toolCall.name.isNotEmpty()
  private val toolHasDescription get() = toolCall.description.isNotEmpty()
  private val cacheName get() = if (toolHasName) toolCall.name else function.name

  val llmName get() = "${cacheName}_$assistantCacheId"

  val llmDescription
    get() =
      when {
        toolHasDescription -> toolCall.description
        toolHasName -> toolCall.name
        else -> function.name
      }

  val llmReturnType
    get() = when (function.returnType.asKClass()) {
      String::class -> "string"
      Int::class -> "integer"
      Double::class -> "double"
      Boolean::class -> "boolean"
      else -> "object"
    }
}
