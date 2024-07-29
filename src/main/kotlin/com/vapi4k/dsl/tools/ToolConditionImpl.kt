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
import com.vapi4k.dsl.tools.enums.ToolMessageType
import com.vapi4k.dsl.tools.toolMessages.ToolMessageComplete
import com.vapi4k.dsl.tools.toolMessages.ToolMessageDelayed
import com.vapi4k.dsl.tools.toolMessages.ToolMessageFailed
import com.vapi4k.dsl.tools.toolMessages.ToolMessageStart
import com.vapi4k.dtos.model.ToolMessageCompleteDto
import com.vapi4k.dtos.model.ToolMessageConditionDto
import com.vapi4k.dtos.model.ToolMessageDelayedDto
import com.vapi4k.dtos.model.ToolMessageDto
import com.vapi4k.dtos.model.ToolMessageFailedDto
import com.vapi4k.dtos.model.ToolMessageStartDto
import com.vapi4k.utils.DuplicateChecker

@AssistantDslMarker
interface ToolCondition {
  fun requestStartMessage(block: ToolMessageStart.() -> Unit): ToolMessageStart

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete

  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed

  fun requestDelayedMessage(block: ToolMessageDelayed.() -> Unit): ToolMessageDelayed
}

class ToolConditionImpl internal constructor(
  internal val tool: ToolImpl,
  internal val conditionSet: Set<ToolMessageConditionDto>,
) : ToolCondition {
  private val messages get() = tool.toolDto.messages
  private val isMatchingRRD: (ToolMessageDto) -> Boolean
    get() = { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions == conditionSet }

  val requestStartChecker = DuplicateChecker()
  val requestCompleteChecker = DuplicateChecker()
  val requestFailedChecker = DuplicateChecker()
  val requestDelayedChecker = DuplicateChecker()

  override fun requestStartMessage(block: ToolMessageStart.() -> Unit): ToolMessageStart {
    requestStartChecker.check("condition{} already has a request start message")
    return ToolMessageStartDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      tool.toolDto.messages.add(dto)
      ToolMessageStart(dto).apply(block)
    }
  }

  override fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    requestCompleteChecker.check("condition{} already has a request complete message")
    return ToolMessageCompleteDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      tool.toolDto.messages.add(dto)
      ToolMessageComplete(dto).apply(block)
    }
  }

  override fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed {
    requestFailedChecker.check("condition{} already has a request failed message")
    return ToolMessageFailedDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      tool.toolDto.messages.add(dto)
      ToolMessageFailed(dto).apply(block)
    }
  }

  override fun requestDelayedMessage(block: ToolMessageDelayed.() -> Unit): ToolMessageDelayed {
    requestDelayedChecker.check("condition{} already has a request delayed message")
    return ToolMessageDelayedDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      tool.toolDto.messages.add(dto)
      ToolMessageDelayed(dto).apply(block)
    }
  }

//  var requestStartMessage by ConditionDelegate(ToolMessageType.REQUEST_START)
//  var requestCompleteMessage by ConditionDelegate(ToolMessageType.REQUEST_COMPLETE)
//  var requestFailedMessage by ConditionDelegate(ToolMessageType.REQUEST_FAILED)
//  var requestDelayedMessage by ConditionDelegate(ToolMessageType.REQUEST_RESPONSE_DELAYED)

//  var delayedMillis
//    get() = messages.singleOrNull(isMatchingRRD)?.timingMilliseconds ?: -1
//    set(delayedMillis) {
//      require(delayedMillis >= 0) { "delayedMillis must be greater than or equal to 0" }
//      if (messages.any(isMatchingRRD)) {
//        messages.single(isMatchingRRD).timingMilliseconds = delayedMillis
//      } else tool.futureDelay = delayedMillis
//    }

  companion object {
//    private class ConditionDelegate(val requestType: ToolMessageType) {
//      operator fun getValue(
//        condition: ToolCondition,
//        property: KProperty<*>,
//      ) = with(condition) { messages.singleOrNull(requestType.isMatch(conditionSet))?.content ?: "" }
//
//      operator fun setValue(
//        condition: ToolCondition,
//        property: KProperty<*>,
//        newVal: String,
//      ) =
//        with(condition) {
//          if (messages.any(requestType.isMatch(conditionSet))) {
//            messages.single(requestType.isMatch(conditionSet)).content = newVal
//          } else {
//            messages += ToolMessageDto().apply {
//              type = requestType.desc
//              content = newVal
//              timingMilliseconds = -1
//              conditions.addAll(conditionSet)
//            }
//          }
//        }
//
//      private fun ToolMessageType.isMatch(conditionSet: Set<ToolMessageConditionDto>): (ToolMessageDto) -> Boolean =
//        { it.type == desc && it.conditions == conditionSet }
//    }
  }
}
