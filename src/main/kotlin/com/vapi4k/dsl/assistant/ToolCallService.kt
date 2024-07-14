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

import com.vapi4k.dsl.vapi4k.ToolCallMessageType.REQUEST_COMPLETE
import com.vapi4k.dsl.vapi4k.ToolCallMessageType.REQUEST_FAILED
import com.vapi4k.dsl.vapi4k.ToolCallRoleType
import com.vapi4k.responses.ToolCallMessage
import com.vapi4k.responses.assistant.ToolMessageCondition
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
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
  @EncodeDefault(Mode.ALWAYS)
  var role = ToolCallRoleType.ASSISTANT
  var requestCompleteMessage = ""
}

@AssistantDslMarker
class RequestFailedCondition {
  var requestFailedMessage = ""
}

class RequestComplete {
  internal val messages = mutableListOf<ToolCallMessage>()

  var role = ToolCallRoleType.ASSISTANT
  var requestCompleteMessage = ""

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg conditions: ToolMessageCondition,
    block: RequestCompleteCondition.() -> Unit,
  ) {
    RequestCompleteCondition()
      .apply(block)
      .also { rcc ->
        if (requestCompleteMessage.isNotEmpty()) {
          messages += ToolCallMessage().apply {
            type = REQUEST_COMPLETE
            role = rcc.role
            content = rcc.requestCompleteMessage
            mutableSetOf(requiredCondition).apply { addAll(conditions.toSet()) }.toSet()
              .also { conditions ->
                if (conditions.isNotEmpty()) {
                  this.conditions = conditions.toList()
                }
              }
          }
        }
      }
  }

  companion object {
    fun requestComplete(block: RequestComplete.() -> Unit) =
      RequestComplete()
        .apply(block)
        .also { rc ->
          if (rc.requestCompleteMessage.isNotEmpty()) {
            rc.messages += ToolCallMessage().apply {
              type = REQUEST_COMPLETE
              role = rc.role
              content = rc.requestCompleteMessage
            }
          }
        }
  }
}

class RequestFailed {
  internal val messages = mutableListOf<ToolCallMessage>()

  var requestFailedMessage = ""

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg conditions: ToolMessageCondition,
    block: RequestFailedCondition.() -> Unit,
  ) {
    RequestFailedCondition()
      .apply(block)
      .also { rfc ->
        if (requestFailedMessage.isNotEmpty()) {
          messages += ToolCallMessage().apply {
            type = REQUEST_FAILED
            content = rfc.requestFailedMessage
            mutableSetOf(requiredCondition).apply { addAll(conditions.toSet()) }.toSet()
              .also { conditions ->
                if (conditions.isNotEmpty()) {
                  this.conditions = conditions.toList()
                }
              }
          }
        }
      }
  }

  companion object {
    fun requestFailed(block: RequestFailed.() -> Unit) =
      RequestFailed()
        .apply(block)
        .also { rf ->
          if (rf.requestFailedMessage.isNotEmpty()) {
            rf.messages += ToolCallMessage().apply {
              type = REQUEST_FAILED
              content = rf.requestFailedMessage
            }
          }
        }
  }
}
