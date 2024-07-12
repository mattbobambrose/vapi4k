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

package com.vapi4k.responses


import com.vapi4k.plugin.Vapi4kConfig
import com.vapi4k.responses.assistant.AssistantDto
import com.vapi4k.responses.assistant.AssistantOverrides
import com.vapi4k.responses.assistant.Destination
import com.vapi4k.responses.assistant.Squad
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AssistantRequestResponse(
  var destination: Destination = Destination(),
  var assistantId: String = "",
  var assistant: AssistantDto = AssistantDto(),
  var assistantOverrides: AssistantOverrides? = null,
  var squadId: String = "",
  var squad: Squad? = null,
  var error: String = "",
) {
  companion object {
    suspend fun getAssistantResponse(config: Vapi4kConfig, request: JsonElement) =
      config.assistantRequest?.invoke(config, request) ?: error("onAssistantRequest{} not called")
  }
}
