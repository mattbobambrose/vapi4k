package com.vapi4k.dsl.assistant

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FirstMessageModeTypeSerializer::class)
enum class FirstMessageModeType(val desc: String) {
  ASSISTANT_SPEAKS_FIRST("assistant-speaks-first"),
  ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL("assistant-speaks-first-with-model-generated-message"),
  ASSISTANT_WAITS_FOR_USE("assistant-waits-for-user");
}

private object FirstMessageModeTypeSerializer : KSerializer<FirstMessageModeType> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ToolCallMessageType", PrimitiveKind.STRING)

  override fun serialize(
    encoder: Encoder,
    value: FirstMessageModeType,
  ) {
    encoder.encodeString(value.desc)
  }

  override fun deserialize(decoder: Decoder) =
    FirstMessageModeType.entries.first { it.desc == decoder.decodeString() }
}
