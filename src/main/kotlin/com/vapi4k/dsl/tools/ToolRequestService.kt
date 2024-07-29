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

package com.vapi4k.dsl.tools

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.vapi4k.enums.ToolCallMessageType
import com.vapi4k.dsl.vapi4k.enums.ToolCallMessageType.REQUEST_COMPLETE
import com.vapi4k.dsl.vapi4k.enums.ToolCallMessageType.REQUEST_FAILED
import com.vapi4k.dsl.vapi4k.enums.ToolCallRoleType
import com.vapi4k.dtos.model.ToolMessageConditionDto
import com.vapi4k.responses.ToolCallMessageDto
import kotlinx.serialization.json.JsonElement

abstract class ToolRequestService {
  open fun onToolRequestComplete(
    toolCallRequest: JsonElement,
    result: String,
  ) = ToolRequestComplete()

  open fun onToolRequestFailed(
    toolCallRequest: JsonElement,
    errorMessage: String,
  ) = ToolRequestFailed()
}

@AssistantDslMarker
class RequestCompleteCondition internal constructor() {
  var role = ToolCallRoleType.ASSISTANT
  var requestCompleteMessage = ""
}

@AssistantDslMarker
class RequestFailedCondition internal constructor() {
  var requestFailedMessage = ""
}

abstract class AbstractToolRequest {
  internal val messages = mutableListOf<ToolCallMessageDto>()

  protected fun addToolCallMessage(
    type: ToolCallMessageType,
    content: String,
    conditions: Set<ToolMessageConditionDto> = emptySet(),
  ): ToolCallMessageDto =
    ToolCallMessageDto()
      .apply {
        this.type = type
        this.content = content
        if (conditions.isNotEmpty())
          this.conditions.addAll(conditions)
      }
      .apply { messages += this }
}

class ToolRequestComplete internal constructor() : AbstractToolRequest() {
  var role = ToolCallRoleType.ASSISTANT
  var requestCompleteMessage = ""

  fun condition(
    requiredConditionDto: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: RequestCompleteCondition.() -> Unit,
  ) = RequestCompleteCondition()
    .apply(block)
    .also { rcc ->
      if (requestCompleteMessage.isNotEmpty()) {
        val cond = mutableSetOf(requiredConditionDto).apply { addAll(additional.toSet()) }
        addToolCallMessage(REQUEST_COMPLETE, rcc.requestCompleteMessage, cond).apply { role = rcc.role }
      }
    }

  companion object {
    fun toolRequestComplete(block: ToolRequestComplete.() -> Unit) =
      ToolRequestComplete()
        .apply(block)
        .also { rc ->
          if (rc.requestCompleteMessage.isNotEmpty())
            rc.addToolCallMessage(REQUEST_COMPLETE, rc.requestCompleteMessage).apply { role = rc.role }
        }
  }
}

class ToolRequestFailed internal constructor() : AbstractToolRequest() {
  var requestFailedMessage = ""

  fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: RequestFailedCondition.() -> Unit,
  ) {
    RequestFailedCondition()
      .apply(block)
      .also { rfc ->
        if (requestFailedMessage.isNotEmpty()) {
          val conds = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
          addToolCallMessage(REQUEST_FAILED, rfc.requestFailedMessage, conds)
        }
      }
  }

  companion object {
    fun toolRequestFailed(block: ToolRequestFailed.() -> Unit) =
      ToolRequestFailed()
        .apply(block)
        .also { rf ->
          if (rf.requestFailedMessage.isNotEmpty())
            rf.addToolCallMessage(REQUEST_FAILED, rf.requestFailedMessage)
        }
  }
}
