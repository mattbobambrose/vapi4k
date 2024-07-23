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

package com.vapi4k.dsl.assistant.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PunctuationTypeSerializer::class)
enum class PunctuationType(internal val desc: String) {
  FULL_STOP('\u3002'.toString()),
  FULL_WIDTH_COMMA('\uff0c'.toString()),
  PERIOD("."),
  EXCLAMATION("!"),
  QUESTION("?"),
  SEMICOLON(";"),
  RIGHT_PAREN(")"),
  ARABIC_COMMA('\u060C'.toString()),
  ARABIC_FULL_STOP('\u06D4'.toString()),
  DEVANAGARI_DANDA('\u0964'.toString()),
  DEVANAGARI_DOUBLE_DANDA('\u0965'.toString()),
  VERTICAL_BAR("|"),
  DOUBLE_VERTICAL_BAR("||"),
  COMMA(","),
  COLON(":"),
}

private object PunctuationTypeSerializer : KSerializer<PunctuationType> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PunctuationType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: PunctuationType,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = PunctuationType.entries.first { it.desc == decoder.decodeString() }
}
