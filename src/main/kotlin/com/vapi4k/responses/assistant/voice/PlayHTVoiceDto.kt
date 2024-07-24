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
import com.vapi4k.dsl.assistant.voice.PlayHTVoiceUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class PlayHTVoiceDto(
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.PLAYHT,

  override var inputPreprocessingEnabled: Boolean? = null,
  override var inputReformattingEnabled: Boolean? = null,
  override var inputMinCharacters: Int = -1,
  override var inputPunctuationBoundaries: MutableList<PunctuationType> = mutableListOf(),
  override var fillerInjectionEnabled: Boolean? = null,
  override var voiceId: PlayHTVoiceId = PlayHTVoiceId.UNSPECIFIED,
  override var speed: Double = 0.0,
  override var temperature: Double = 0.0,
  override var emotion: PlayHTVoiceEmotion = PlayHTVoiceEmotion.UNSPECIFIED,
  override var voiceGuidance: Double = 0.0,
  override var styleGuidance: Double = 0.0,
  override var textGuidance: Double = 0.0,
) : PlayHTVoiceUnion, AbstractVoiceDto()

@Serializable(with = PlayHTVoiceIdSerializer::class)
enum class PlayHTVoiceId(val desc: String) {
  JENNIFER("jennifer"),
  MELISSA("melissa"),
  WILL("will"),
  CHRIS("chris"),
  MATT("matt"),
  JACK("jack"),
  RUBY("ruby"),
  DAVIS("davis"),
  DONNA("donna"),
  MICHAEL("michael"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object PlayHTVoiceIdSerializer : KSerializer<PlayHTVoiceId> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: PlayHTVoiceId,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = PlayHTVoiceId.entries.first { it.desc == decoder.decodeString() }
}

@Serializable(with = PlayHTVoiceEmotionSerializer::class)
enum class PlayHTVoiceEmotion(val desc: String) {
  FEMALE_HAPPY("female_happy"),
  FEMALE_SAD("female_sad"),
  FEMALE_ANGRY("female_angry"),
  FEMALE_FEARFUL("female_fearful"),
  FEMALE_DISGUST("female_disgust"),
  FEMALE_SURPRISED("female_surprised"),
  MALE_HAPPY("male_happy"),
  MALE_SAD("male_sad"),
  MALE_ANGRY("male_angry"),
  MALE_FEARFUL("male_fearful"),
  MALE_DISGUST("male_disgust"),
  MALE_SURPRISED("male_surprised"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object PlayHTVoiceEmotionSerializer : KSerializer<PlayHTVoiceEmotion> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: PlayHTVoiceEmotion,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = PlayHTVoiceEmotion.entries.first { it.desc == decoder.decodeString() }
}
