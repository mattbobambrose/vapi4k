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

import com.vapi4k.AssistantDslMarker
import com.vapi4k.dsl.assistant.enums.MessageRoleType
import com.vapi4k.responses.assistant.ModelDto
import com.vapi4k.responses.assistant.RoleMessage
import com.vapi4k.utils.Utils.trimLeadingSpaces
import kotlin.reflect.KProperty

interface ModelUnion {
  var provider: String
  var model: String
  var temperature: Int
  var maxTokens: Int
  var emotionRecognitionEnabled: Boolean
}

@AssistantDslMarker
data class Model internal constructor(
  internal val assistant: Assistant,
  internal val modelDto: ModelDto,
) : ModelUnion by modelDto {
  private val messages get() = modelDto.messages
  internal val tools get() = modelDto.tools
  internal val functions get() = modelDto.functions

  var systemMessage by MessageDelegate(MessageRoleType.SYSTEM)
  var assistantMessage by MessageDelegate(MessageRoleType.ASSISTANT)
  var functionMessage by MessageDelegate(MessageRoleType.FUNCTION)
  var toolMessage by MessageDelegate(MessageRoleType.TOOL)
  var userMessage by MessageDelegate(MessageRoleType.USER)

  fun tools(block: Tools.() -> Unit) {
    Tools(this).apply(block)
  }

  fun functions(block: Functions.() -> Unit) {
    Functions(this).apply(block)
  }

  companion object {
    private class MessageDelegate(val messageRoleType: MessageRoleType) {
      operator fun getValue(
        model: Model,
        property: KProperty<*>,
      ): String {
        val msgs = model.messages.filter { it.role == messageRoleType.desc }
        return if (msgs.isEmpty()) "" else (msgs.joinToString("") { it.content })
      }

      operator fun setValue(
        model: Model,
        property: KProperty<*>,
        newVal: String,
      ) = model.message(messageRoleType, newVal)
    }

    private fun Model.message(
      role: MessageRoleType,
      content: String,
    ) {
      // Remove any existing messages with the same role
      messages.removeAll { it.role == role.desc }
      // Use trimLeadingSpaces() instead of trimIndent() because trimIndent() doesn't work with += operator
      messages += RoleMessage(role.desc, content.trimLeadingSpaces())
    }
  }
}
