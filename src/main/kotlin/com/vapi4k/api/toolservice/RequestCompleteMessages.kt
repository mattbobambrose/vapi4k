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

package com.vapi4k.api.toolservice

import com.vapi4k.api.tools.ToolMessageComplete
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dtos.tools.ToolMessageCompleteDto
import com.vapi4k.dtos.tools.ToolMessageCondition
import com.vapi4k.utils.DuplicateChecker

@AssistantDslMarker
class RequestCompleteMessages internal constructor() {
  private val duplicateChecker = DuplicateChecker()
  internal val messageList = mutableListOf<ToolMessageComplete>()

  private val dtoConditions
    get() = messageList.map { it.dto }.filter { it.conditions.isNotEmpty() }.map { it.conditions }.toSet()

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    duplicateChecker.check("tool{} already has a request complete message")
    return ToolMessageCompleteDto().let { dto ->
      ToolMessageComplete(dto).apply(block).also { messageList += it }
    }
  }

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg additionalConditions: ToolMessageCondition,
    block: RequestCompleteCondition.() -> Unit,
  ) {
    val conditionSet = mutableSetOf(requiredCondition).apply { addAll(additionalConditions) }
    if (conditionSet in dtoConditions) {
      error("condition(${conditionSet.joinToString()}){} duplicates an existing condition{}")
    }
    RequestCompleteCondition(this, conditionSet).apply(block)
    if (conditionSet !in dtoConditions) {
      error("condition(${conditionSet.joinToString()}){} must have at least one message")
    }
  }
}
