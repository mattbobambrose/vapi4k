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
import com.vapi4k.dsl.tools.enums.ToolMessageRoleType
import com.vapi4k.dsl.tools.enums.ToolMessageType
import com.vapi4k.dtos.model.ToolMessageCompleteDto
import com.vapi4k.dtos.model.ToolMessageConditionDto


interface ToolMessageCompleteProperties {
  var type: ToolMessageType
  var role: ToolMessageRoleType
  var endCallAfterSpokenEnabled: Boolean?
  var content: String
  val conditions: MutableSet<ToolMessageConditionDto>

}

@AssistantDslMarker
data class ToolMessageComplete internal constructor(internal val dto: ToolMessageCompleteDto) :
  ToolMessageCompleteProperties by dto
