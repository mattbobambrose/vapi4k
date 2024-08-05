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

package com.vapi4k.api.vapi4k

import com.vapi4k.api.assistant.AssistantResponse
import com.vapi4k.api.vapi4k.enums.ServerRequestType
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.EnvVar.Companion.defaultServerPath
import com.vapi4k.common.EnvVar.Companion.serverBaseUrl
import com.vapi4k.dsl.assistant.AssistantResponseImpl
import com.vapi4k.dsl.vapi4k.Vapi4KDslMarker
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.Utils.dropLeading
import com.vapi4k.utils.Utils.isNull
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

@Vapi4KDslMarker
class Vapi4kApplication {
  internal val applicationId = getRandomSecret(10).toApplicationId()
  internal val toolCallEndpoints = mutableListOf<Endpoint>()
  internal var assistantRequest: (suspend AssistantResponse.() -> Unit)? = null

  internal val applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()
  internal var eocrCacheRemovalEnabled = true
  internal val serverPathAsSegment get() = serverPath.dropLeading("/")

  var serverPath = defaultServerPath
  var serverSecret = ""

  fun onAssistantRequest(block: suspend AssistantResponse.() -> Unit) {
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

  internal suspend fun getAssistantResponse(request: JsonElement): AssistantRequestResponse {
    val requestContext = RequestContext(this, request)
    val assistantResponse = AssistantResponseImpl(requestContext)
    assistantRequest?.invoke(assistantResponse) ?: error("onAssistantRequest{} not called")
    return if (assistantResponse.isAssigned)
      assistantResponse.assistantRequestResponse
    else
      error("onAssistantRequest{} is missing an assistant{}, assistantId{}, squad{}, or squadId{} declaration")
  }

  private fun getEmptyEndpoint() = toolCallEndpoints.firstOrNull { endpoint -> endpoint.name.isEmpty() }

  private val defaultToolCallEndpoint
    get() = Endpoint().apply {
      this.serverUrl = "$serverBaseUrl/${this@Vapi4kApplication.serverPathAsSegment}"
      this.serverSecret = this@Vapi4kApplication.serverSecret
    }

  internal fun getEndpoint(endpointName: String) =
    if (endpointName.isEmpty())
      getEmptyEndpoint() ?: defaultToolCallEndpoint
    else
      toolCallEndpoints.firstOrNull { endpoint -> endpoint.name == endpointName }
        ?: error("Endpoint not found in the vapi4kApplication{}: $endpointName")
}
