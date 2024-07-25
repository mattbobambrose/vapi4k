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

package com.vapi4k.dtos.assistant.voice

import com.vapi4k.dsl.voice.enums.OpenAIVoiceIdType
import com.vapi4k.dsl.voice.enums.VoiceProviderType
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class OpenAIVoiceDto(
  var voiceId: String = "",
  @Transient
  override var voiceIdType: OpenAIVoiceIdType = OpenAIVoiceIdType.UNSPECIFIED,
  @Transient
  override var customVoiceId: String = "",

  override var speed: Double = -1.0,
) : com.vapi4k.dsl.voice.OpenAIVoiceProperties, AbstractVoiceDto(), CommonVoiceDto {
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.OPENAI

  fun assignEnumOverrides() {
    voiceId = customVoiceId.ifEmpty { voiceIdType.desc }
  }

  override fun verifyValues() {
    if (voiceIdType.isNotSpecified() && customVoiceId.isEmpty())
      error("openAIVoice{} requires a voiceIdType or customVoiceId value")
    if (voiceIdType.isSpecified() && customVoiceId.isNotEmpty())
      error("openAIVoice{} cannot have both voiceIdType and customVoiceId values")
  }
}
