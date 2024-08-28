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
import com.vapi4k.common.CoreEnvVars.defaultServerPath
import com.vapi4k.common.CoreEnvVars.vapi4kBaseUrl
import com.vapi4k.common.Headers.VALIDATE_HEADER
import com.vapi4k.common.Headers.VALIDATE_VALUE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.common.QueryParams.SECRET_QUERY_PARAM
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.tools.ManualToolCache
import com.vapi4k.dsl.tools.ServiceCache
import com.vapi4k.dsl.tools.TransferDestinationImpl
import com.vapi4k.dtos.tools.TransferMessageResponseDto
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonElementUtils.EMPTY_JSON_ELEMENT
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.MiscUtils.removeEnds
import com.vapi4k.utils.common.Utils.isNull
import com.vapi4k.utils.enums.ServerRequestType
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

enum class ApplicationType(
  val desc: String,
) {
  INBOUND_CALL("inboundCallApplication{}"),
  OUTBOUND_CALL("outboundCallApplication{}"),
  WEB("webApplication{}"),
}

abstract class AbstractApplicationImpl(
  val applicationType: ApplicationType,
) {
  internal val applicationId = getRandomSecret(15).toApplicationId()
  internal val serviceToolCache = ServiceCache { serverPath }
  internal val functionCache = ServiceCache { serverPath }
  internal val manualToolCache = ManualToolCache { serverPath }

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  internal var eocrCacheRemovalEnabled = true

  private var transferDestinationRequest: (suspend TransferDestinationResponse.(JsonElement) -> Unit)? = null

  var serverPath = defaultServerPath
  var serverSecret = ""

  internal val serverUrl get() = "$vapi4kBaseUrl/$serverPathAsSegment"
  internal val serverPathAsSegment get() = serverPath.removeEnds("/")
  internal val serverPathWithSecretAsQueryParam: String
    get() = "$serverPathAsSegment${serverSecret.let { if (it.isBlank()) "" else "?$SECRET_QUERY_PARAM=$it" }}"

  fun fetchContent(
    request: JsonElement,
    appName: String,
    secret: String,
  ): Pair<HttpStatusCode, String> =
    runBlocking {
      val url = "$vapi4kBaseUrl/$appName"
      val response = httpClient.post(url) {
        contentType(Application.Json)
        headers.append(VALIDATE_HEADER, VALIDATE_VALUE)
        // logger.info { "Assigning secret from QP: $secret" }
        if (secret.isNotEmpty())
          headers.append(VAPI_SECRET_HEADER, secret)
        val jsonBody =
          if (applicationType == ApplicationType.INBOUND_CALL)
            request
          else
            EMPTY_JSON_ELEMENT
        setBody(jsonBody)
      }
      response.status to response.bodyAsText()
    }

  internal fun containsServiceToolInCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) = serviceToolCache.containsSessionCacheId(sessionCacheId) &&
    serviceToolCache.getFromCache(sessionCacheId).containsFunction(funcName)

  internal fun containsManualToolInCache(funcName: String): Boolean = manualToolCache.containsTool(funcName)

  internal fun containsFunctionInCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) = functionCache.containsSessionCacheId(sessionCacheId) &&
    functionCache.getFromCache(sessionCacheId).containsFunction(funcName)

  internal fun getServiceToolFromCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) = serviceToolCache.getFromCache(sessionCacheId).getFunction(funcName)

  internal fun getFunctionFromCache(
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) = functionCache.getFromCache(sessionCacheId).getFunction(funcName)

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

  internal suspend fun getTransferDestinationResponse(request: JsonElement) =
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
    request: JsonElement,
    logger: KLogger,
    logger0: KLogger,
    logger1: KLogger,
    logger2: KLogger,
  ) {
    if (eocrCacheRemovalEnabled) {
      val sessionCacheId = request.sessionCacheId
      with(this) {
        serviceToolCache.removeFromCache(sessionCacheId) { funcInfo ->
          logger.info { "EOCR removed ${funcInfo.functions.size} serviceTool cache items [${funcInfo.ageSecs}] " }
        } ?: logger0.warn { "EOCR unable to find and remove serviceTool cache entry [$sessionCacheId]" }
        functionCache.removeFromCache(sessionCacheId) { funcInfo ->
          logger1.info { "EOCR removed ${funcInfo.functions.size} function cache items [${funcInfo.ageSecs}] " }
        } ?: logger2.warn { "EOCR unable to find and remove function cache entry [$sessionCacheId]" }
      }
    }
  }
}
