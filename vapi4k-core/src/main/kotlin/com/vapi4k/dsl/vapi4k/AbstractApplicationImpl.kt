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
import com.vapi4k.dsl.model.AbstractModel
import com.vapi4k.dsl.tools.ManualToolCache
import com.vapi4k.dsl.tools.ServiceCache
import com.vapi4k.dsl.tools.ToolWithServerImpl
import com.vapi4k.dsl.tools.TransferDestinationImpl
import com.vapi4k.dtos.tools.TransferMessageResponseDto
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.responses.AssistantMessageResponse
import com.vapi4k.server.RequestContext
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.MiscUtils.removeEnds
import com.vapi4k.utils.common.Utils.isNull
import com.vapi4k.utils.enums.ServerRequestType
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KFunction
import kotlin.time.Duration

abstract class AbstractApplicationImpl(
  val applicationType: ApplicationType,
) {
  internal val applicationId = getRandomSecret(15).toApplicationId()

  internal val serviceToolCache = ServiceCache { fullServerPath }
  internal val functionCache = ServiceCache { fullServerPath }
  private val manualToolCache = ManualToolCache { fullServerPath }

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

  internal abstract suspend fun getAssistantResponse(requestContext: RequestContext): AssistantMessageResponse

  internal fun serviceCacheAsJson() = serviceToolCache.cacheAsJson().toJsonElement()

  internal fun functionCacheAsJson() = functionCache.cacheAsJson().toJsonElement()

  internal fun manualCacheAsJson() = manualToolCache.cacheAsJson().toJsonElement()

  internal fun purgeServiceToolCache(maxAge: Duration) = serviceToolCache.purgeToolCache(maxAge)

  internal fun purgeFunctionCache(maxAge: Duration) = functionCache.purgeToolCache(maxAge)

  internal fun clearServiceToolCache() = serviceToolCache.clearToolCache()

  internal fun clearFunctionCache() = functionCache.clearToolCache()

  internal fun addAssistantId(assistantId: AssistantId) {
    assistantIds += assistantId
  }

  internal fun addServiceToolToCache(
    model: AbstractModel,
    obj: Any,
    function: KFunction<*>,
  ) = serviceToolCache.addToCache(model, obj, function)

  internal fun addManualToolToCache(
    funcName: FunctionName,
    manualToolImpl: ToolWithServerImpl,
  ) = manualToolCache.addToCache(funcName, manualToolImpl)

  internal fun addFunctionToCache(
    model: AbstractModel,
    obj: Any,
    function: KFunction<*>,
  ) = functionCache.addToCache(model, obj, function)

  internal fun hasServiceTools() = serviceToolCache.isNotEmpty()

  internal fun hasFunctions() = functionCache.isNotEmpty()

  internal fun hasManualTools() = manualToolCache.functions.isNotEmpty()

  internal fun containsServiceTool(
    requestContext: RequestContext,
    funcName: FunctionName,
  ): Boolean =
    serviceToolCache.containsIds(requestContext) &&
      serviceToolCache.getFromCache(requestContext).containsFunction(funcName)

  internal fun containsManualTool(funcName: FunctionName): Boolean = manualToolCache.containsTool(funcName)

  internal fun containsFunction(
    requestContext: RequestContext,
    funcName: FunctionName,
  ) = functionCache.containsIds(requestContext) &&
    functionCache.getFromCache(requestContext).containsFunction(funcName)

  internal fun getServiceTool(
    requestContext: RequestContext,
    funcName: FunctionName,
  ) = serviceToolCache.getFromCache(requestContext).getFunction(funcName)

  internal fun getManualTool(funcName: FunctionName) = manualToolCache.getTool(funcName)

  internal fun getFunction(
    requestContext: RequestContext,
    funcName: FunctionName,
  ) = functionCache.getFromCache(requestContext).getFunction(funcName)

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

  internal suspend fun getTransferDestinationResponse(requestContext: RequestContext) =
    transferDestinationRequest.let { func ->
      if (func.isNull()) {
        error("onTransferDestinationRequest{} not called")
      } else {
        val responseDto = TransferMessageResponseDto()
        val destImpl = TransferDestinationImpl("onTransferDestinationRequest", responseDto)
        func.invoke(destImpl, requestContext.request)
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

  fun processEOCRMessage(requestContext: RequestContext) {
    // Need to count the number of functions available to prevent error if no funcs exist
    if (eocrCacheRemovalEnabled) {
      val cacheKey = cacheKeyValue(requestContext)
      if (hasServiceTools()) {
        serviceToolCache.removeFromCache(requestContext) { funcInfo ->
          logger.info { "EOCR removed ${funcInfo.size} serviceTool cache items [${funcInfo.ageSecs}] " }
        } ?: logger.warn { "EOCR unable to find and remove serviceTool cache entry [$cacheKey]" }
      }

      if (hasFunctions()) {
        functionCache.removeFromCache(requestContext) { funcInfo ->
          logger.info { "EOCR removed ${funcInfo.size} function cache items [${funcInfo.ageSecs}] " }
        } ?: logger.warn { "EOCR unable to find and remove function cache entry [$cacheKey]" }
      }
    }
  }
}
