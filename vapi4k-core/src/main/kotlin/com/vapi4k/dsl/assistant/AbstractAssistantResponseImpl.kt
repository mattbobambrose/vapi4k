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

import com.vapi4k.api.assistant.Assistant
import com.vapi4k.api.assistant.AssistantId
import com.vapi4k.api.squad.Squad
import com.vapi4k.api.squad.SquadId
import com.vapi4k.dsl.squad.SquadIdImpl
import com.vapi4k.dsl.squad.SquadImpl
import com.vapi4k.dsl.vapi4k.AssistantRequestContext
import com.vapi4k.responses.AssistantMessageResponseDto
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.DuplicateInvokeChecker
import com.vapi4k.utils.JsonElementUtils.sessionCacheId

abstract class AbstractAssistantResponseImpl(
  internal val assistantRequestContext: AssistantRequestContext,
) {
  internal val duplicateChecker = DuplicateInvokeChecker()
  internal val assistantRequestResponse = AssistantMessageResponseDto()

  internal val isAssigned get() = duplicateChecker.wasCalled

  fun assistant(block: Assistant.() -> Unit) {
    duplicateChecker.check("An assistant{} is already declared")
    assistantRequestResponse.apply {
      val sessionCacheId = assistantRequestContext.request.sessionCacheId
      val assistantCacheIdSource = AssistantCacheIdSource()
      AssistantImpl(
        assistantRequestContext,
        sessionCacheId,
        assistantCacheIdSource,
        messageResponse.assistantDto,
        messageResponse.assistantOverridesDto,
      ).apply(block)
        .apply {
          messageResponse.assistantDto.updated = true
          messageResponse.assistantDto.verifyValues()
        }
    }
  }

  fun assistantId(block: AssistantId.() -> Unit) {
    duplicateChecker.check("An assistantId{} is already declared")
    assistantRequestResponse.apply {
      val sessionCacheId = assistantRequestContext.request.sessionCacheId
      val assistantCacheIdSource = AssistantCacheIdSource()
      AssistantIdImpl(assistantRequestContext, sessionCacheId, assistantCacheIdSource, messageResponse).apply(block)
    }
  }

  fun squad(block: Squad.() -> Unit) {
    duplicateChecker.check("An squad{} is already declared")
    assistantRequestResponse.apply {
      val sessionCacheId = assistantRequestContext.request.sessionCacheId
      val assistantCacheIdSource = AssistantCacheIdSource()
      SquadImpl(assistantRequestContext, sessionCacheId, assistantCacheIdSource, messageResponse.squadDto).apply(block)
    }
  }

  fun squadId(block: SquadId.() -> Unit) {
    duplicateChecker.check("An squadId{} is already declared")
    assistantRequestResponse.apply {
      SquadIdImpl(assistantRequestContext, messageResponse).apply(block)
    }
  }
}
