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
import com.vapi4k.dsl.vapi4k.Vapi4KDslMarker
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

@Vapi4KDslMarker
interface Vapi4kApplication {
  var serverPath: String
  var serverSecret: String

  fun onAssistantRequest(block: suspend AssistantResponse.() -> Unit)

  fun toolServers(block: ToolServers.() -> Unit)

  fun onAllRequests(block: suspend (request: JsonElement) -> Unit)

  fun onRequest(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement) -> Unit,
  )

  fun onAllResponses(
    block: suspend (requestType: ServerRequestType, response: JsonElement, elapsed: Duration) -> Unit,
  )

  fun onResponse(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (requestType: ServerRequestType, request: JsonElement, elapsed: Duration) -> Unit,
  )
}
