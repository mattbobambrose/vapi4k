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

package com.vapi4k.dsl.squad

import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.api.squad.Members
import com.vapi4k.api.squad.Squad
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.assistant.AssistantOverridesImpl
import com.vapi4k.dsl.vapi4k.AssistantRequestContext
import com.vapi4k.dtos.squad.SquadDto
import com.vapi4k.utils.AssistantIdSource

interface SquadProperties {
  /**
  This is the name of the squad.
   */
  var name: String
}

class SquadImpl internal constructor(
  internal val assistantRequestContext: AssistantRequestContext,
  internal val sessionId: SessionId,
  internal val assistantIdSource: AssistantIdSource,
  internal val dto: SquadDto,
) : SquadProperties by dto,
  Squad {
  override fun members(block: Members.() -> Unit): Members = MembersImpl(this).apply(block)

  override fun memberOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(assistantRequestContext, sessionId, assistantIdSource, dto.membersOverrides).apply(block)
}
