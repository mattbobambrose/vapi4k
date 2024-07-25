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

import com.vapi4k.dsl.assistant.assistant.AssistantImpl
import com.vapi4k.dsl.vapi4k.enums.ServerRequestType
import com.vapi4k.responses.AssistantRequestResponse
import io.ktor.server.config.ApplicationConfig
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

typealias RequestArgs = suspend (JsonElement) -> Unit
typealias ResponseArgs = suspend (requestType: ServerRequestType, JsonElement, Duration) -> Unit

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class Vapi4KDslMarker

@Vapi4KDslMarker
class Vapi4kConfig internal constructor() {

  init {
    AssistantImpl.config = this
  }

  internal lateinit var applicationConfig: ApplicationConfig

  internal var assistantRequest: (suspend (request: JsonElement) -> AssistantRequestResponse)? = null
  internal var allRequests = mutableListOf<(RequestArgs)>()
  internal val perRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val allResponses = mutableListOf<ResponseArgs>()
  internal val perResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  internal val configProperties: Vapi4kConfigProperties = Vapi4kConfigProperties()
  internal val toolCallEndpoints = mutableListOf<Endpoint>()

  internal val defaultToolCallEndpoint
    get() = Endpoint().apply {
      url = this@Vapi4kConfig.configProperties.serverUrl.ifEmpty {
        error("No default tool endpoint has been specified in the Vapi4k configuration")
      }
      secret = this@Vapi4kConfig.configProperties.serverUrlSecret
    }

  internal fun getEmptyEndpoint() = toolCallEndpoints.firstOrNull { it.name.isEmpty() }

  internal fun getEndpoint(endpointName: String) =
    (toolCallEndpoints.firstOrNull {
      it.name == endpointName
    } ?: error("Endpoint not found in vapi4k configuration: $endpointName"))

  fun configure(block: Vapi4kConfigProperties.() -> Unit) {
    configProperties.apply(block)
  }

  fun toolCallEndpoints(block: ToolCallEndpoints.() -> Unit) {
    ToolCallEndpoints().apply(block)
  }

  fun onAssistantRequest(
    block: suspend (request: JsonElement) -> AssistantRequestResponse,
  ) {
    if (assistantRequest == null)
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once")
  }

  fun onAllRequests(block: suspend (request: JsonElement) -> Unit) {
    allRequests += block
  }

  fun onRequest(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement) -> Unit,
  ) {
    perRequests += requestType to block
    requestTypes.forEach { perRequests += it to block }
  }

  fun onAllResponses(
    block: suspend (requestType: ServerRequestType, response: JsonElement, elapsed: Duration) -> Unit,
  ) {
    allResponses += block
  }

  fun onResponse(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (requestType: ServerRequestType, request: JsonElement, elapsed: Duration) -> Unit,
  ) {
    perResponses += requestType to block
    requestTypes.forEach { perResponses += it to block }
  }
}
