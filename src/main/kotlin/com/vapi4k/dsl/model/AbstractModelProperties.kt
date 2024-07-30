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

package com.vapi4k.dsl.model

import com.vapi4k.common.AssistantCacheId
import com.vapi4k.common.MessageCallId
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.model.enums.MessageRoleType
import com.vapi4k.dtos.RoleMessage
import com.vapi4k.dtos.functions.FunctionDto
import com.vapi4k.dtos.tools.ToolDto

interface AbstractModelProperties {
  val sessionCacheId: SessionCacheId
  val assistantCacheId: AssistantCacheId
  val messageCallId: MessageCallId
  val toolDtos: MutableList<ToolDto>
  val functionDtos: MutableList<FunctionDto>
  val messages: MutableList<RoleMessage>

  fun message(
    role: MessageRoleType,
    content: String,
  )
}
