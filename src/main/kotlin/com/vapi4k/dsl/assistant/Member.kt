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
import com.vapi4k.responses.assistant.AssistantDto
import com.vapi4k.responses.assistant.MemberDto

data class Member(internal val members: Members, internal val memberDto: MemberDto) {
  private var errorMsg = ""
  fun assistantId(block: AssistantId.() -> Unit) {
    if (errorMsg.isNotEmpty()) {
      error(errorMsg)
    } else {
      errorMsg = "Member already has an assistantId assigned"
      AssistantId(memberDto).apply(block)
    }
  }

  fun assistant(block: Assistant.() -> Unit) {
    if (errorMsg.isNotEmpty()) {
      error(errorMsg)
    } else {
      errorMsg = "Member already has an assistant assigned"
      memberDto.assistant = AssistantDto().also { assistantDto ->
        Assistant(members.squad.request, assistantDto).apply(block)
      }
    }
  }

  fun destinations(block: AssistantDestinations.() -> Unit) {
    AssistantDestinations(this, memberDto).apply(block)
  }

  data class AssistantId internal constructor(val memberDto: MemberDto) {
    var id
      get() = memberDto.assistantId
      set(value) {
        memberDto.assistantId = value
      }
  }

  data class AssistantDestinations internal constructor(val member: Member, val memberDto: MemberDto) {
    fun destination(block: AssistantDestination.() -> Unit) {
      memberDto.assistantDestinations +=
        AssistantDestination(AssistantDestinationDto().apply { type = "assistant" })
          .apply(block).dto
    }
  }

  interface AssistantDestinationUnion {
    //    var type: String
    var assistantName: String
    var message: String
    var description: String
  }

  data class AssistantDestination internal constructor(
    val dto: AssistantDestinationDto
  ) : AssistantDestinationUnion by dto
}
