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

import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.CoreEnvVars.defaultServerPath
import com.vapi4k.common.CoreEnvVars.serverBaseUrl
import com.vapi4k.common.Headers.SECRET_HEADER
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.tools.ManualToolCache
import com.vapi4k.dsl.tools.ServiceCache
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.enums.ServerRequestType
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

enum class ApplicationType {
  INBOUND_CALL,
  OUTBOUND_CALL,
  WEB,
}

abstract class AbstractApplicationImpl(
  val applicationType: ApplicationType,
) {
  internal val applicationId = getRandomSecret(10).toApplicationId()
  internal val serviceToolCache = ServiceCache { serverPath }
  internal val functionCache = ServiceCache { serverPath }
  internal val manualToolCache = ManualToolCache { serverPath }

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  internal val serverUrl get() = "$serverBaseUrl/$serverPathAsSegment"

  var serverPath = defaultServerPath
  var serverSecret = ""

  internal val fqServerPath get() = "$serverBaseUrl/$serverPathAsSegment"
  internal val serverPathAsSegment get() = serverPath.removePrefix("/").removeSuffix("/")
  internal val serverPathWithSecret: String
    get() = "$serverPathAsSegment${serverSecret.let { if (it.isBlank()) "" else "?$SECRET_HEADER=$it" }}"

  abstract fun fetchContent(
    request: JsonElement,
    appName: String,
    secret: String,
  ): Pair<HttpStatusCode, String>

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
}
