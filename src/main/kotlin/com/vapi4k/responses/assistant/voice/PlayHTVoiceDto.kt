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

import com.vapi4k.dsl.assistant.voice.PlayHTVoiceProperties
import com.vapi4k.dsl.assistant.voice.enums.PlayHTVoiceEmotionType
import com.vapi4k.dsl.assistant.voice.enums.PlayHTVoiceIdType
import com.vapi4k.dsl.assistant.voice.enums.VoiceProviderType
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PlayHTVoiceDto(
  var voiceId: String = "",
  @Transient
  override var voiceIdType: PlayHTVoiceIdType = PlayHTVoiceIdType.UNSPECIFIED,
  @Transient
  override var customVoiceId: String = "",

  override var speed: Double = -1.0,
  override var temperature: Double = -1.0,
  override var emotion: PlayHTVoiceEmotionType = PlayHTVoiceEmotionType.UNSPECIFIED,
  override var voiceGuidance: Double = -1.0,
  override var styleGuidance: Double = -1.0,
  override var textGuidance: Double = -1.0,
) : PlayHTVoiceProperties, AbstractVoiceDto(), CommonVoiceDto {
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.PLAYHT

  fun assignEnumOverrides() {
    voiceId = customVoiceId.ifEmpty { voiceIdType.desc }
  }

  override fun verifyValues() {
    if (voiceIdType.isNotSpecified() && customVoiceId.isEmpty())
      error("playHTVoice{} requires a voiceIdType or customVoiceId value")
    if (voiceIdType.isSpecified() && customVoiceId.isNotEmpty())
      error("playHTVoice{} cannot have both voiceIdType and customVoiceId values")
  }
}
