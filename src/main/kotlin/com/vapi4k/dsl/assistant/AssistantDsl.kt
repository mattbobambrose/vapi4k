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

package com.vapi4k.dsl.assistant

import com.vapi4k.responses.assistant.AssistantRequestMessageResponse
import kotlinx.serialization.json.JsonElement
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

object AssistantDsl {
  fun assistant(
    request: JsonElement,
    block: Assistant.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      Assistant(request, this).apply(block)
    }.messageResponse

  fun assistantId(request: JsonElement, block: AssistantId.() -> Unit) =
    AssistantRequestMessageResponse().apply {
      AssistantId(request, this).apply(block)
    }.messageResponse

  fun squad(
    request: JsonElement,
    block: Squad.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      Squad(request, this).apply(block)
    }.messageResponse

  fun squadId(id: String) =
    AssistantRequestMessageResponse().apply {
      messageResponse.squadId = id
    }.messageResponse

  data class AssistantId internal constructor(
    val request: JsonElement,
    val requestMessageResponse: AssistantRequestMessageResponse
  ) {
    private val messageResponse get() = requestMessageResponse.messageResponse
    var id
      get() = messageResponse.assistantId
      set(value) {
        messageResponse.assistantId = value
      }

    fun overrides(block: AssistantOverrides.() -> Unit) {
      AssistantOverrides(request, messageResponse.assistantOverridesDto).apply(block)
    }
  }
}

@Retention(RUNTIME)
@Target(AnnotationTarget.FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class ToolCall(
  val description: String = "",
  val name: String = "",
)

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
annotation class Param(val description: String)
