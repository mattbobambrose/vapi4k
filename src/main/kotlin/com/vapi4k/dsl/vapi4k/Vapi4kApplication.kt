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

import com.vapi4k.common.EnvVar.Companion.defaultServerPath
import com.vapi4k.common.EnvVar.Companion.serverBaseUrl
import com.vapi4k.dsl.vapi4k.enums.ServerRequestType
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.utils.Utils.dropLeading
import com.vapi4k.utils.Utils.isNull
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

class Vapi4kApplication {
  internal val toolCallEndpoints = mutableListOf<Endpoint>()
  internal var assistantRequest: (suspend (requestContext: RequestContext) -> AssistantRequestResponse)? = null

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  var serverPath = defaultServerPath
  var serverSecret = ""
  var eocrCacheRemovalEnabled = true

  fun onAssistantRequest(block: suspend (requestContext: RequestContext) -> AssistantRequestResponse) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per vapi4kApplication{}")
  }

  fun toolCallEndpoints(block: ToolCallEndpoints.() -> Unit) {
    ToolCallEndpoints(this).apply(block)
  }

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

  internal suspend fun getAssistantResponse(request: JsonElement) =
    assistantRequest?.invoke(RequestContext(this, request)) ?: error("onAssistantRequest{} not called")

  private fun getEmptyEndpoint() = toolCallEndpoints.firstOrNull { it.name.isEmpty() }

  internal val defaultToolCallEndpoint
    get() = Endpoint().apply {
      this.serverUrl = "$serverBaseUrl/${serverPath.dropLeading("/")}"
      this.serverSecret = this@Vapi4kApplication.serverSecret
    }

  internal fun getEndpoint(endpointName: String) =
    if (endpointName.isEmpty())
      getEmptyEndpoint() ?: defaultToolCallEndpoint
    else
      toolCallEndpoints.firstOrNull {
        it.name == endpointName
      } ?: error("Endpoint not found in the vapi4kApplication{}: $endpointName")
}
