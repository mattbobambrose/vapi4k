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

import com.vapi4k.AssistantDslMarker
import com.vapi4k.dsl.assistant.enums.ProviderType
import com.vapi4k.dsl.assistant.enums.PunctuationType
import com.vapi4k.dsl.assistant.enums.VoiceType
import com.vapi4k.responses.assistant.VoiceDto

interface VoiceUnion {
  var inputPreprocessingEnabled: Boolean
  var inputReformattingEnabled: Boolean
  var inputMinCharacters: Int
  var fillerInjectionEnabled: Boolean
  var provider: ProviderType
  var voiceId: VoiceType
  var speed: Double
  val inputPunctuationBoundaries: MutableList<PunctuationType>
}

@AssistantDslMarker
data class Voice internal constructor(val voiceDto: VoiceDto) : VoiceUnion by voiceDto

@AssistantDslMarker
data class VoiceOverrides internal constructor(val voiceDto: VoiceDto) : VoiceUnion by voiceDto
