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

import com.vapi4k.responses.assistant.NumberDestinationDto
import com.vapi4k.responses.assistant.SipDestinationDto
import kotlinx.serialization.json.JsonElement

internal interface NumberDestinationUnion {
  var number: String
  var message: String
  var description: String
}

internal interface SipDestinationUnion {
  var sipUri: String
  var message: String
  var description: String
}

@AssistantDslMarker
class NumberDestination internal constructor(
  val request: JsonElement,
  private val dto: NumberDestinationDto,
) : NumberDestinationUnion by dto

@AssistantDslMarker
class SipDestination internal constructor(
  val request: JsonElement,
  private val dto: SipDestinationDto,
) : SipDestinationUnion by dto
