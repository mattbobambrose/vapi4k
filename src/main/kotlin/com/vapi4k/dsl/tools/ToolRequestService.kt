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
import com.vapi4k.dsl.tools.toolMessages.ToolMessageComplete
import com.vapi4k.dsl.tools.toolMessages.ToolMessageFailed
import com.vapi4k.dsl.vapi4k.enums.ToolCallRoleType
import com.vapi4k.dtos.model.ToolMessageCompleteDto
import com.vapi4k.dtos.model.ToolMessageConditionDto
import com.vapi4k.dtos.model.ToolMessageFailedDto
import com.vapi4k.utils.DuplicateChecker
import kotlinx.serialization.json.JsonElement

abstract class ToolRequestService {
  val requestFailedChecker = DuplicateChecker()

  open fun onToolRequestComplete(
    toolCallRequest: JsonElement,
    result: String,
  ): List<ToolMessageComplete> = emptyList()

  open fun onToolRequestFailed(
    toolCallRequest: JsonElement,
    errorMessage: String,
  ): List<ToolMessageFailed> = emptyList()

  fun requestCompleteMessages(block: CompleteMessages.() -> Unit) =
    CompleteMessages().apply(block).messageList

  fun requestFailedMessages(block: FailedMessages.() -> Unit) =
    FailedMessages().apply(block).messageList
}

@AssistantDslMarker
class CompleteMessages internal constructor() {
  internal val messageList = mutableListOf<ToolMessageComplete>()
  private val requestCompleteChecker = DuplicateChecker()
  private val conditionSetList
    get() = messageList.map { it.dto }.filter { it.conditions.isNotEmpty() }.map { it.conditions }.toSet()

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    requestCompleteChecker.check("tool{} already has a request complete message")
    return ToolMessageCompleteDto().let { dto ->
      ToolMessageComplete(dto).apply(block).also { messageList += it }
    }
  }

  fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: CompleteCondition.() -> Unit,
  ) {
    val conditionsSet = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
    if (conditionsSet.isNotEmpty() && conditionsSet in conditionSetList) {
      error("tool{} already has a condition(${conditionsSet.joinToString()}){} with the same set of conditions")
    }
    CompleteCondition(this, conditionsSet).apply(block)
    if (conditionsSet.isNotEmpty() && conditionsSet !in conditionSetList) {
      error("condition(${conditionsSet.joinToString()}){} must have at least one message")
    }
  }
}

@AssistantDslMarker
class CompleteCondition internal constructor(
  private val completeMessages: CompleteMessages,
  private val conditionSet: Set<ToolMessageConditionDto>,
) {
  private val requestCompleteChecker = DuplicateChecker()

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    requestCompleteChecker.check("condition${conditionSet.joinToString()}{} already has a request complete message")
    return ToolMessageCompleteDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      ToolMessageComplete(dto).apply(block)
    }
  }
}

@AssistantDslMarker
class FailedMessages internal constructor() {
  internal val messageList = mutableListOf<ToolMessageFailed>()
  private val requestFailedChecker = DuplicateChecker()
  private val conditionSetList
    get() = messageList.map { it.dto }.filter { it.conditions.isNotEmpty() }.map { it.conditions }.toSet()

  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed {
    requestFailedChecker.check("tool{} already has a request failed message")
    return ToolMessageFailedDto().let { dto ->
      ToolMessageFailed(dto).apply(block).also { messageList += it }
    }
  }

  fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: FailedCondition.() -> Unit,
  ) {
    val conditionsSet = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
    if (conditionsSet.isNotEmpty() && conditionsSet in conditionSetList) {
      error("tool{} already has a condition(${conditionsSet.joinToString()}){} with the same set of conditions")
    }
    FailedCondition(this, conditionsSet).apply(block)
    if (conditionsSet.isNotEmpty() && conditionsSet !in conditionSetList) {
      error("condition(${conditionsSet.joinToString()}){} must have at least one message")
    }
  }
}

@AssistantDslMarker
class FailedCondition internal constructor(
  private val failedMessages: FailedMessages,
  private val conditionSet: Set<ToolMessageConditionDto>,
) {
  val requestFailedChecker = DuplicateChecker()

  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed {
    requestFailedChecker.check("condition${conditionSet.joinToString()}{} already has a request failed message")
    return ToolMessageFailedDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      ToolMessageFailed(dto).apply(block)
    }
  }
}

@AssistantDslMarker
class RequestCompleteCondition internal constructor() {
  var role = ToolCallRoleType.ASSISTANT
  var message = ""
}

@AssistantDslMarker
class RequestFailedCondition internal constructor() {
  var message = ""
}

//abstract class AbstractToolRequest {
//  internal val messages = mutableListOf<CommonToolMessageDto>()
//
//  protected fun addToolCallMessage(
//    type: ToolMessageType,
//    content: String,
//    conditions: Set<ToolMessageConditionDto> = emptySet(),
//  ): CommonToolMessageDto =
//    ToolCallMessageDto()
//      .apply {
//        this.type = type
//        this.content = content
//        if (conditions.isNotEmpty())
//          this.conditions.addAll(conditions)
//      }
//      .apply { messages += this }
//}
