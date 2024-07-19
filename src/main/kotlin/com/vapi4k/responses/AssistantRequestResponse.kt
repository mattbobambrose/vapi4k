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


import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.responses.assistant.AbstractDestinationDto
import com.vapi4k.responses.assistant.AssistantDto
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.NumberDestinationDto
import com.vapi4k.responses.assistant.SquadDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AssistantRequestResponse(
  var destination: AbstractDestinationDto = NumberDestinationDto(),

  var assistantId: String = "",
  @SerialName("assistant")
  val assistantDto: AssistantDto = AssistantDto(),
  @SerialName("assistantOverrides")
  val assistantOverridesDto: AssistantOverridesDto = AssistantOverridesDto(),

  var squadId: String = "",
  // Do not initialize this to a SquadDto
  @SerialName("squad")
  val squadDto: SquadDto = SquadDto(),

  var error: String = "",
) {
  companion object {
    internal suspend fun getAssistantResponse(
      request: JsonElement,
    ) =
      Assistant.config.assistantRequest?.invoke(request) ?: error("onAssistantRequest{} not called")
  }
}
