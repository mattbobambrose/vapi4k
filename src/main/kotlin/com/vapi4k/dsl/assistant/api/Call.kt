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

package com.vapi4k.dsl.assistant.api

import com.vapi4k.common.CacheId
import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantId
import com.vapi4k.dsl.assistant.AssistantOverrides
import com.vapi4k.dsl.assistant.squad.Squad
import com.vapi4k.dsl.assistant.squad.SquadId
import com.vapi4k.responses.api.CallRequestDto
import com.vapi4k.utils.JsonElementUtils.emptyJsonElement

interface CallUnion {
  var phoneNumberId: String
}

@AssistantDslMarker
class Call internal constructor(
  private val cacheId: CacheId,
  internal val callRequest: CallRequestDto,
) : CallUnion by callRequest {
  private var primaryErorMsg = ""
  private var overridesErorMsg = ""

  private fun checkIfPrimaryDeclared(newStr: String) =
    if (primaryErorMsg.isNotEmpty()) error(primaryErorMsg) else primaryErorMsg = newStr

  private fun checkIfOverridesDeclared(newStr: String) =
    if (overridesErorMsg.isNotEmpty()) error(overridesErorMsg) else overridesErorMsg = newStr

  fun assistantId(block: AssistantId.() -> Unit) {
    checkIfPrimaryDeclared("assistantId{} already called")
    AssistantId(emptyJsonElement(), cacheId, callRequest).apply(block)
  }

  fun assistant(block: Assistant.() -> Unit) {
    checkIfPrimaryDeclared("assistant{} already called")
    with(callRequest) {
      Assistant(emptyJsonElement(), cacheId, assistantDto, assistantOverridesDto)
        .apply(block)
        .apply {
          assistantDto.updated = true
          assistantDto.verifyValues()
        }

    }
  }

  fun assistantOverrides(block: AssistantOverrides.() -> Unit) {
    checkIfOverridesDeclared("assistantOverrides{} already called")
    if (callRequest.assistantDto.updated || callRequest.assistantId.isNotEmpty())
      with(callRequest) {
        AssistantOverrides(emptyJsonElement(), cacheId, assistantOverridesDto).apply(block)
      }
    else
      error("assistant{} or assistantId{} must be called before assistantOverrides{}")
  }

  fun squadId(block: SquadId.() -> Unit) {
    checkIfPrimaryDeclared("squadId{} already called")
    SquadId(emptyJsonElement(), callRequest).apply(block)
  }


  fun squad(block: Squad.() -> Unit) {
    checkIfPrimaryDeclared("squad{} already called")
    with(callRequest) {
      Squad(emptyJsonElement(), cacheId, squadDto).apply(block)
    }
  }


  fun customer(block: Customer.() -> Unit) {
    Customer(callRequest.customerDto).apply(block)
  }
}
