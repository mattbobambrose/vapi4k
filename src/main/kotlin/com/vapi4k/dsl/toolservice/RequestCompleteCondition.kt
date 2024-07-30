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

package com.vapi4k.dsl.toolservice

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.tools.ToolMessageComplete
import com.vapi4k.dtos.tools.ToolMessageCompleteDto
import com.vapi4k.dtos.tools.ToolMessageCondition
import com.vapi4k.utils.DuplicateChecker

@AssistantDslMarker
class RequestCompleteCondition internal constructor(
  private val completeMessages: RequestCompleteMessages,
  private val conditionSet: Set<ToolMessageCondition>,
) {
  private val duplicateChecker = DuplicateChecker()

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete {
    duplicateChecker.check("condition${conditionSet.joinToString()}{} already has a requestCompleteMessage{}")
    return ToolMessageCompleteDto().let { dto ->
      dto.conditions.addAll(conditionSet)
      ToolMessageComplete(dto).apply(block).also { completeMessages.messageList += it }
    }
  }
}
