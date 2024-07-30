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
import com.vapi4k.dtos.tools.ToolMessageCompleteDto
import com.vapi4k.dtos.tools.ToolMessageConditionDto
import com.vapi4k.dtos.tools.ToolMessageFailedDto
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

  fun requestCompleteMessages(block: RequestCompleteMessages.() -> Unit) =
    RequestCompleteMessages().apply(block).messageList

  fun requestFailedMessages(block: RequestFailedMessages.() -> Unit) =
    RequestFailedMessages().apply(block).messageList
}

@AssistantDslMarker
class RequestCompleteMessages internal constructor() {
  internal val messageList = mutableListOf<ToolMessageComplete>()
  private val requestCompleteChecker = DuplicateChecker()
  private val dtoConditions
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
    block: RequestCompleteCondition.() -> Unit,
  ) {
    val conditionSet = mutableSetOf(requiredCondition).apply { addAll(additional) }
    if (conditionSet in dtoConditions) {
      error("tool{} already has a condition(${conditionSet.joinToString()}){} with the same set of conditions")
    }
    RequestCompleteCondition(this, conditionSet).apply(block)
    if (conditionSet !in dtoConditions) {
      error("condition(${conditionSet.joinToString()}){} must have at least one message")
    }
  }
}

@AssistantDslMarker
class RequestCompleteCondition internal constructor(
  private val completeMessages: RequestCompleteMessages,
  private val conditionSet: Set<ToolMessageConditionDto>,
) {
  private val requestCompleteChecker = DuplicateChecker()

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    requestCompleteChecker.check("condition${conditionSet.joinToString()}{} already has a request complete message")
    return ToolMessageCompleteDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      ToolMessageComplete(dto).apply(block).also { completeMessages.messageList += it }
    }
  }
}

@AssistantDslMarker
class RequestFailedMessages internal constructor() {
  internal val messageList = mutableListOf<ToolMessageFailed>()
  private val requestFailedChecker = DuplicateChecker()
  private val dtoConditions
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
    block: RequestFailedCondition.() -> Unit,
  ) {
    val conditionSet = mutableSetOf(requiredCondition).apply { addAll(additional) }
    if (conditionSet in dtoConditions) {
      error("tool{} already has a condition(${conditionSet.joinToString()}){} with the same set of conditions")
    }
    RequestFailedCondition(this, conditionSet).apply(block)
    if (conditionSet !in dtoConditions) {
      error("condition(${conditionSet.joinToString()}){} must have at least one message")
    }
  }
}

@AssistantDslMarker
class RequestFailedCondition internal constructor(
  private val failedMessages: RequestFailedMessages,
  private val conditionSet: Set<ToolMessageConditionDto>,
) {
  private val requestFailedChecker = DuplicateChecker()

  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed {
    requestFailedChecker.check("condition${conditionSet.joinToString()}{} already has a request failed message")
    return ToolMessageFailedDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      ToolMessageFailed(dto).apply(block).also { failedMessages.messageList += it }
    }
  }
}
