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

package com.vapi4k.dsl.model

import com.vapi4k.common.SessionId
import com.vapi4k.dsl.model.enums.MessageRoleType
import com.vapi4k.dsl.tools.Functions
import com.vapi4k.dsl.tools.FunctionsImpl
import com.vapi4k.dsl.tools.Tools
import com.vapi4k.dsl.tools.ToolsImpl
import com.vapi4k.dtos.assistant.model.CommonModelDto
import com.vapi4k.dtos.assistant.model.KnowledgeBaseDto
import com.vapi4k.utils.JsonElementUtils.messageCallId
import com.vapi4k.utils.ReflectionUtils.trimLeadingSpaces
import kotlinx.serialization.json.JsonElement

abstract class AbstractModel(
  internal val request: JsonElement,
  override val sessionId: SessionId,
  private val dto: CommonModelDto,
) : com.vapi4k.dsl.model.ModelMessageProperties {
  override val messages get() = dto.messages
  override val toolDtos get() = dto.tools
  override val functionDtos get() = dto.functions
  override val messageCallId get() = request.messageCallId

  var systemMessage by com.vapi4k.dsl.model.ModelMessageDelegate(MessageRoleType.SYSTEM)
  var assistantMessage by com.vapi4k.dsl.model.ModelMessageDelegate(MessageRoleType.ASSISTANT)
  var functionMessage by com.vapi4k.dsl.model.ModelMessageDelegate(MessageRoleType.FUNCTION)
  var toolMessage by com.vapi4k.dsl.model.ModelMessageDelegate(MessageRoleType.TOOL)
  var userMessage by com.vapi4k.dsl.model.ModelMessageDelegate(MessageRoleType.USER)

  fun tools(block: Tools.() -> Unit): Tools = ToolsImpl(this).apply(block)
  fun functions(block: Functions.() -> Unit): Functions = FunctionsImpl(this).apply(block)

  fun knowledgeBase(block: com.vapi4k.dsl.model.KnowledgeBase.() -> Unit): com.vapi4k.dsl.model.KnowledgeBase {
    val kbDto = KnowledgeBaseDto().also { dto.knowledgeBaseDto = it }
    return com.vapi4k.dsl.model.KnowledgeBase(request, kbDto)
      .apply(block)
      .apply {
        if (kbDto.fileIds.isEmpty())
          error("knowledgeBase{} must have at least one file")
      }
  }

  override fun message(
    role: MessageRoleType,
    content: String,
  ) {
    // Remove any existing messages with the same role
    messages.removeAll { it.role == role.desc }
    // Use trimLeadingSpaces() instead of trimIndent() because trimIndent() doesn't work with += operator
    messages += com.vapi4k.dtos.assistant.RoleMessage(role.desc, content.trimLeadingSpaces())
  }
}
