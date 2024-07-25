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

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dtos.assistant.assistant.AssistantDestinationDto
import com.vapi4k.dtos.assistant.squad.MemberDto

@AssistantDslMarker
interface AssistantDestinations {
  fun destination(block: AssistantDestination.() -> Unit)
}

data class AssistantDestinationsImpl internal constructor(
  internal val member: MemberImpl,
  internal val dto: MemberDto,
) : AssistantDestinations {
  override fun destination(block: AssistantDestination.() -> Unit) {
    dto.assistantDestinations +=
      AssistantDestination(
        AssistantDestinationDto().apply { type = "assistant" }).apply(block).dto
  }
}
