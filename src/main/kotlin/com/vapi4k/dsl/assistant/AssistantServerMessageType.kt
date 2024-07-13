package com.vapi4k.dsl.assistant

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AssistantServerMessageTypeSerializer::class)
enum class AssistantServerMessageType(val desc: String) {
  CONVERSATION_UPDATE("conversation-update"),
  END_OF_CALL_REPORT("end-of-call-report"),
  FUNCTION_CALL("function-call"),
  HANG("hang"),
  MODEL_OUTPUT("model-output"),
  PHONE_CALL_CONTROL("phone-call-control"),
  SPEECH_UPDATE("speech-update"),
  STATUS_UPDATE("status-update"),
  TRANSCRIPT("transcript"),
  TOOL_CALLS("tool-calls"),
  TRANSFER_DESTINATION_REQUEST("transfer-destination-request"),
  USER_INTERRUPTED("user-interrupted"),
  VOICE_INPUT("voice-input");
}

private object AssistantServerMessageTypeSerializer : KSerializer<AssistantServerMessageType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("AssistantServerMessageType", PrimitiveKind.STRING)

  override fun serialize(
    encoder: Encoder,
    value: AssistantServerMessageType,
  ) {
    encoder.encodeString(value.desc)
  }

  override fun deserialize(decoder: Decoder) =
    AssistantServerMessageType.entries.first { it.desc == decoder.decodeString() }
}
