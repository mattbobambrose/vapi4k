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

package com.vapi4k.responses.assistant.voice

import com.vapi4k.dsl.assistant.enums.OpenAIVoiceIdType
import com.vapi4k.dsl.assistant.enums.PunctuationType
import com.vapi4k.dsl.assistant.enums.VoiceProviderType
import com.vapi4k.dsl.assistant.voice.OpenAIVoiceUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIVoiceDto(
  override var inputPreprocessingEnabled: Boolean? = null,
  override var inputReformattingEnabled: Boolean? = null,
  override var inputMinCharacters: Int = -1,
  override var inputPunctuationBoundaries: MutableSet<PunctuationType> = mutableSetOf(),
  override var fillerInjectionEnabled: Boolean? = null,
  override var voiceId: OpenAIVoiceIdType = OpenAIVoiceIdType.UNSPECIFIED,
  override var speed: Double = -1.0,
) : OpenAIVoiceUnion, AbstractVoiceDto {
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.OPENAI

  override fun verifyValues() {
    if (voiceId.isNotSpecified())
      error("openAIVoice{} requires a voiceId value")
  }
}
