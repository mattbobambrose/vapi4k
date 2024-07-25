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

import com.vapi4k.common.DuplicateChecker
import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dtos.squad.MemberDto

@AssistantDslMarker
interface Member {
  fun assistantId(block: AssistantId.() -> Unit): AssistantId
  fun assistant(block: Assistant.() -> Unit): Assistant
  fun destinations(block: AssistantDestinations.() -> Unit): AssistantDestinations
}

data class MemberImpl(
  internal val members: MembersImpl,
  internal val dto: MemberDto,
) : Member {
  private val memberChecker = DuplicateChecker()

  override fun assistantId(block: AssistantId.() -> Unit): AssistantId {
    memberChecker.check("Member already has an assistantId assigned")
    return AssistantId(dto).apply(block)
  }

  override fun assistant(block: Assistant.() -> Unit): Assistant {
    memberChecker.check("Member already has an assistant assigned")
    return AssistantImpl(
      members.squad.request,
      members.squad.sessionId,
      dto.assistant,
      dto.assistantOverrides
    ).apply(block)
  }

  override fun destinations(block: AssistantDestinations.() -> Unit): AssistantDestinations =
    AssistantDestinationsImpl(this, dto).apply(block)
}
