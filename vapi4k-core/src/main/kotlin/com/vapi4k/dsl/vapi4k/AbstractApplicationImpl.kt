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

import com.vapi4k.api.tools.TransferDestinationResponse
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.AssistantId
import com.vapi4k.common.CacheKey.Companion.cacheKeyValue
import com.vapi4k.common.CoreEnvVars.defaultServerPath
import com.vapi4k.common.CoreEnvVars.vapi4kBaseUrl
import com.vapi4k.common.FunctionName
import com.vapi4k.common.QueryParams.SECRET_PARAM
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.tools.ManualToolCache
import com.vapi4k.dsl.tools.ServiceCache
import com.vapi4k.dsl.tools.TransferDestinationImpl
import com.vapi4k.dtos.tools.TransferMessageResponseDto
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.MiscUtils.removeEnds
import com.vapi4k.utils.common.Utils.isNull
import com.vapi4k.utils.enums.ServerRequestType
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

abstract class AbstractApplicationImpl(
  val applicationType: ApplicationType,
) {
  internal val applicationId = getRandomSecret(15).toApplicationId()
  internal val serviceCache = ServiceCache { fullServerPath }
  internal val functionCache = ServiceCache { fullServerPath }
  internal val manualToolCache = ManualToolCache { fullServerPath }

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  private val assistantIds = mutableListOf<AssistantId>()

  internal var eocrCacheRemovalEnabled = true

  private var transferDestinationRequest: (suspend TransferDestinationResponse.(JsonElement) -> Unit)? = null

  var serverPath = defaultServerPath
  var serverSecret = ""

  internal val serverUrl get() = "$vapi4kBaseUrl/$fullServerPath"
  internal val serverPathAsSegment get() = serverPath.removeEnds("/")
  internal val fullServerPath: String get() = "${applicationType.pathPrefix}/$serverPathAsSegment"
  internal val fullServerPathWithSecretAsQueryParam: String
    get() = "${fullServerPath}${serverSecret.let { if (it.isBlank()) "" else "?$SECRET_PARAM=$it" }}"

  internal fun addAssistantId(assistantId: AssistantId) {
    assistantIds += assistantId
  }

  internal fun containsServiceToolInCache(
    sessionId: SessionId,
    assistantId: AssistantId,
    funcName: FunctionName,
  ): Boolean =
    serviceCache.containsIds(sessionId, assistantId) &&
      serviceCache.getFromCache(sessionId, assistantId).containsFunction(funcName)

  internal fun containsManualToolInCache(funcName: FunctionName): Boolean = manualToolCache.containsTool(funcName)

  internal fun containsFunctionInCache(
    sessionId: SessionId,
    assistantId: AssistantId,
    funcName: FunctionName,
  ) = functionCache.containsIds(sessionId, assistantId) &&
    functionCache.getFromCache(sessionId, assistantId).containsFunction(funcName)

  internal fun getServiceToolFromCache(
    sessionId: SessionId,
    assistantId: AssistantId,
    funcName: FunctionName,
  ) = serviceCache.getFromCache(sessionId, assistantId).getFunction(funcName)

  internal fun getFunctionFromCache(
    sessionId: SessionId,
    assistantId: AssistantId,
    funcName: FunctionName,
  ) = functionCache.getFromCache(sessionId, assistantId).getFunction(funcName)

  fun onAllRequests(block: suspend (request: JsonElement) -> Unit) {
    applicationAllRequests += block
  }

  fun onRequest(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement) -> Unit,
  ) {
    applicationPerRequests += requestType to block
    requestTypes.forEach { applicationPerRequests += it to block }
  }

  fun onAllResponses(
    block: suspend (
      requestType: ServerRequestType,
      response: JsonElement,
      elapsed: Duration,
    ) -> Unit,
  ) {
    applicationAllResponses += block
  }

  fun onResponse(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (requestType: ServerRequestType, request: JsonElement, elapsed: Duration) -> Unit,
  ) {
    applicationPerResponses += requestType to block
    requestTypes.forEach { applicationPerResponses += it to block }
  }

  internal suspend fun getTransferDestinationResponse(
    request: JsonElement,
    sessionId: SessionId,
    assistantId: AssistantId,
  ): TransferMessageResponseDto =
    transferDestinationRequest.let { func ->
      if (func.isNull()) {
        error("onTransferDestinationRequest{} not called")
      } else {
        val responseDto = TransferMessageResponseDto()
        val destImpl = TransferDestinationImpl("onTransferDestinationRequest", responseDto)
        func.invoke(destImpl, request)
        if (responseDto.messageResponse.destination.isNull())
          error(
            "onTransferDestinationRequest{} is missing a call to numberDestination{}, sipDestination{}, " +
              "or assistantDestination{}",
          )
        responseDto
      }
    }

  fun onTransferDestinationRequest(block: suspend TransferDestinationResponse.(JsonElement) -> Unit) {
    if (transferDestinationRequest.isNull())
      transferDestinationRequest = block
    else
      error("onTransferDestinationRequest{} can be called only once per inboundCallApplication{}")
  }

  fun processEOCRMessage(
    sessionId: SessionId,
    assistantId: AssistantId,
  ) {
    // Need to count the number of functions available to prevent error if no funcs exist
    if (eocrCacheRemovalEnabled) {
      val cacheKey = cacheKeyValue(sessionId, assistantId)
      if (serviceCache.isNotEmpty()) {
        serviceCache.removeFromCache(sessionId, assistantId) { funcInfo ->
          logger.info { "EOCR removed ${funcInfo.size} serviceTool cache items [${funcInfo.ageSecs}] " }
        } ?: logger.warn { "EOCR unable to find and remove serviceTool cache entry [$cacheKey]" }
      }

      if (functionCache.isNotEmpty()) {
        functionCache.removeFromCache(sessionId, assistantId) { funcInfo ->
          logger.info { "EOCR removed ${funcInfo.size} function cache items [${funcInfo.ageSecs}] " }
        } ?: logger.warn { "EOCR unable to find and remove function cache entry [$cacheKey]" }
      }
    }
  }
}
