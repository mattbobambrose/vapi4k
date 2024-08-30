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
import com.vapi4k.common.QueryParams.APPLICATION_ID
import com.vapi4k.common.QueryParams.ASSISTANT_ID
import com.vapi4k.common.QueryParams.SESSION_ID
import com.vapi4k.dsl.squad.SquadIdImpl
import com.vapi4k.dsl.squad.SquadImpl
import com.vapi4k.dsl.vapi4k.ApplicationType
import com.vapi4k.dsl.vapi4k.AssistantRequestContext
import com.vapi4k.responses.AssistantMessageResponseDto
import com.vapi4k.utils.AssistantIdSource
import com.vapi4k.utils.DuplicateInvokeChecker
import com.vapi4k.utils.MiscUtils.appendQueryParams

abstract class AbstractAssistantResponseImpl(
  internal val assistantRequestContext: AssistantRequestContext,
) {
  internal val duplicateChecker = DuplicateInvokeChecker()
  internal val assistantRequestResponse = AssistantMessageResponseDto()

  internal val isAssigned get() = duplicateChecker.wasCalled

  fun assistant(block: Assistant.() -> Unit): Assistant {
    duplicateChecker.check("assistant{} was already called")
    return assistantRequestResponse.run {
      val sessionId = assistantRequestContext.sessionId
      val assistantIdSource = AssistantIdSource()
      AssistantImpl(
        assistantRequestContext,
        sessionId,
        assistantIdSource,
        messageResponse.assistantDto,
        messageResponse.assistantOverridesDto,
      ).apply(block)
        .apply {
          messageResponse.assistantDto.updated = true
          messageResponse.assistantDto.verifyValues()

          if (assistantRequestContext.application.applicationType in assignUrlTypes) {
            messageResponse.assistantDto.serverUrl =
              assistantRequestContext.application.serverUrl.appendQueryParams(
                APPLICATION_ID to assistantRequestContext.application.applicationId.value,
                SESSION_ID to sessionId.value,
                ASSISTANT_ID to assistantId.value,
              )
          }
        }
    }
  }

  fun assistantId(block: AssistantId.() -> Unit): AssistantId {
    duplicateChecker.check("assistantId{} was already called")
    return assistantRequestResponse.run {
      val sessionId = assistantRequestContext.sessionId
      val assistantIdSource = AssistantIdSource()
      AssistantIdImpl(assistantRequestContext, sessionId, assistantIdSource, messageResponse).apply(block)
    }
  }

  fun squad(block: Squad.() -> Unit): Squad {
    duplicateChecker.check("squad{} was already called")
    return assistantRequestResponse.run {
      val sessionId = assistantRequestContext.sessionId
      val assistantIdSource = AssistantIdSource()
      SquadImpl(assistantRequestContext, sessionId, assistantIdSource, messageResponse.squadDto).apply(block)
    }
  }

  fun squadId(block: SquadId.() -> Unit): SquadId {
    duplicateChecker.check("squadId{} was already called")
    return assistantRequestResponse.run {
      SquadIdImpl(assistantRequestContext, messageResponse).apply(block)
    }
  }

  companion object {
    internal val assignUrlTypes = listOf(ApplicationType.WEB, ApplicationType.OUTBOUND_CALL)
  }
}
