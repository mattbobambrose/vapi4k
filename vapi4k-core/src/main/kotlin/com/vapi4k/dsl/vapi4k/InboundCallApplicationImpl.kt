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
import com.vapi4k.api.tools.TransferDestinationResponse
import com.vapi4k.api.vapi4k.InboundCallApplication
import com.vapi4k.common.CoreEnvVars.serverBaseUrl
import com.vapi4k.common.Headers.VAPI4K_VALIDATE_HEADER
import com.vapi4k.common.Headers.VAPI4K_VALIDATE_VALUE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.dsl.assistant.InboundCallAssistantResponseImpl
import com.vapi4k.dsl.tools.TransferDestinationImpl
import com.vapi4k.dtos.tools.TransferMessageResponseDto
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.common.Utils.isNull
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement

class InboundCallApplicationImpl internal constructor() :
  AbstractApplicationImpl(ApplicationType.INBOUND_CALL),
  InboundCallApplication {
  private lateinit var assistantRequest: (suspend InboundCallAssistantResponse.(JsonElement) -> Unit)
  private lateinit var transferDestinationRequest: (suspend TransferDestinationResponse.(JsonElement) -> Unit)

  internal var eocrCacheRemovalEnabled = true

  override fun onAssistantRequest(block: suspend InboundCallAssistantResponse.(JsonElement) -> Unit) {
    if (!::assistantRequest.isInitialized)
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per inboundCallApplication{}")
  }

  override fun fetchContent(
    request: JsonElement,
    appName: String,
    secret: String,
  ): Pair<HttpStatusCode, String> =
    runBlocking {
      val url = "$serverBaseUrl/$appName"
      val response = httpClient.post(url) {
        contentType(Application.Json)
        headers.append(VAPI4K_VALIDATE_HEADER, VAPI4K_VALIDATE_VALUE)
        if (secret.isNotEmpty())
          headers.append(VAPI_SECRET_HEADER, secret)
        setBody(request)
      }
      response.status to response.bodyAsText()
    }

  override fun onTransferDestinationRequest(block: suspend TransferDestinationResponse.(JsonElement) -> Unit) {
    if (!::transferDestinationRequest.isInitialized)
      transferDestinationRequest = block
    else
      error("onTransferDestinationRequest{} can be called only once per inboundCallApplication{}")
  }

  internal suspend fun getAssistantResponse(request: JsonElement) =
    if (!::assistantRequest.isInitialized) {
      error("onAssistantRequest{} not called")
    } else {
      val assistantRequestContext = AssistantRequestContext(this, request)
      val assistantResponse = InboundCallAssistantResponseImpl(assistantRequestContext)
      assistantRequest.invoke(assistantResponse, request)
      if (!assistantResponse.isAssigned)
        error("onAssistantRequest{} is missing a call to assistant{}, assistantId{}, squad{}, or squadId{}")
      else
        assistantResponse.assistantRequestResponse
    }

  internal suspend fun getTransferDestinationResponse(request: JsonElement) =
    if (!::transferDestinationRequest.isInitialized) {
      error("onTransferDestinationRequest{} not called")
    } else {
      val responseDto = TransferMessageResponseDto()
      val destImpl = TransferDestinationImpl("onTransferDestinationRequest", responseDto)
      transferDestinationRequest.invoke(destImpl, request)
      if (responseDto.messageResponse.destination.isNull())
        error(
          "onTransferDestinationRequest{} is missing a call to numberDestination{}, sipDestination{}, " +
            "or assistantDestination{}",
        )
      responseDto
    }
}
