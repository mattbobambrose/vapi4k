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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// Required: provider, voiceId
interface CartesiaVoiceUnion {
  var inputPreprocessingEnabled: Boolean
  var inputReformattingEnabled: Boolean
  var inputMinCharacters: Int
  var inputPunctuationBoundaries: MutableList<PunctuationType>
  var fillerInjectionEnabled: Boolean
  var provider: VoiceProviderType
  var model: CartesiaVoiceModel
  var language: CartesiaVoiceLanguage
  var voiceId: String
}

@Serializable
data class CartesiaVoiceDto(
  override var inputPreprocessingEnabled: Boolean = false,
  override var inputReformattingEnabled: Boolean = false,
  override var inputMinCharacters: Int = 0,
  override var inputPunctuationBoundaries: MutableList<PunctuationType> = mutableListOf(),
  override var fillerInjectionEnabled: Boolean = false,
  override var provider: VoiceProviderType = VoiceProviderType.CARTESIA,
  override var model: CartesiaVoiceModel = CartesiaVoiceModel.UNSPECIFIED,
  override var language: CartesiaVoiceLanguage = CartesiaVoiceLanguage.UNSPECIFIED,
  override var voiceId: String = "",
) : CartesiaVoiceUnion

@Serializable(with = CartesiaVoiceModelSerializer::class)
enum class CartesiaVoiceModel(val desc: String) {
  SONIC_ENGLISH("sonic-english"),
  SONIC_MULTILINGUAL("sonic-multilingual"),
  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object CartesiaVoiceModelSerializer : KSerializer<CartesiaVoiceModel> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: CartesiaVoiceModel,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = CartesiaVoiceModel.entries.first { it.desc == decoder.decodeString() }
}

@Serializable(with = CartesiaVoiceLanguageSerializer::class)
enum class CartesiaVoiceLanguage(val desc: String) {
  GERMAN("de"),
  ENGLISH("en"),
  SPANISH("es"),
  FRENCH("fr"),
  JAPANESE("ja"),
  PORTUGUESE("pt"),
  CHINESE("zh"),
  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object CartesiaVoiceLanguageSerializer : KSerializer<CartesiaVoiceLanguage> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: CartesiaVoiceLanguage,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = CartesiaVoiceLanguage.entries.first { it.desc == decoder.decodeString() }
}
