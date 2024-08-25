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

package com.vapi4k.dsl.call

import com.vapi4k.api.assistant.Assistant
import com.vapi4k.api.assistant.AssistantId
import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.api.call.Customer
import com.vapi4k.api.call.OutboundCall
import com.vapi4k.api.squad.Squad
import com.vapi4k.api.squad.SquadId
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.assistant.AssistantIdImpl
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.assistant.AssistantOverridesImpl
import com.vapi4k.dsl.call.VapiApiImpl.Companion.outboundRequestContext
import com.vapi4k.dsl.squad.SquadIdImpl
import com.vapi4k.dsl.squad.SquadImpl
import com.vapi4k.dtos.api.OutboundCallRequestDto
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.DuplicateInvokeChecker

interface OutboundCallProperties {
  var phoneNumberId: String
}

class OutboundCallImpl internal constructor(
  private val sessionCacheId: SessionCacheId,
  private val assistantCacheIdSource: AssistantCacheIdSource,
  private val dto: OutboundCallRequestDto,
) : OutboundCallProperties by dto,
  OutboundCall {
  private val assistantChecker = DuplicateInvokeChecker()
  private val overridesChecker = DuplicateInvokeChecker()

  override fun assistantId(block: AssistantId.() -> Unit): AssistantId {
    assistantChecker.check("assistantId{} already called")
    return AssistantIdImpl(outboundRequestContext, sessionCacheId, assistantCacheIdSource, dto).apply(block)
  }

  override fun assistant(block: Assistant.() -> Unit): Assistant {
    assistantChecker.check("assistant{} already called")
    return with(dto) {
      AssistantImpl(
        outboundRequestContext,
        sessionCacheId,
        assistantCacheIdSource,
        assistantDto,
        assistantOverridesDto,
      )
        .apply(block)
        .apply {
          assistantDto.updated = true
          assistantDto.verifyValues()

          assistantDto.serverUrl = assistantRequestContext.application.serverUrl
        }
    }
  }

  override fun squadId(block: SquadId.() -> Unit): SquadId {
    assistantChecker.check("squadId{} already called")
    return SquadIdImpl(outboundRequestContext, dto).apply(block)
  }

  override fun squad(block: Squad.() -> Unit): Squad {
    assistantChecker.check("squad{} already called")
    return with(dto) {
      SquadImpl(outboundRequestContext, sessionCacheId, assistantCacheIdSource, squadDto).apply(block)
    }
  }

  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides {
    overridesChecker.check("assistantOverrides{} already called")
    return if (dto.assistantDto.updated || dto.assistantId.isNotEmpty())
      AssistantOverridesImpl(
        outboundRequestContext,
        sessionCacheId,
        assistantCacheIdSource,
        dto.assistantOverridesDto,
      ).apply(block)
    else
      error("assistant{} or assistantId{} must be called before assistantOverrides{}")
  }

  override fun customer(block: Customer.() -> Unit): Customer = Customer(dto.customerDto).apply(block)
}
