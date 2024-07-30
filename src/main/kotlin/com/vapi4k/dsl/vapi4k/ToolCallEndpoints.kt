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

import com.vapi4k.dsl.assistant.AssistantImpl

@Vapi4KDslMarker
class ToolCallEndpoints internal constructor() {
  private fun hasName(endpoint: Endpoint) = AssistantImpl.config.toolCallEndpoints.any { it.name == endpoint.name }

  private fun hasUrl(endpoint: Endpoint) =
    AssistantImpl.config.toolCallEndpoints.any { it.serverUrl == endpoint.serverUrl }

  fun endpoint(block: Endpoint.() -> Unit) {
    AssistantImpl.config.toolCallEndpoints += Endpoint().apply(block).also { endpoint ->
      when {
        hasName(endpoint) && endpoint.name.isEmpty() -> error("Duplicate blank endpoint names")
        hasName(endpoint) -> error("Duplicate endpoint name: ${endpoint.name}")
        hasUrl(endpoint) -> error("Duplicate endpoint url: ${endpoint.serverUrl}")
      }
    }
  }
}
