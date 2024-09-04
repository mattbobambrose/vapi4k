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

import com.vapi4k.api.assistant.OutboundCallAssistantResponse
import com.vapi4k.api.tools.RequestContext
import com.vapi4k.api.vapi4k.OutboundCallApplication
import com.vapi4k.dsl.assistant.OutboundCallAssistantResponseImpl
import com.vapi4k.responses.AssistantMessageResponse
import com.vapi4k.server.RequestContextImpl
import com.vapi4k.utils.common.Utils.isNull

class OutboundCallApplicationImpl internal constructor() :
  AbstractApplicationImpl(ApplicationType.OUTBOUND_CALL),
  OutboundCallApplication {
  private var assistantRequest: (suspend OutboundCallAssistantResponse.(RequestContext) -> Unit)? = null

  override fun onAssistantRequest(block: suspend OutboundCallAssistantResponse.(RequestContext) -> Unit) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per inboundCallApplication{}")
  }

  override suspend fun getAssistantResponse(requestContext: RequestContextImpl): AssistantMessageResponse =
    assistantRequest.let { func ->
      if (func.isNull()) {
        error("onAssistantRequest{} not called")
      } else {
        val assistantResponse = OutboundCallAssistantResponseImpl(requestContext)
        func.invoke(assistantResponse, requestContext)
        if (!assistantResponse.isAssigned)
          error("onAssistantRequest{} is missing a call to assistant{}, assistantId{}, squad{}, or squadId{}")
        else
          assistantResponse.assistantRequestResponse
      }
    }
}
