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
import com.vapi4k.dsl.model.enums.ToolMessageType
import com.vapi4k.dtos.model.ToolMessageConditionDto
import com.vapi4k.dtos.model.ToolMessageDto
import kotlin.reflect.KProperty

@AssistantDslMarker
class ToolCondition internal constructor(
  internal val tool: ToolImpl,
  internal val conditionSet: Set<ToolMessageConditionDto>,
) {
  private val messages get() = tool.toolDto.messages
  private val isMatchingRRD: (ToolMessageDto) -> Boolean
    get() = { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions == conditionSet }

  var requestStartMessage by ConditionDelegate(ToolMessageType.REQUEST_START)
  var requestCompleteMessage by ConditionDelegate(ToolMessageType.REQUEST_COMPLETE)
  var requestFailedMessage by ConditionDelegate(ToolMessageType.REQUEST_FAILED)
  var requestDelayedMessage by ConditionDelegate(ToolMessageType.REQUEST_RESPONSE_DELAYED)

  var delayedMillis
    get() = messages.singleOrNull(isMatchingRRD)?.timingMilliseconds ?: -1
    set(delayedMillis) {
      require(delayedMillis >= 0) { "delayedMillis must be greater than or equal to 0" }
      if (messages.any(isMatchingRRD)) {
        messages.single(isMatchingRRD).timingMilliseconds = delayedMillis
      } else tool.futureDelay = delayedMillis
    }

  companion object {
    private class ConditionDelegate(val requestType: ToolMessageType) {
      operator fun getValue(
        condition: ToolCondition,
        property: KProperty<*>,
      ) = with(condition) { messages.singleOrNull(requestType.isMatch(conditionSet))?.content ?: "" }

      operator fun setValue(
        condition: ToolCondition,
        property: KProperty<*>,
        newVal: String,
      ) =
        with(condition) {
          if (messages.any(requestType.isMatch(conditionSet))) {
            messages.single(requestType.isMatch(conditionSet)).content = newVal
          } else {
            messages += ToolMessageDto().apply {
              type = requestType.type
              content = newVal
              timingMilliseconds = -1
              conditions.addAll(conditionSet)
            }
          }
        }

      private fun ToolMessageType.isMatch(conditionSet: Set<ToolMessageConditionDto>): (ToolMessageDto) -> Boolean =
        { it.type == type && it.conditions == conditionSet }
    }
  }
}
