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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DestinationSerializer::class)
interface AbstractDestinationDto {
  var type: String
  var message: String
  var description: String
}

private object DestinationSerializer : KSerializer<AbstractDestinationDto> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AbstractDestinationDto")

  override fun serialize(
    encoder: Encoder,
    value: AbstractDestinationDto,
  ) {
    when (value) {
      is NumberDestinationDto -> encoder.encodeSerializableValue(NumberDestinationDto.serializer(), value)
      is SipDestinationDto -> encoder.encodeSerializableValue(SipDestinationDto.serializer(), value)
    }
  }

  override fun deserialize(decoder: Decoder): AbstractDestinationDto {
    throw NotImplementedError("Deserialization is not supported")
  }
}
