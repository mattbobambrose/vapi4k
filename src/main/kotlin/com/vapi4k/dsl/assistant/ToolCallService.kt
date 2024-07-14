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

import com.vapi4k.dsl.vapi4k.ToolCallMessageType
import com.vapi4k.dsl.vapi4k.ToolCallMessageType.REQUEST_COMPLETE
import com.vapi4k.dsl.vapi4k.ToolCallMessageType.REQUEST_FAILED
import com.vapi4k.dsl.vapi4k.ToolCallRoleType
import com.vapi4k.responses.ToolCallMessage
import com.vapi4k.responses.assistant.ToolMessageCondition
import kotlinx.serialization.json.JsonElement

abstract class ToolCallService() {
  open fun onRequestComplete(
    toolCallRequest: JsonElement,
    result: String,
  ) = RequestComplete()

  open fun onRequestFailed(
    toolCallRequest: JsonElement,
    errorMessage: String,
  ) = RequestFailed()
}

@AssistantDslMarker
class RequestCompleteCondition {
  var role = ToolCallRoleType.ASSISTANT
  var requestCompleteMessage = ""
}

@AssistantDslMarker
class RequestFailedCondition {
  var requestFailedMessage = ""
}

abstract class AbstractRequest {
  internal val messages = mutableListOf<ToolCallMessage>()

  protected fun addToolCallMessage(
    type: ToolCallMessageType,
    content: String,
    conditions: Set<ToolMessageCondition> = emptySet(),
  ): ToolCallMessage =
    ToolCallMessage()
      .apply {
        this.type = type
        this.content = content
        if (conditions.isNotEmpty())
          this.conditions = conditions.toList()
      }
      .apply { messages += this }
}

class RequestComplete : AbstractRequest() {
  var role = ToolCallRoleType.ASSISTANT
  var requestCompleteMessage = ""

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg additional: ToolMessageCondition,
    block: RequestCompleteCondition.() -> Unit,
  ) {
    RequestCompleteCondition()
      .apply(block)
      .also { rcc ->
        if (requestCompleteMessage.isNotEmpty()) {
          val cond = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
          addToolCallMessage(REQUEST_COMPLETE, rcc.requestCompleteMessage, cond).apply { role = rcc.role }
        }
      }
  }

  companion object {
    fun requestComplete(block: RequestComplete.() -> Unit) =
      RequestComplete()
        .apply(block)
        .also { rc ->
          if (rc.requestCompleteMessage.isNotEmpty())
            rc.addToolCallMessage(REQUEST_COMPLETE, rc.requestCompleteMessage).apply { role = rc.role }
        }
  }
}

class RequestFailed : AbstractRequest() {
  var requestFailedMessage = ""

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg additional: ToolMessageCondition,
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
    fun requestFailed(block: RequestFailed.() -> Unit) =
      RequestFailed()
        .apply(block)
        .also { rf ->
          if (rf.requestFailedMessage.isNotEmpty())
            rf.addToolCallMessage(REQUEST_FAILED, rf.requestFailedMessage)
        }
  }
}
