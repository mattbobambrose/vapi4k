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

package com.vapi4k.utils

import com.vapi4k.utils.JsonElementUtils.isFunctionCall
import com.vapi4k.utils.JsonElementUtils.isToolCall
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.jsonList
import com.vapi4k.utils.JsonUtils.stringValue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

internal object Utils {
  fun String.ensureStartsWith(s: String) = if (startsWith(s)) this else s + this
  fun String.ensureEndsWith(s: String) = if (endsWith(s)) this else this + s
  fun String.trimLeadingSpaces() = lines().joinToString(separator = "\n") { it.trimStart() }

  fun <T> lambda(block: T) = block

  val JsonElement.functionName
    get() = if (isFunctionCall) this["message.functionCall.name"].stringValue else error("JsonElement is not a function call")

  val JsonElement.functionParameters
    get() = if (isFunctionCall) this["message.functionCall.parameters"] else error("JsonElement is not a function call")

  val JsonElement.toolCallId
    get() = if (isToolCall) this["id"].stringValue else error("JsonElement is not a tool call")

  val JsonElement.toolCallList
    get() = if (isToolCall) this["message.toolCallList"].jsonList else error("JsonElement is not a tool call")

  val JsonElement.toolCallName
    get() = if (isToolCall) this["function.name"].stringValue else error("JsonElement is not a tool call")

  val JsonElement.toolCallArguments
    get() = if (isToolCall) this["function.arguments"] else error("JsonElement is not a tool call")

  val JsonElement.assistantClientMessages get() = this["assistant.clientMessages"].jsonArray

  val JsonElement.assistantServerMessages get() = this["assistant.serverMessages"].jsonArray

}
