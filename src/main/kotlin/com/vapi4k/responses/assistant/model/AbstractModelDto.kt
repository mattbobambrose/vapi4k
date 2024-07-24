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

package com.vapi4k.responses.assistant.model

import com.vapi4k.dsl.assistant.enums.ModelType
import com.vapi4k.responses.assistant.ToolDto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ModelSerializer::class)
interface AbstractModelDto {
  val provider: ModelType

  //fun assignEnumOverrides()
  val tools: MutableList<ToolDto>
}

private object ModelSerializer : KSerializer<AbstractModelDto> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AbstractModelDto")

  override fun serialize(
    encoder: Encoder,
    value: AbstractModelDto,
  ) {
    when (value) {
      is AnyscaleModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(AnyscaleModelDto.serializer(), value)
      }

      is AnthropicModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(AnthropicModelDto.serializer(), value)
      }

      is CustomLLMModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(CustomLLMModelDto.serializer(), value)
      }

      is DeepInfraModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(DeepInfraModelDto.serializer(), value)
      }

      is GroqModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(GroqModelDto.serializer(), value)
      }

      is OpenAIModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(OpenAIModelDto.serializer(), value)
      }

      is OpenRouterModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(OpenRouterModelDto.serializer(), value)
      }

      is PerplexityAIModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(PerplexityAIModelDto.serializer(), value)
      }

      is TogetherAIModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(TogetherAIModelDto.serializer(), value)
      }

      is VapiModelDto -> {
        //value.assignEnumOverrides()
        encoder.encodeSerializableValue(VapiModelDto.serializer(), value)
      }
    }
  }

  override fun deserialize(decoder: Decoder): AbstractModelDto {
    throw NotImplementedError("Deserialization is not supported")
  }
}
