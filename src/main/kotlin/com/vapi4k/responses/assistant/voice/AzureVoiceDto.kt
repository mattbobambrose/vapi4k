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

import com.vapi4k.common.Constants.UNSPECIFIED_DEFAULT
import com.vapi4k.dsl.assistant.enums.PunctuationType
import com.vapi4k.dsl.assistant.enums.VoiceProviderType
import com.vapi4k.dsl.assistant.voice.AzureVoiceUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class AzureVoiceDto(
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.AZURE,

  override var inputPreprocessingEnabled: Boolean? = null,
  override var inputReformattingEnabled: Boolean? = null,
  override var inputMinCharacters: Int = -1,
  override var inputPunctuationBoundaries: MutableList<PunctuationType> = mutableListOf(),
  override var fillerInjectionEnabled: Boolean? = null,
  override var voiceId: AzureVoiceId = AzureVoiceId.UNSPECIFIED,
  override var speed: Double = 0.0,
) : AzureVoiceUnion, AbstractVoiceDto()

@Serializable(with = AzureVoiceIdSerializer::class)
enum class AzureVoiceId(val desc: String) {
  ANDREW("andrew"),
  BRIAN("brian"),
  EMMA("emma"),
  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object AzureVoiceIdSerializer : KSerializer<AzureVoiceId> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: AzureVoiceId,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = AzureVoiceId.entries.first { it.desc == decoder.decodeString() }
}
