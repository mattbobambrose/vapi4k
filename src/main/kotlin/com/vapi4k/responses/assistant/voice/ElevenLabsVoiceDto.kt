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
import com.vapi4k.dsl.assistant.voice.ElevenLabsVoiceUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ElevenLabsVoiceDto(
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.ELEVENLABS,

  override var inputPreprocessingEnabled: Boolean? = null,
  override var inputReformattingEnabled: Boolean? = null,
  override var inputMinCharacters: Int = -1,
  override var inputPunctuationBoundaries: MutableList<PunctuationType> = mutableListOf(),
  override var fillerInjectionEnabled: Boolean? = null,
  override var voiceId: ElevenLabsVoiceId = ElevenLabsVoiceId.UNSPECIFIED,
  override var stability: Double = 0.0,
  override var similarityBoost: Double = 0.0,
  override var style: Double = 0.0,
  override var useSpeakerBoost: Boolean? = null,
  override var optimizeStreaming: Double = 0.0,
  override var enableSsmlParsing: Boolean? = null,
  override var model: ElevenLabsVoiceModel = ElevenLabsVoiceModel.UNSPECIFIED,
) : ElevenLabsVoiceUnion, AbstractVoiceDto()

@Serializable(with = ElevenLabsVoiceIdSerializer::class)
enum class ElevenLabsVoiceId(val desc: String) {
  BURT("burt"),
  MARISSA("marissa"),
  ANDREA("andrea"),
  SARAH("sarah"),
  PHILLIP("phillip"),
  STEVE("steve"),
  JOSEPH("joseph"),
  MYRA("myra"),
  PAULA("paula"),
  RYAN("ryan"),
  DREW("drew"),
  PAUL("paul"),
  MRB("mrb"),
  MATILDA("matilda"),
  MARK("mark"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object ElevenLabsVoiceIdSerializer : KSerializer<ElevenLabsVoiceId> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: ElevenLabsVoiceId,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = ElevenLabsVoiceId.entries.first { it.desc == decoder.decodeString() }
}

@Serializable(with = ElevenLabsVoiceModelSerializer::class)
enum class ElevenLabsVoiceModel(val desc: String) {
  ELEVEN_MULTILINGUAL_V2("eleven-multilingual_v2"),
  ELEVEN_TURBO_V2("eleven_turbo_v2"),
  ELEVEN_TURBO_V2_5("eleven_turbo_v2_5"),
  ELEVEN_MONOLINGUAL_V1("eleven_monolingual_v1"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object ElevenLabsVoiceModelSerializer : KSerializer<ElevenLabsVoiceModel> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: ElevenLabsVoiceModel,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = ElevenLabsVoiceModel.entries.first { it.desc == decoder.decodeString() }
}
