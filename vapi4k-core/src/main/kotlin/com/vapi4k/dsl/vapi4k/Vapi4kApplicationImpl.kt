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

import com.vapi4k.api.assistant.AssistantResponse
import com.vapi4k.api.tools.TransferDestinationResponse
import com.vapi4k.api.tools.enums.ToolType
import com.vapi4k.api.vapi4k.AssistantRequestContext
import com.vapi4k.api.vapi4k.TransferDestinationRequestContext
import com.vapi4k.api.vapi4k.Vapi4kApplication
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.dsl.assistant.AssistantResponseImpl
import com.vapi4k.dsl.tools.ToolCache
import com.vapi4k.dsl.tools.TransferToolImpl
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.responses.AssistantRequestResponseDto
import com.vapi4k.utils.DslUtils
import com.vapi4k.utils.common.Utils.isNull
import com.vapi4k.utils.enums.ServerRequestType
import com.vapi4k.utils.envvar.CoreEnvVars
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

class Vapi4kApplicationImpl internal constructor() : Vapi4kApplication {
  internal val applicationId = DslUtils.getRandomSecret(10).toApplicationId()
  internal val toolCache = ToolCache { serverPath }
  internal var assistantRequest: (suspend AssistantResponse.() -> Unit)? = null
  internal var transferDestinationRequest: (suspend TransferDestinationResponse.() -> Unit)? = null

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()
  internal var eocrCacheRemovalEnabled = true
  internal val serverPathAsSegment get() = serverPath.removePrefix("/")

  override var serverPath = CoreEnvVars.defaultServerPath
  override var serverSecret = ""

  override fun onAssistantRequest(block: suspend AssistantResponse.() -> Unit) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per vapi4kApplication{}")
  }

  override fun onTransferDestinationRequest(block: suspend TransferDestinationResponse.() -> Unit) {
    if (transferDestinationRequest.isNull())
      transferDestinationRequest = block
    else
      error("onTransferDestinationRequest{} can be called only once per vapi4kApplication{}")
  }

  override fun onAllRequests(block: suspend (request: JsonElement) -> Unit) {
    applicationAllRequests += block
  }

  override fun onRequest(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement) -> Unit,
  ) {
    applicationPerRequests += requestType to block
    requestTypes.forEach { applicationPerRequests += it to block }
  }

  override fun onAllResponses(
    block: suspend (
      requestType: ServerRequestType,
      response: JsonElement,
      elapsed: Duration,
    ) -> Unit,
  ) {
    applicationAllResponses += block
  }

  override fun onResponse(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (requestType: ServerRequestType, request: JsonElement, elapsed: Duration) -> Unit,
  ) {
    applicationPerResponses += requestType to block
    requestTypes.forEach { applicationPerResponses += it to block }
  }

  internal suspend fun getAssistantResponse(request: JsonElement): AssistantRequestResponseDto {
    val assistantRequestContext = AssistantRequestContext(this, request)
    val assistantResponse = AssistantResponseImpl(assistantRequestContext)
    assistantRequest?.invoke(assistantResponse) ?: error("onAssistantRequest{} not called")
    return if (assistantResponse.isAssigned)
      assistantResponse.assistantRequestResponse
    else
      error("onAssistantRequest{} is missing an assistant{}, assistantId{}, squad{}, or squadId{} declaration")
  }

  internal suspend fun getTransferDestinationResponse(request: JsonElement): ToolDto {
    val toolDto = ToolDto(ToolType.TRANSFER_CALL)
    val transferTool = TransferToolImpl("onTransferDestinationRequest", toolDto).also {
      it.transferDestinationRequest = TransferDestinationRequestContext(this, request)
    }
    transferDestinationRequest?.invoke(transferTool) ?: error("onTransferDestinationRequest{} not called")
    return toolDto
  }
}
