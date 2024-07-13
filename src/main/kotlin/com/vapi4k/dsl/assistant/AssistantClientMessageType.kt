package com.vapi4k.dsl.assistant

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AssistantClientMessageTypeSerializer::class)
enum class AssistantClientMessageType(val desc: String) {
  CONVERSATION_UPDATE("conversation-update"),
  FUNCTION_CALL("function-call"),
  FUNCTION_CALL_RESULT("function-call-result"),
  HANG("hang"),
  METADATA("metadata"),
  MODEL_OUTPUT("model-output"),
  SPEECH_UPDATE("speech-update"),
  STATUS_UPDATE("status-update"),
  TRANSCRIPT("transcript"),
  TOOL_CALLS("tool-calls"),
  TOOL_CALLS_RESULTS("tool-calls-results"),
  USER_INTERRUPTED("user-interrupted"),
  VOICE_INPUT("voice-input");
}

private object AssistantClientMessageTypeSerializer : KSerializer<AssistantClientMessageType> {
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
