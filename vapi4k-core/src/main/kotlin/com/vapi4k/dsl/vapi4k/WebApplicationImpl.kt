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
import com.vapi4k.api.buttons.ButtonConfig
import com.vapi4k.api.tools.RequestContext
import com.vapi4k.api.vapi4k.WebApplication
import com.vapi4k.dsl.assistant.WebAssistantResponseImpl
import com.vapi4k.dsl.buttons.ButtonConfigImpl
import com.vapi4k.server.RequestContextImpl
import com.vapi4k.utils.DuplicateInvokeChecker
import com.vapi4k.utils.common.Utils.isNull

class WebApplicationImpl internal constructor() :
  AbstractApplicationImpl(ApplicationType.WEB),
  WebApplication {
  private var assistantRequest: (suspend WebAssistantResponse.(RequestContext) -> Unit)? = null
  private var buttonConfigBlock: ButtonConfig.(RequestContext) -> Unit = {}

  override fun onAssistantRequest(block: suspend WebAssistantResponse.(RequestContext) -> Unit) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per inboundCallApplication{}")
  }

  override suspend fun getAssistantResponse(requestContext: RequestContextImpl) =
    assistantRequest.let { func ->
      if (func.isNull()) {
        error("onAssistantRequest{} not called")
      } else {
        val assistantResponse = WebAssistantResponseImpl(requestContext)
        func.invoke(assistantResponse, requestContext)
        if (assistantResponse.isAssigned) {
          assignButtonConfig(assistantResponse, requestContext)
          assistantResponse.assistantRequestResponse
        } else {
          error("onAssistantRequest{} is missing a call to assistant{}, assistantId{}, squad{}, or squadId{}")
        }
      }
    }

  private fun assignButtonConfig(
    assistantResponse: WebAssistantResponseImpl,
    requestContext: RequestContextImpl,
  ) {
    val buttonConfigDuplicateChecker = DuplicateInvokeChecker()
    buttonConfigDuplicateChecker.check("buttonConfig{} was already called")
    ButtonConfigImpl(assistantResponse.messageResponse.buttonConfigDto).apply { buttonConfigBlock(requestContext) }
  }

  override fun buttonConfig(block: ButtonConfig.(RequestContext) -> Unit) {
    buttonConfigBlock = block
  }
}
