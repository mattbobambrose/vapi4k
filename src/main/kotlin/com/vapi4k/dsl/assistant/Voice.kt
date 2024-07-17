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

package com.vapi4k.dsl.assistant

import com.vapi4k.responses.assistant.PunctuationType
import com.vapi4k.responses.assistant.VoiceDto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface VoiceUnion {
  var inputPreprocessingEnabled: Boolean
  var inputReformattingEnabled: Boolean
  var inputMinCharacters: Int
  var fillerInjectionEnabled: Boolean
  var provider: ProviderType
  var voiceId: VoiceType
  var speed: Double
  var inputPunctuationBoundaries: List<PunctuationType>
}

data class Voice internal constructor(val voiceDto: VoiceDto) : VoiceUnion by voiceDto

@Serializable(with = VoiceTypeSerializer::class)
enum class VoiceType(val desc: String) {
  ANDREW("Andrew"),
  BRIAN("Brian"),
  EMMA("Emma"),
  UNKNOWN("Unknown")
}

private object VoiceTypeSerializer : KSerializer<VoiceType> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VoiceType", PrimitiveKind.STRING)

  override fun serialize(
    encoder: Encoder,
    value: VoiceType,
  ) {
    encoder.encodeString(value.desc)
  }

  override fun deserialize(decoder: Decoder) =
    VoiceType.entries.first { it.desc == decoder.decodeString() }
}
