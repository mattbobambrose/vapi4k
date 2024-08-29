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

package com.vapi4k.dsl.vapi4k

import com.vapi4k.api.assistant.WebAssistantResponse
import com.vapi4k.api.vapi4k.WebApplication
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.assistant.WebAssistantResponseImpl
import com.vapi4k.responses.AssistantMessageResponseDto
import com.vapi4k.utils.JsonUtils.getSessionIdQueryParameter
import com.vapi4k.utils.common.Utils.isNull
import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.json.JsonElement

class WebApplicationImpl internal constructor() :
  AbstractApplicationImpl(ApplicationType.WEB),
  WebApplication {
  private var assistantRequest: (suspend WebAssistantResponse.(JsonElement) -> Unit)? = null

  override fun onAssistantRequest(block: suspend WebAssistantResponse.(JsonElement) -> Unit) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per inboundCallApplication{}")
  }

  internal suspend fun getAssistantResponse(
    request: JsonElement,
    sessionCacheId: SessionCacheId,
  ): AssistantMessageResponseDto =
    assistantRequest.let { requestFunc ->
      if (requestFunc.isNull()) {
        error("onAssistantRequest{} not called")
      } else {
        val assistantRequestContext = AssistantRequestContext(this, request, sessionCacheId)
        val assistantResponse = WebAssistantResponseImpl(assistantRequestContext)
        requestFunc.invoke(assistantResponse, request)
        if (!assistantResponse.isAssigned)
          error("onAssistantRequest{} is missing a call to assistant{}, assistantId{}, squad{}, or squadId{}")
        else
          assistantResponse.assistantRequestResponse
      }
    }

  override fun getSessionCacheId(
    call: ApplicationCall,
    request: JsonElement,
  ): SessionCacheId = call.getSessionIdQueryParameter()
}
