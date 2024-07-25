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

import com.vapi4k.dsl.assistant.enums.PlayHTVoiceEmotionType
import com.vapi4k.dsl.assistant.enums.PlayHTVoiceIdType
import com.vapi4k.dsl.assistant.enums.VoiceProviderType
import com.vapi4k.dsl.assistant.voice.PlayHTVoiceProperties
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class PlayHTVoiceDto(
  override var voiceId: PlayHTVoiceIdType = PlayHTVoiceIdType.UNSPECIFIED,
  override var speed: Double = -1.0,
  override var temperature: Double = -1.0,
  override var emotion: PlayHTVoiceEmotionType = PlayHTVoiceEmotionType.UNSPECIFIED,
  override var voiceGuidance: Double = -1.0,
  override var styleGuidance: Double = -1.0,
  override var textGuidance: Double = -1.0,
) : PlayHTVoiceProperties, AbstractVoiceDto(), CommonVoiceDto {
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.PLAYHT

  override fun verifyValues() {
    if (voiceId.isNotSpecified())
      error("playHTVoice{} requires a voiceId value")
  }
}
