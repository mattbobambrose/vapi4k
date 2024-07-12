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

import com.vapi4k.enums.ToolMessageType
import com.vapi4k.enums.ToolMessageType.REQUEST_COMPLETE
import com.vapi4k.enums.ToolMessageType.REQUEST_FAILED
import com.vapi4k.enums.ToolMessageType.REQUEST_RESPONSE_DELAYED
import com.vapi4k.enums.ToolMessageType.REQUEST_START
import com.vapi4k.responses.assistant.ToolMessage
import com.vapi4k.responses.assistant.ToolMessageCondition
import kotlin.reflect.KProperty

@AssistantDslMarker
class Condition(
  val tool: Tool,
  val conditionSet: Set<ToolMessageCondition>,
) {
  private class ConditionDelegate(
    val requestType: ToolMessageType,
    val messages: MutableList<ToolMessage>,
    val condSet: Set<ToolMessageCondition>
  ) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
      messages.singleOrNull(requestType.isMatch)?.content ?: ""

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newVal: String) =
      if (messages.any(requestType.isMatch)) {
        messages.single(requestType.isMatch).content = newVal
      } else {
        messages += ToolMessage().apply {
          type = requestType.type
          content = newVal
          timingMilliseconds = -1
          conditions = condSet
        }
      }

    private val ToolMessageType.isMatch: (ToolMessage) -> Boolean
      get() = { it.type == type && it.conditions == condSet }
  }

  private val messages get() = tool.toolDto.messages

  private val isMatchingRRD: (ToolMessage) -> Boolean
    get() = { it.type == REQUEST_RESPONSE_DELAYED.type && it.conditions == conditionSet }

  var requestStartMessage by ConditionDelegate(REQUEST_START, messages, conditionSet)
  var requestCompleteMessage by ConditionDelegate(REQUEST_COMPLETE, messages, conditionSet)
  var requestFailedMessage by ConditionDelegate(REQUEST_FAILED, messages, conditionSet)
  var requestDelayedMessage by ConditionDelegate(REQUEST_RESPONSE_DELAYED, messages, conditionSet)

  var delayedMillis
    get() = messages.singleOrNull(isMatchingRRD)?.timingMilliseconds ?: -1
    set(delayedMillis) {
      require(delayedMillis >= 0) { "delayedMillis must be greater than or equal to 0" }
      if (messages.any(isMatchingRRD)) {
        messages.single(isMatchingRRD).timingMilliseconds = delayedMillis
      } else tool.futureDelay = delayedMillis
    }
}
