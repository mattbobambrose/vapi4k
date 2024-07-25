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
import com.vapi4k.dsl.assistant.isMatching
import com.vapi4k.dsl.model.enums.ToolMessageType
import com.vapi4k.dtos.assistant.model.ToolDto
import com.vapi4k.dtos.assistant.model.ToolMessageConditionDto
import com.vapi4k.dtos.assistant.model.ToolMessageDto
import kotlin.reflect.KProperty

@AssistantDslMarker
interface Tool {
  var requestStartMessage: String
  var requestCompleteMessage: String
  var requestFailedMessage: String
  var requestDelayedMessage: String
  var delayedMillis: Int

  fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: ToolCondition.() -> Unit,
  )
}

class ToolImpl internal constructor(internal val toolDto: ToolDto) : Tool {
  internal val messages get() = toolDto.messages

  internal var futureDelay = -1

  override var requestStartMessage by ToolMessageDelegate(ToolMessageType.REQUEST_START)
  override var requestCompleteMessage by ToolMessageDelegate(ToolMessageType.REQUEST_COMPLETE)
  override var requestFailedMessage by ToolMessageDelegate(ToolMessageType.REQUEST_FAILED)
  override var requestDelayedMessage by ToolMessageDelegate(ToolMessageType.REQUEST_RESPONSE_DELAYED)

  override var delayedMillis
    get() = messages.singleOrNull(ToolMessageType.REQUEST_RESPONSE_DELAYED.isMatching)?.timingMilliseconds ?: -1
    set(delayedMillis) {
      require(delayedMillis >= 0) { "delayedMillis must be greater than or equal to 0" }
      if (messages.any(ToolMessageType.REQUEST_RESPONSE_DELAYED.isMatching)) {
        messages.single(ToolMessageType.REQUEST_RESPONSE_DELAYED.isMatching).timingMilliseconds = delayedMillis
      } else futureDelay = delayedMillis
    }

  override fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: ToolCondition.() -> Unit,
  ) {
    val conditionsSet = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
    ToolCondition(this, conditionsSet).apply(block)
  }

  companion object {
    private class ToolMessageDelegate(val requestType: ToolMessageType) {
      operator fun getValue(
        tool: ToolImpl,
        property: KProperty<*>,
      ) =
        tool.messages.singleOrNull(requestType.isMatching)?.content ?: ""

      operator fun setValue(
        tool: ToolImpl,
        property: KProperty<*>,
        newVal: String,
      ) =
        with(tool) {
          if (messages.any(requestType.isMatching)) {
            messages.single(requestType.isMatching).content = newVal
          } else {
            messages += ToolMessageDto().apply {
              type = requestType.type
              content = newVal
              timingMilliseconds = futureDelay
            }
          }
        }
    }
  }
}
