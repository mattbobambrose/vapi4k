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

import com.vapi4k.dsl.assistant.ToolMessageType.REQUEST_COMPLETE
import com.vapi4k.dsl.assistant.ToolMessageType.REQUEST_FAILED
import com.vapi4k.dsl.assistant.ToolMessageType.REQUEST_RESPONSE_DELAYED
import com.vapi4k.dsl.assistant.ToolMessageType.REQUEST_START
import com.vapi4k.responses.assistant.ToolDto
import com.vapi4k.responses.assistant.ToolMessage
import com.vapi4k.responses.assistant.ToolMessageCondition
import com.vapi4k.responses.assistant.ToolMessageCondition.Companion.toolMessageCondition
import kotlin.reflect.KProperty

@AssistantDslMarker
class Tool internal constructor(internal val toolDto: ToolDto) {
  private val messages get() = toolDto.messages

  var futureDelay = -1

  private class MessageDelegate(val requestType: ToolMessageType) {
    operator fun getValue(
      thisRef: Tool,
      property: KProperty<*>,
    ) =
      thisRef.messages.singleOrNull(requestType.isMatching)?.content ?: ""

    operator fun setValue(
      thisRef: Tool,
      property: KProperty<*>,
      newVal: String,
    ) =
      with(thisRef) {
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

  var requestStartMessage by MessageDelegate(REQUEST_START)
  var requestCompleteMessage by MessageDelegate(REQUEST_COMPLETE)
  var requestFailedMessage by MessageDelegate(REQUEST_FAILED)
  var requestDelayedMessage by MessageDelegate(REQUEST_RESPONSE_DELAYED)

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
    private val ToolMessageType.isMatching: (ToolMessage) -> Boolean
      get() = { it.type == type && it.conditions.isEmpty() }
  }
}

infix fun String.eq(value: String) =
  toolMessageCondition(this, "eq", value)

infix fun String.neq(value: String) =
  toolMessageCondition(this, "neq", value)

infix fun String.gt(value: String) =
  toolMessageCondition(this, "gt", value)

infix fun String.gte(value: String) =
  toolMessageCondition(this, "gte", value)

infix fun String.lt(value: String) =
  toolMessageCondition(this, "lt", value)

infix fun String.lte(value: String) =
  toolMessageCondition(this, "lte", value)

infix fun String.eq(value: Int) =
  toolMessageCondition(this, "eq", value)

infix fun String.neq(value: Int) =
  toolMessageCondition(this, "neq", value)

infix fun String.gt(value: Int) =
  toolMessageCondition(this, "gt", value)

infix fun String.gte(value: Int) =
  toolMessageCondition(this, "gte", value)

infix fun String.lt(value: Int) =
  toolMessageCondition(this, "lt", value)

infix fun String.lte(value: Int) =
  toolMessageCondition(this, "lte", value)

infix fun String.eq(value: Boolean) =
  toolMessageCondition(this, "eq", value)

infix fun String.neq(value: Boolean) =
  toolMessageCondition(this, "neq", value)

infix fun String.gt(value: Boolean) =
  toolMessageCondition(this, "gt", value)

infix fun String.gte(value: Boolean) =
  toolMessageCondition(this, "gte", value)

infix fun String.lt(value: Boolean) =
  toolMessageCondition(this, "lt", value)

infix fun String.lte(value: Boolean) =
  toolMessageCondition(this, "lte", value)
