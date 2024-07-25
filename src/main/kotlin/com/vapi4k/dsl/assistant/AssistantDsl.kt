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

import com.vapi4k.common.SessionId.Companion.toSessionId
import com.vapi4k.dsl.destination.NumberDestination
import com.vapi4k.dsl.destination.NumberDestinationImpl
import com.vapi4k.dsl.destination.SipDestination
import com.vapi4k.dsl.destination.SipDestinationImpl
import com.vapi4k.dsl.squad.Squad
import com.vapi4k.dsl.squad.SquadId
import com.vapi4k.dsl.squad.SquadIdImpl
import com.vapi4k.dsl.squad.SquadImpl
import com.vapi4k.dtos.api.destination.NumberDestinationDto
import com.vapi4k.dtos.api.destination.SipDestinationDto
import com.vapi4k.responses.AssistantRequestMessageResponse
import com.vapi4k.utils.JsonElementUtils.messageCallId
import kotlinx.serialization.json.JsonElement
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class AssistantDslMarker

object AssistantDsl {
  fun assistant(
    request: JsonElement,
    block: Assistant.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      with(messageResponse) {
        AssistantImpl(request, request.messageCallId.toSessionId(), assistantDto, assistantOverridesDto)
          .apply(block)
          .apply {
            assistantDto.updated = true
            assistantDto.verifyValues()
          }
      }
    }.messageResponse

  fun assistantId(
    request: JsonElement,
    block: AssistantId.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      AssistantIdImpl(request, request.messageCallId.toSessionId(), messageResponse).apply(block)
    }.messageResponse

  fun squad(
    request: JsonElement,
    block: Squad.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      SquadImpl(request, request.messageCallId.toSessionId(), messageResponse.squadDto).apply(block)
    }.messageResponse

  fun squadId(
    request: JsonElement,
    block: SquadId.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      SquadIdImpl(request, messageResponse).apply(block)
    }.messageResponse

  fun numberDestination(
    request: JsonElement,
    block: NumberDestination.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      val numDto = NumberDestinationDto()
      messageResponse.destination = numDto
      NumberDestinationImpl(request, numDto).apply(block)
    }.messageResponse

  fun sipDestination(
    request: JsonElement,
    block: SipDestination.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      val sipDto = SipDestinationDto()
      messageResponse.destination = sipDto
      SipDestinationImpl(request, sipDto).apply(block)
    }.messageResponse
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
