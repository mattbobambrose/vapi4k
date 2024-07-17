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

package com.vapi4k.common

import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import com.vapi4k.dsl.assistant.enums.ProviderType
import com.vapi4k.dsl.assistant.enums.VoiceType
import com.vapi4k.dsl.vapi4k.ToolCallMessageType
import com.vapi4k.dsl.vapi4k.ToolCallRoleType
import com.vapi4k.responses.assistant.PunctuationType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object Serializers {

  internal object AssistantClientMessageTypeSerializer : KSerializer<AssistantClientMessageType> {
    override val descriptor: SerialDescriptor =
      PrimitiveSerialDescriptor("AssistantClientMessageType", PrimitiveKind.STRING)

    override fun serialize(
      encoder: Encoder,
      value: AssistantClientMessageType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      AssistantClientMessageType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object AssistantServerMessageTypeSerializer : KSerializer<AssistantServerMessageType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AssistantServerMessageType", STRING)

    override fun serialize(
      encoder: Encoder,
      value: AssistantServerMessageType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      AssistantServerMessageType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object FirstMessageModeTypeSerializer : KSerializer<FirstMessageModeType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ToolCallMessageType", STRING)

    override fun serialize(
      encoder: Encoder,
      value: FirstMessageModeType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      FirstMessageModeType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object ProviderTypeSerializer : KSerializer<ProviderType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

    override fun serialize(
      encoder: Encoder,
      value: ProviderType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      ProviderType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object VoiceTypeSerializer : KSerializer<VoiceType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VoiceType", STRING)

    override fun serialize(
      encoder: Encoder,
      value: VoiceType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      VoiceType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object ToolCallMessageTypeSerializer : KSerializer<ToolCallMessageType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ToolCallMessageType", STRING)

    override fun serialize(
      encoder: Encoder,
      value: ToolCallMessageType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      ToolCallMessageType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object ToolCallRoleSerializer : KSerializer<ToolCallRoleType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ToolCallRole", STRING)

    override fun serialize(
      encoder: Encoder,
      value: ToolCallRoleType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      ToolCallRoleType.entries.first { it.desc == decoder.decodeString() }
  }

  internal object PunctuationTypeSerializer : KSerializer<PunctuationType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PunctuationType", STRING)

    override fun serialize(
      encoder: Encoder,
      value: PunctuationType,
    ) {
      encoder.encodeString(value.desc)
    }

    override fun deserialize(decoder: Decoder) =
      PunctuationType.entries.first { it.desc == decoder.decodeString() }
  }


}
