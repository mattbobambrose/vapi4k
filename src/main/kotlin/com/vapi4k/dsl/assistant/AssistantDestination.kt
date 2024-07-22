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

package com.vapi4k.dsl.assistant

import com.vapi4k.responses.assistant.AssistantDestinationDto
import com.vapi4k.responses.assistant.MemberDto

@AssistantDslMarker
data class AssistantDestinations internal constructor(
  internal val member: Member,
  internal val memberDto: MemberDto,
) {
  fun destination(block: AssistantDestination.() -> Unit) {
    memberDto.assistantDestinations +=
      AssistantDestination(AssistantDestinationDto().apply { type = "assistant" }).apply(block).dto
  }
}

interface AssistantDestinationUnion {
  var assistantName: String
  var message: String
  var description: String
}

@AssistantDslMarker
data class AssistantDestination internal constructor(internal val dto: AssistantDestinationDto) :
  AssistantDestinationUnion by dto
