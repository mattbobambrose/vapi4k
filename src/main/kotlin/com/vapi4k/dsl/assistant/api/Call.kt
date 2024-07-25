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

import com.vapi4k.common.DuplicateChecker
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.assistant.Assistant
import com.vapi4k.dsl.assistant.assistant.AssistantId
import com.vapi4k.dsl.assistant.assistant.AssistantIdImpl
import com.vapi4k.dsl.assistant.assistant.AssistantImpl
import com.vapi4k.dsl.assistant.assistant.AssistantOverrides
import com.vapi4k.dsl.assistant.assistant.AssistantOverridesImpl
import com.vapi4k.dsl.assistant.squad.Squad
import com.vapi4k.dsl.assistant.squad.SquadId
import com.vapi4k.dsl.assistant.squad.SquadIdImpl
import com.vapi4k.dsl.assistant.squad.SquadImpl
import com.vapi4k.responses.api.CallRequestDto
import com.vapi4k.utils.JsonElementUtils.emptyJsonElement

interface CallProperties {
  var phoneNumberId: String
}

@AssistantDslMarker
interface Call : CallProperties {
  fun assistantId(block: AssistantId.() -> Unit): AssistantId
  fun assistant(block: Assistant.() -> Unit): Assistant
  fun squadId(block: SquadId.() -> Unit): SquadId
  fun squad(block: Squad.() -> Unit): Squad
  fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides
  fun customer(block: Customer.() -> Unit): Customer
}

data class CallImpl internal constructor(
  private val sessionId: SessionId,
  internal val dto: CallRequestDto,
) : CallProperties by dto, Call {
  private val assistantChecker = DuplicateChecker()
  private val overridesChecker = DuplicateChecker()

  override fun assistantId(block: AssistantId.() -> Unit): AssistantId {
    assistantChecker.check("assistantId{} already called")
    return AssistantIdImpl(emptyJsonElement(), sessionId, dto).apply(block)
  }

  override fun assistant(block: Assistant.() -> Unit): Assistant {
    assistantChecker.check("assistant{} already called")
    return with(dto) {
      AssistantImpl(emptyJsonElement(), sessionId, assistantDto, assistantOverridesDto)
        .apply(block)
        .apply {
          assistantDto.updated = true
          assistantDto.verifyValues()
        }
    }
  }

  override fun squadId(block: SquadId.() -> Unit): SquadId {
    assistantChecker.check("squadId{} already called")
    return SquadIdImpl(emptyJsonElement(), dto).apply(block)
  }


  override fun squad(block: Squad.() -> Unit): Squad {
    assistantChecker.check("squad{} already called")
    return with(dto) {
      SquadImpl(emptyJsonElement(), sessionId, squadDto).apply(block)
    }
  }

  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides {
    overridesChecker.check("assistantOverrides{} already called")
    return if (dto.assistantDto.updated || dto.assistantId.isNotEmpty())
      with(dto) {
        AssistantOverridesImpl(emptyJsonElement(), sessionId, assistantOverridesDto).apply(block)
      }
    else
      error("assistant{} or assistantId{} must be called before assistantOverrides{}")
  }

  override fun customer(block: Customer.() -> Unit): Customer = Customer(dto.customerDto).apply(block)
}
