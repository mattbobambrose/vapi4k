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
import com.vapi4k.dsl.tools.RequestCompleteCondition
import com.vapi4k.dsl.tools.enums.ToolMessageRoleType
import com.vapi4k.dtos.model.ToolMessageCompleteDto
import com.vapi4k.dtos.model.ToolMessageConditionDto

interface ToolMessageCompleteProperties {
  var role: ToolMessageRoleType
  var endCallAfterSpokenEnabled: Boolean?
  var content: String
}

@AssistantDslMarker
class ToolMessageComplete internal constructor(
  internal val dto: ToolMessageCompleteDto,
) : ToolMessageCompleteProperties by dto {
  fun condition(
    requiredConditionDto: ToolMessageConditionDto,
    vararg additional: ToolMessageConditionDto,
    block: RequestCompleteCondition.() -> Unit,
  ) = RequestCompleteCondition()
    .apply(block)
    .also { rcc ->
//      if (content.isNotEmpty()) {
//        val cond = mutableSetOf(requiredConditionDto).apply { addAll(additional.toSet()) }
//        addToolCallMessage(REQUEST_COMPLETE, rcc.message, cond).apply { role = rcc.role }
//      }
    }
}


//@AssistantDslMarker
//interface ToolRequestComplete {
//  var role: ToolCallRoleType
//  var content: String
//  var endCallAfterSpokenEnabled: Boolean?
//  fun condition(
//    requiredConditionDto: ToolMessageConditionDto,
//    vararg additional: ToolMessageConditionDto,
//    block: RequestCompleteCondition.() -> Unit,
//  ): RequestCompleteCondition
//}
//
//class ToolRequestCompleteImpl internal constructor(internal val dto: ToolMessageCompleteDto) :
//  ToolRequestComplete by dto, AbstractToolRequest(), ToolRequestComplete {
//  override var role = ToolCallRoleType.ASSISTANT
//  override var content = ""
//  override var endCallAfterSpokenEnabled: Boolean? = null
//
//  override fun condition(
//    requiredConditionDto: ToolMessageConditionDto,
//    vararg additional: ToolMessageConditionDto,
//    block: RequestCompleteCondition.() -> Unit,
//  ) = RequestCompleteCondition()
//    .apply(block)
//    .also { rcc ->
//      if (message.isNotEmpty()) {
//        val cond = mutableSetOf(requiredConditionDto).apply { addAll(additional.toSet()) }
//        addToolCallMessage(REQUEST_COMPLETE, rcc.message, cond).apply { role = rcc.role }
//      }
//    }
//
//  companion object {
//    fun toolRequestComplete(block: ToolRequestComplete.() -> Unit) =
//      ToolRequestCompleteImpl()
//        .apply(block)
//        .also { rc ->
//          if (rc.message.isNotEmpty())
//            rc.addToolCallMessage(ToolMessageType.REQUEST_COMPLETE, rc.message).apply { role = rc.role }
//        }
//  }
//}
