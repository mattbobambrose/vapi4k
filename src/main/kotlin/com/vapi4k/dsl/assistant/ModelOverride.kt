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
import com.vapi4k.utils.JsonElementUtils.messageCallId
import com.vapi4k.utils.Utils.trimLeadingSpaces

@AssistantDslMarker
data class ModelOverride internal constructor(
  internal val assistant: AssistantOverrides,
  internal val modelDto: ModelDto,
) : AbstractModel, AbstractModelDelegate, ModelUnion by modelDto {
  override val messages get() = modelDto.messages
  override val toolDtos get() = modelDto.tools
  override val functions get() = modelDto.functions
  override val messageCallId get() = assistant.request.messageCallId

  var systemMessage by ModelMessageDelegate(MessageRoleType.SYSTEM)
  var assistantMessage by ModelMessageDelegate(MessageRoleType.ASSISTANT)
  var functionMessage by ModelMessageDelegate(MessageRoleType.FUNCTION)
  var toolMessage by ModelMessageDelegate(MessageRoleType.TOOL)
  var userMessage by ModelMessageDelegate(MessageRoleType.USER)

  fun tools(block: Tools.() -> Unit) {
    Tools(this).apply(block)
  }

  fun functions(block: Functions.() -> Unit) {
    Functions(this).apply(block)
  }

  override fun message(role: MessageRoleType, content: String) {
    // Remove any existing messages with the same role
    messages.removeAll { it.role == role.desc }
    // Use trimLeadingSpaces() instead of trimIndent() because trimIndent() doesn't work with += operator
    messages += RoleMessage(role.desc, content.trimLeadingSpaces())
  }
}
