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

package com.vapi4k.dsl.assistant.squad


import com.vapi4k.common.CacheId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantOverrides
import com.vapi4k.dsl.assistant.AssistantOverridesImpl
import com.vapi4k.responses.assistant.SquadDto
import kotlinx.serialization.json.JsonElement

interface SquadProperties {
  var name: String
}

@AssistantDslMarker
interface Squad : SquadProperties {
  fun members(block: Members.() -> Unit): Members
  fun memberOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides
}

data class SquadImpl internal constructor(
  internal val request: JsonElement,
  internal val cacheId: CacheId,
  internal val dto: SquadDto,
) : SquadProperties by dto, Squad {
  override fun members(block: Members.() -> Unit): Members = MembersImpl(this).apply(block)

  override fun memberOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(request, cacheId, dto.membersOverrides).apply(block)
}
