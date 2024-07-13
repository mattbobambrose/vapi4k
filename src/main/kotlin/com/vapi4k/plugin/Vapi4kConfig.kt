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

package com.vapi4k.plugin

import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.dsl.vapi4k.Endpoint
import com.vapi4k.dsl.vapi4k.ServerRequestType
import com.vapi4k.dsl.vapi4k.Vapi4KDslMarker
import com.vapi4k.dsl.vapi4k.Vapi4kConfigProperties
import com.vapi4k.responses.AssistantRequestResponse
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

@Vapi4KDslMarker
class Vapi4kConfig {

  init {
    Assistant.config = this
  }

  internal var assistantRequest: (suspend (config: Vapi4kConfig, request: JsonElement) -> AssistantRequestResponse)? =
    null
  internal var allRequests = mutableListOf<(suspend (requestType: ServerRequestType, request: JsonElement) -> Unit)>()
  internal val perRequests = mutableListOf<Pair<ServerRequestType, suspend (JsonElement) -> Unit>>()
  internal val allResponses =
    mutableListOf<(suspend (requestType: ServerRequestType, response: JsonElement, elapsed: Duration) -> Unit)>()
  internal val perResponses = mutableListOf<Pair<ServerRequestType, suspend (JsonElement, Duration) -> Unit>>()

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
}
