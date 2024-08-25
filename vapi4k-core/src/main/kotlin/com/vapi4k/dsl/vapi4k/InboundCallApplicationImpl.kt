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

import com.vapi4k.api.assistant.InboundCallAssistantResponse
import com.vapi4k.api.vapi4k.InboundCallApplication
import com.vapi4k.dsl.assistant.InboundCallAssistantResponseImpl
import com.vapi4k.utils.common.Utils.isNull
import kotlinx.serialization.json.JsonElement

class InboundCallApplicationImpl internal constructor() :
  AbstractApplicationImpl(ApplicationType.INBOUND_CALL),
  InboundCallApplication {
  private var assistantRequest: (suspend InboundCallAssistantResponse.(JsonElement) -> Unit)? = null

  override fun onAssistantRequest(block: suspend InboundCallAssistantResponse.(JsonElement) -> Unit) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per inboundCallApplication{}")
  }

  internal suspend fun getAssistantResponse(request: JsonElement) =
    assistantRequest.let { func ->
      if (func.isNull()) {
        error("onAssistantRequest{} not called")
      } else {
        val assistantRequestContext = AssistantRequestContext(this, request)
        val assistantResponse = InboundCallAssistantResponseImpl(assistantRequestContext)
        func.invoke(assistantResponse, request)
        if (!assistantResponse.isAssigned)
          error("onAssistantRequest{} is missing a call to assistant{}, assistantId{}, squad{}, or squadId{}")
        else
          assistantResponse.assistantRequestResponse
      }
    }
}
