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
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.dtos.tools.ToolMessageCompleteDto
import com.vapi4k.dtos.tools.ToolMessageConditionDto
import com.vapi4k.dtos.tools.ToolMessageDelayedDto
import com.vapi4k.dtos.tools.ToolMessageFailedDto
import com.vapi4k.dtos.tools.ToolMessageStartDto
import com.vapi4k.utils.DuplicateChecker

@AssistantDslMarker
interface Tool {
  fun requestStartMessage(block: ToolMessageStart.() -> Unit): ToolMessageStart

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete

  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed

  fun requestDelayedMessage(block: ToolMessageDelayed.() -> Unit): ToolMessageDelayed

  fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: ToolCondition.() -> Unit,
  )
}

class ToolImpl internal constructor(
  internal val toolDto: ToolDto,
) : Tool {
  private val requestStartChecker = DuplicateChecker()
  private val requestCompleteChecker = DuplicateChecker()
  private val requestFailedChecker = DuplicateChecker()
  private val requestDelayedChecker = DuplicateChecker()

  internal val messages get() = toolDto.messages
  internal val dtoConditions
    get() = messages.filter { it.conditions.isNotEmpty() }.map { it.conditions }.toSet()

  internal var futureDelay = -1

  override fun requestStartMessage(block: ToolMessageStart.() -> Unit): ToolMessageStart {
    requestStartChecker.check("tool{} already has a request start message")
    return ToolMessageStartDto().let { dto ->
      toolDto.messages.add(dto)
      ToolMessageStart(dto).apply(block)
    }
  }

  override fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    requestCompleteChecker.check("tool{} already has a request complete message")
    return ToolMessageCompleteDto().let { dto ->
      toolDto.messages.add(dto)
      ToolMessageComplete(dto).apply(block)
    }
  }

  override fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed {
    requestFailedChecker.check("tool{} already has a request failed message")
    return ToolMessageFailedDto().let { dto ->
      toolDto.messages.add(dto)
      ToolMessageFailed(dto).apply(block)
    }
  }

  override fun requestDelayedMessage(block: ToolMessageDelayed.() -> Unit): ToolMessageDelayed {
    requestDelayedChecker.check("tool{} already has a request delayed message")
    return ToolMessageDelayedDto().let { dto ->
      toolDto.messages.add(dto)
      ToolMessageDelayed(dto).apply(block)
    }
  }

  override fun condition(
    requiredCondition: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: ToolCondition.() -> Unit,
  ) {
    val conditionSet = mutableSetOf(requiredCondition).apply { addAll(additional) }
    if (conditionSet in dtoConditions) {
      error("condition(${conditionSet.joinToString()}){} duplicates an existing condition{}")
    }
    ToolConditionImpl(this, conditionSet).apply(block)
    if (conditionSet !in dtoConditions) {
      error("condition(${conditionSet.joinToString()}){} must have at least one message")
    }
  }
}
