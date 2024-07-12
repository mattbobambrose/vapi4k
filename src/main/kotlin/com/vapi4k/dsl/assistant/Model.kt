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

package com.vapi4k.dsl.assistant

import com.vapi4k.common.Utils.ensureEndsWith
import com.vapi4k.common.Utils.trimLeadingSpaces
import com.vapi4k.enums.MessageRoleType
import com.vapi4k.responses.assistant.ModelDto
import com.vapi4k.responses.assistant.RoleMessage

interface ModelUnion {
  var provider: String
  var model: String
  var temperature: Int
  var maxTokens: Int
  var emotionRecognitionEnabled: Boolean
}

@AssistantDslMarker
data class Model(
  internal val assistant: Assistant,
  internal val modelDto: ModelDto,
) : ModelUnion by modelDto {
  internal val config get() = assistant.config

  private fun getMessageValue(roleType: MessageRoleType) =
    with(modelDto.messages.filter { it.role == roleType.roleValue }) {
      if (isEmpty()) "" else (joinToString("") { it.content }).ensureEndsWith(" ")
    }

  var assistantMessage: String
    get() = getMessageValue(MessageRoleType.ASSISTANT)
    set(content) = modelDto.message(MessageRoleType.ASSISTANT, content)

  var functionMessage: String
    get() = getMessageValue(MessageRoleType.FUNCTION)
    set(content) = modelDto.message(MessageRoleType.FUNCTION, content)

  var systemMessage: String
    get() = getMessageValue(MessageRoleType.SYSTEM)
    set(content) = modelDto.message(MessageRoleType.SYSTEM, content)

  var toolMessage: String
    get() = getMessageValue(MessageRoleType.TOOL)
    set(content) = modelDto.message(MessageRoleType.TOOL, content)

  var userMessage: String
    get() = getMessageValue(MessageRoleType.USER)
    set(content) = modelDto.message(MessageRoleType.USER, content)

  fun tools(block: Tools.() -> Unit) {
    Tools(this).apply(block)
  }

  fun functions(block: Functions.() -> Unit) {
    Functions(this).apply(block)
  }

  companion object {
    private fun ModelDto.message(
      role: MessageRoleType,
      content: String,
    ) {
      // Remove any existing messages with the same role
      messages.removeAll { it.role == role.roleValue }
      // We are using trimLeadingSpaces() instead of trimIndent() because trimIndent() doesn't work with += operator
      messages += RoleMessage(role.roleValue, content.trimLeadingSpaces())
    }
  }
}
