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

package com.vapi4k.dsl.voice

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.voice.enums.OpenAIVoiceIdType
import com.vapi4k.dsl.voice.enums.PunctuationType
import com.vapi4k.dtos.voice.OpenAIVoiceDto

interface OpenAIVoiceProperties {
  var customVoiceId: String
  var fillerInjectionEnabled: Boolean?
  var inputMinCharacters: Int
  var inputPreprocessingEnabled: Boolean?
  val inputPunctuationBoundaries: MutableSet<PunctuationType>
  var inputReformattingEnabled: Boolean?
  var speed: Double
  var voiceIdType: OpenAIVoiceIdType
}

@AssistantDslMarker
data class OpenAIVoice internal constructor(
  private val dto: OpenAIVoiceDto,
) : OpenAIVoiceProperties by dto
