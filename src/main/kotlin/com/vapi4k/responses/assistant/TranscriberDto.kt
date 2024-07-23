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

package com.vapi4k.responses.assistant


import com.vapi4k.dsl.assistant.enums.DeepgramLanguageType
import com.vapi4k.dsl.assistant.enums.DeepgramModelType
import com.vapi4k.dsl.assistant.enums.GladiaLanguageType
import com.vapi4k.dsl.assistant.enums.GladiaModelType
import com.vapi4k.dsl.assistant.enums.TalkscriberLanguageType
import com.vapi4k.dsl.assistant.enums.TalkscriberModelType
import com.vapi4k.dsl.assistant.enums.TranscriberType
import com.vapi4k.dsl.assistant.transcriber.DeepgramTranscriberUnion
import com.vapi4k.dsl.assistant.transcriber.GladiaTranscriberUnion
import com.vapi4k.dsl.assistant.transcriber.TalkscriberTranscriberUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TranscriberSerializer::class)
interface AbstractTranscriberDto {
  val provider: TranscriberType
}

private object TranscriberSerializer : KSerializer<AbstractTranscriberDto> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AbstractTranscriberDto")

  override fun serialize(
    encoder: Encoder,
    value: AbstractTranscriberDto,
  ) {
    when (value) {
      is DeepgramTranscriberDto -> {
        value.language =
          if (value.customLanguage.isNotEmpty()) {
            if (value.transcriberLanguage.isSpecified())
              error("Cannot have both custom language and transcriber language in deepgramTranscriber{}")
            value.customLanguage
          } else {
            value.transcriberLanguage.desc
          }
        encoder.encodeSerializableValue(DeepgramTranscriberDto.serializer(), value)
      }

      is GladiaTranscriberDto -> {
        encoder.encodeSerializableValue(GladiaTranscriberDto.serializer(), value)
      }

      is TalkscriberTranscriberDto -> {
        encoder.encodeSerializableValue(TalkscriberTranscriberDto.serializer(), value)
      }
    }
  }

  override fun deserialize(decoder: Decoder): AbstractTranscriberDto {
    throw NotImplementedError("Deserialization is not supported")
  }
}

@Serializable
data class DeepgramTranscriberDto(
  @EncodeDefault
  override val provider: TranscriberType = TranscriberType.DEEPGRAM,

  @SerialName("model")
  override var transcriberModel: DeepgramModelType = DeepgramModelType.UNSPECIFIED,

  var language: String = "",
  @Transient
  override var transcriberLanguage: DeepgramLanguageType = DeepgramLanguageType.UNSPECIFIED,
  @Transient
  override var customLanguage: String = "",

  override var smartFormat: Boolean = false,
  override val keywords: MutableList<String> = mutableListOf(),
) : DeepgramTranscriberUnion, AbstractTranscriberDto


@Serializable
data class GladiaTranscriberDto(
  @EncodeDefault
  override val provider: TranscriberType = TranscriberType.GLADIA,

  @SerialName("model")
  override var transcriberModel: GladiaModelType = GladiaModelType.UNSPECIFIED,

  @SerialName("language")
  override var transcriberLanguage: GladiaLanguageType = GladiaLanguageType.UNSPECIFIED,

  override var languageBehavior: String = "",
  override var transcriptionHint: String = "",
  override var prosody: Boolean = false,
  override var audioEnhancer: Boolean = false,
) : GladiaTranscriberUnion, AbstractTranscriberDto

@Serializable
data class TalkscriberTranscriberDto(
  @EncodeDefault
  override val provider: TranscriberType = TranscriberType.TALKSCRIBER,

  @SerialName("model")
  override var transcriberModel: TalkscriberModelType = TalkscriberModelType.UNSPECIFIED,

  @SerialName("language")
  override var transcriberLanguage: TalkscriberLanguageType = TalkscriberLanguageType.UNSPECIFIED,

  ) : TalkscriberTranscriberUnion, AbstractTranscriberDto
