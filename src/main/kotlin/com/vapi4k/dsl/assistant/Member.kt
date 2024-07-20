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

import com.vapi4k.responses.assistant.MemberDto

@AssistantDslMarker
data class Member(
  internal val members: Members,
  internal val memberDto: MemberDto,
) {
  // errorMsg prevents further assistant or assistantId assignments
  private var errorMsg = ""

  private fun checkIfDeclared(newStr: String) = if (errorMsg.isNotEmpty()) error(errorMsg) else errorMsg = newStr

  fun assistantId(block: AssistantId.() -> Unit) {
    checkIfDeclared("Member already has an assistantId assigned")
    AssistantId(memberDto).apply(block)
  }

  fun assistant(block: Assistant.() -> Unit) {
    checkIfDeclared("Member already has an assistant assigned")
    Assistant(members.squad.request, memberDto.assistant, memberDto.assistantOverrides).apply(block)
  }

  fun destinations(block: AssistantDestinations.() -> Unit) {
    AssistantDestinations(this, memberDto).apply(block)
  }

  @AssistantDslMarker
  data class AssistantId internal constructor(val memberDto: MemberDto) {
    var id
      get() = memberDto.assistantId
      set(value) {
        memberDto.assistantId = value
      }
  }
}
