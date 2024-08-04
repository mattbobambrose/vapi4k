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

import com.vapi4k.common.Endpoints.DEFAULT_SERVER_PATH
import com.vapi4k.dsl.vapi4k.enums.ServerRequestType
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.utils.Utils.ensureStartsWith
import com.vapi4k.utils.Utils.isNull
import kotlinx.serialization.json.JsonElement
import java.net.URI
import kotlin.time.Duration

class Vapi4kApplication {
  internal val toolCallEndpoints = mutableListOf<Endpoint>()
  internal val serverUrlPath
    get() = (if (serverUrl.isEmpty()) DEFAULT_SERVER_PATH else URI(serverUrl).toURL().path).ensureStartsWith("/")

  internal val serverUrlPathSegments
    get() = serverUrlPath.split("/").filter { it.isNotEmpty() }

  internal var assistantRequest: (suspend (request: JsonElement) -> AssistantRequestResponse)? = null

  internal var applicationAllRequests = mutableListOf<(RequestArgs)>()
  internal val applicationPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val applicationAllResponses = mutableListOf<ResponseArgs>()
  internal val applicationPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  var serverUrl = ""
  var serverUrlSecret = ""
  var eocrCacheRemovalEnabled = true

  fun onAssistantRequest(block: suspend (request: JsonElement) -> AssistantRequestResponse) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once")
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

  fun onAllResponses(block: suspend (requestType: ServerRequestType, response: JsonElement, elapsed: Duration) -> Unit) {
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

  private fun getEmptyEndpoint() = toolCallEndpoints.firstOrNull { it.name.isEmpty() }

  internal val defaultToolCallEndpoint
    get() = Endpoint().apply {
      this.serverUrl = this@Vapi4kApplication.serverUrl.ifEmpty {
        error("No default tool endpoint has been specified in the Vapi4k configuration")
      }
      this.serverUrlSecret = this@Vapi4kApplication.serverUrlSecret
    }

  internal fun getEndpoint(endpointName: String) =
    if (endpointName.isEmpty())
      getEmptyEndpoint() ?: defaultToolCallEndpoint
    else
      toolCallEndpoints.firstOrNull {
        it.name == endpointName
      } ?: error("Endpoint not found in vapi4k configuration: $endpointName")

}
