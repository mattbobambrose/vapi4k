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

package com.vapi4k.server

import com.vapi4k.common.AssistantId
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import kotlinx.serialization.json.JsonElement

data class RequestContext(
  val application: AbstractApplicationImpl,
  val request: JsonElement,
  val sessionId: SessionId,
  val assistantId: AssistantId,
) {
  fun copyWithNewAssistantId(newAssistantId: AssistantId) =
    RequestContext(application, request, sessionId, newAssistantId)
}
