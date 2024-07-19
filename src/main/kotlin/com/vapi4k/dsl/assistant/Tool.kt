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
import com.vapi4k.dsl.assistant.enums.ToolMessageType
import com.vapi4k.dsl.assistant.enums.ToolMessageType.REQUEST_COMPLETE
import com.vapi4k.dsl.assistant.enums.ToolMessageType.REQUEST_FAILED
import com.vapi4k.dsl.assistant.enums.ToolMessageType.REQUEST_RESPONSE_DELAYED
import com.vapi4k.dsl.assistant.enums.ToolMessageType.REQUEST_START
import com.vapi4k.responses.assistant.ToolDto
import com.vapi4k.responses.assistant.ToolMessage
import com.vapi4k.responses.assistant.ToolMessageCondition
import kotlin.reflect.KProperty

@AssistantDslMarker
class Tool internal constructor(internal val toolDto: ToolDto) {
  internal val messages get() = toolDto.messages

  var futureDelay = -1

  var requestStartMessage by ToolMessageDelegate(REQUEST_START)
  var requestCompleteMessage by ToolMessageDelegate(REQUEST_COMPLETE)
  var requestFailedMessage by ToolMessageDelegate(REQUEST_FAILED)
  var requestDelayedMessage by ToolMessageDelegate(REQUEST_RESPONSE_DELAYED)

  var delayedMillis
    get() = messages.singleOrNull(REQUEST_RESPONSE_DELAYED.isMatching)?.timingMilliseconds ?: -1
    set(delayedMillis) {
      require(delayedMillis >= 0) { "delayedMillis must be greater than or equal to 0" }
      if (messages.any(REQUEST_RESPONSE_DELAYED.isMatching)) {
        messages.single(REQUEST_RESPONSE_DELAYED.isMatching).timingMilliseconds = delayedMillis
      } else futureDelay = delayedMillis
    }

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg additional: ToolMessageCondition,
    block: ToolCondition.() -> Unit,
  ) {
    val conditionsSet = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
    ToolCondition(this, conditionsSet).apply(block)
  }

  companion object {
    private class ToolMessageDelegate(val requestType: ToolMessageType) {
      operator fun getValue(
        tool: Tool,
        property: KProperty<*>,
      ) =
        tool.messages.singleOrNull(requestType.isMatching)?.content ?: ""

      operator fun setValue(
        tool: Tool,
        property: KProperty<*>,
        newVal: String,
      ) =
        with(tool) {
          if (messages.any(requestType.isMatching)) {
            messages.single(requestType.isMatching).content = newVal
          } else {
            messages += ToolMessage().apply {
              type = requestType.type
              content = newVal
              timingMilliseconds = futureDelay
            }
          }
        }
    }
  }
}
