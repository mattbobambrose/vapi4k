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

import com.vapi4k.dsl.assistant.enums.RimeAIVoiceIdType
import com.vapi4k.dsl.assistant.enums.RimeAIVoiceModelType
import com.vapi4k.dsl.assistant.enums.VoiceProviderType
import com.vapi4k.dsl.assistant.voice.RimeAIVoiceProperties
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class RimeAIVoiceDto(
  override var voiceId: RimeAIVoiceIdType = RimeAIVoiceIdType.UNSPECIFIED,
  override var model: RimeAIVoiceModelType = RimeAIVoiceModelType.UNSPECIFIED,
  override var speed: Double = -1.0,
) : RimeAIVoiceProperties, AbstractVoiceDto(), CommonVoiceDto {
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.ELEVENLABS

  override fun verifyValues() {
    if (voiceId.isNotSpecified())
      error("rimeAIVoice{} requires a voiceId value")
  }
}
