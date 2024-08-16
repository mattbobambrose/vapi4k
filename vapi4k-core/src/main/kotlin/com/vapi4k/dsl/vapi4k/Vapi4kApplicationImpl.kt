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
import com.vapi4k.api.vapi4k.Vapi4kApplication
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.CoreEnvVars.defaultServerPath
import com.vapi4k.common.CoreEnvVars.serverBaseUrl
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.assistant.AssistantResponseImpl
import com.vapi4k.dsl.tools.ManualToolCache
import com.vapi4k.dsl.tools.ServiceCache
import com.vapi4k.dsl.tools.TransferDestinationImpl
import com.vapi4k.dtos.tools.TransferMessageResponseDto
import com.vapi4k.utils.DslUtils
import com.vapi4k.utils.common.Utils.isNull
import com.vapi4k.utils.enums.ServerRequestType
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

class Vapi4kApplicationImpl internal constructor() : Vapi4kApplication {
  internal val applicationId = DslUtils.getRandomSecret(10).toApplicationId()
  internal val serviceToolCache = ServiceCache { serverPath }
  internal val functionCache = ServiceCache { serverPath }
  internal val manualToolCache = ManualToolCache { serverPath }

  private lateinit var assistantRequest: (suspend AssistantResponse.(JsonElement) -> Unit)
  private lateinit var transferDestinationRequest: (suspend TransferDestinationResponse.(JsonElement) -> Unit)

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()
  internal var eocrCacheRemovalEnabled = true
  internal val serverPathAsSegment get() = serverPath.removePrefix("/").removeSuffix("/")

  override var serverPath = defaultServerPath
  override var serverSecret = ""

  internal val fqServerPath get() = "$serverBaseUrl/$serverPathAsSegment"

  internal fun containsServiceToolInCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) = serviceToolCache.containsSessionCacheId(sessionCacheId) &&
    serviceToolCache.getFromCache(sessionCacheId).containsFunction(funcName)

  internal fun containsFunctionInCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) = functionCache.containsSessionCacheId(sessionCacheId) &&
    functionCache.getFromCache(sessionCacheId).containsFunction(funcName)

  internal fun getServiceToolFromCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) =
    serviceToolCache.getFromCache(sessionCacheId).getFunction(funcName)

  internal fun getFunctionFromCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) =
    functionCache.getFromCache(sessionCacheId).getFunction(funcName)

  override fun onAssistantRequest(block: suspend AssistantResponse.(JsonElement) -> Unit) {
    if (!::assistantRequest.isInitialized)
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per vapi4kApplication{}")
  }

  override fun onTransferDestinationRequest(block: suspend TransferDestinationResponse.(JsonElement) -> Unit) {
    if (!::transferDestinationRequest.isInitialized)
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

  internal suspend fun getAssistantResponse(request: JsonElement) =
    if (!::assistantRequest.isInitialized) {
      error("onAssistantRequest{} not called")
    } else {
      val assistantRequestContext = AssistantRequestContext(this, request)
      val assistantResponse = AssistantResponseImpl(assistantRequestContext)
      assistantRequest.invoke(assistantResponse, request)
      if (!assistantResponse.isAssigned)
        error("onAssistantRequest{} is missing an assistant{}, assistantId{}, squad{}, or squadId{} declaration")
      else
        assistantResponse.assistantRequestResponse
    }

  internal suspend fun getTransferDestinationResponse(request: JsonElement) =
    if (!::transferDestinationRequest.isInitialized) {
      error("onTransferDestinationRequest{} not declared")
    } else {
      val responseDto = TransferMessageResponseDto()
      val destImpl = TransferDestinationImpl("onTransferDestinationRequest", responseDto)
      transferDestinationRequest.invoke(destImpl, request)
      if (responseDto.messageResponse.destination.isNull())
        error(
          "onTransferDestinationRequest{} is missing a numberDestination{}, sipDestination{}, " +
            "or assistantDestination{} declaration",
        )
      responseDto
    }
}
