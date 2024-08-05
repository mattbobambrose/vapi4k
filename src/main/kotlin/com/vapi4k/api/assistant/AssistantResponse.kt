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

package com.vapi4k.api.assistant

import com.vapi4k.api.destination.NumberDestination
import com.vapi4k.api.destination.NumberDestinationImpl
import com.vapi4k.api.destination.SipDestination
import com.vapi4k.api.destination.SipDestinationImpl
import com.vapi4k.api.squad.Squad
import com.vapi4k.api.squad.SquadId
import com.vapi4k.api.vapi4k.RequestContext
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantIdImpl
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.squad.SquadIdImpl
import com.vapi4k.dsl.squad.SquadImpl
import com.vapi4k.dtos.api.destination.NumberDestinationDto
import com.vapi4k.dtos.api.destination.SipDestinationDto
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.DuplicateChecker
import com.vapi4k.utils.JsonElementUtils.sessionCacheId

@AssistantDslMarker
class AssistantResponse(
  val requestContext: RequestContext,
) {
  private val checker = DuplicateChecker()
  internal lateinit var assistantRequestResponse: AssistantRequestResponse
  internal val isAssigned: Boolean
    get() = this::assistantRequestResponse.isInitialized

  fun assistant(block: Assistant.() -> Unit) {
    checker.check("An assistant{} is already declared")
    assistantRequestResponse = AssistantRequestResponse().apply {
      val sessionCacheId = requestContext.assistantRequest.sessionCacheId
      val assistantCacheIdSource = AssistantCacheIdSource()
      AssistantImpl(requestContext, sessionCacheId, assistantCacheIdSource, assistantDto, assistantOverridesDto)
        .apply(block)
        .apply {
          assistantDto.updated = true
          assistantDto.verifyValues()
        }
    }
  }

  fun assistantId(block: AssistantId.() -> Unit) {
    checker.check("An assistantId{} is already declared")
    assistantRequestResponse = AssistantRequestResponse().apply {
      val sessionCacheId = requestContext.assistantRequest.sessionCacheId
      val assistantCacheIdSource = AssistantCacheIdSource()
      AssistantIdImpl(requestContext, sessionCacheId, assistantCacheIdSource, this).apply(block)
    }
  }

  fun squad(block: Squad.() -> Unit) {
    checker.check("An squad{} is already declared")
    assistantRequestResponse = AssistantRequestResponse().apply {
      val sessionCacheId = requestContext.assistantRequest.sessionCacheId
      val assistantCacheIdSource = AssistantCacheIdSource()
      SquadImpl(requestContext, sessionCacheId, assistantCacheIdSource, squadDto).apply(block)
    }
  }

  fun squadId(block: SquadId.() -> Unit) {
    checker.check("An squadId{} is already declared")
    assistantRequestResponse = AssistantRequestResponse().apply {
      SquadIdImpl(requestContext, this).apply(block)
    }
  }

  fun numberDestination(block: NumberDestination.() -> Unit) {
    checker.check("numberDestination{} already declared")
    assistantRequestResponse = AssistantRequestResponse().apply {
      val numDto = NumberDestinationDto()
      destination = numDto
      NumberDestinationImpl(numDto).apply(block)
    }
  }

  fun sipDestination(block: SipDestination.() -> Unit) {
    checker.check("sipDestination{} already declared")
    assistantRequestResponse = AssistantRequestResponse().apply {
      val sipDto = SipDestinationDto()
      destination = sipDto
      SipDestinationImpl(sipDto).apply(block)
    }
  }
}
