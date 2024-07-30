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

package com.vapi4k.dsl.tools.toolMessages

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dtos.model.ToolMessageFailedDto

interface ToolMessageFailedProperties {
  var endCallAfterSpokenEnabled: Boolean?
  var content: String
}

@AssistantDslMarker
data class ToolMessageFailed internal constructor(
  internal val dto: ToolMessageFailedDto,
) : ToolMessageFailedProperties by dto

// @AssistantDslMarker
// class ToolRequestFailed internal constructor() : AbstractToolRequest() {
//  var message = ""
//
//  fun condition(
//    requiredCondition: ToolMessageConditionDto,
//    vararg additional: ToolMessageConditionDto,
//    block: RequestFailedCondition.() -> Unit,
//  ) {
//    RequestFailedCondition()
//      .apply(block)
//      .also { rfc ->
//        if (message.isNotEmpty()) {
//          val conds = mutableSetOf(requiredCondition).apply { addAll(additional.toSet()) }
//          addToolCallMessage(ToolMessageType.REQUEST_FAILED, rfc.message, conds)
//        }
//      }
//  }
//
//  companion object {
//    fun toolRequestFailed(block: ToolRequestFailed.() -> Unit) =
//      ToolRequestFailed()
//        .apply(block)
//        .also { rf ->
//          if (rf.message.isNotEmpty())
//            rf.addToolCallMessage(REQUEST_FAILED, rf.message)
//        }
//  }
// }
