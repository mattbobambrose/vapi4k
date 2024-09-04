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

package com.vapi4k.utils.enums

import com.vapi4k.utils.json.JsonElementUtils.stringValue
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement

enum class ServerRequestType(
  val desc: String,
) {
  ASSISTANT_REQUEST("assistant-request"),
  CONVERSATION_UPDATE("conversation-update"),
  END_OF_CALL_REPORT("end-of-call-report"),
  FUNCTION_CALL("function-call"),
  HANG("hang"),
  PHONE_CALL_CONTROL("phone-call-control"),
  SPEECH_UPDATE("speech-update"),
  STATUS_UPDATE("status-update"),
  TOOL_CALL("tool-calls"),
  TRANSCRIPT("transcript"),
  TRANSFER_DESTINATION_REQUEST("transfer-destination-request"),
  USER_INTERRUPTED("user-interrupted"),
  UNKNOWN_REQUEST_TYPE("unknown-request-type"),
  ;

  companion object {
    internal val logger = KotlinLogging.logger {}
    internal val ServerRequestType.isToolCall get() = this == TOOL_CALL

    val JsonElement.serverRequestType get() = ServerRequestType.fromString(stringValue("message.type"))

    val JsonElement.isAssistantRequest get() = serverRequestType == ASSISTANT_REQUEST
    val JsonElement.isConversationUpdate get() = serverRequestType == CONVERSATION_UPDATE
    val JsonElement.isEndOfCallReport get() = serverRequestType == END_OF_CALL_REPORT
    val JsonElement.isFunctionCall get() = serverRequestType == FUNCTION_CALL
    val JsonElement.isHang get() = serverRequestType == HANG
    val JsonElement.isPhoneCallControl get() = serverRequestType == PHONE_CALL_CONTROL
    val JsonElement.isSpeechUpdate get() = serverRequestType == SPEECH_UPDATE
    val JsonElement.isStatusUpdate get() = serverRequestType == STATUS_UPDATE
    val JsonElement.isToolCall get() = serverRequestType == TOOL_CALL
    val JsonElement.isTransferDestinationRequest get() = serverRequestType == TRANSFER_DESTINATION_REQUEST
    val JsonElement.isUserInterrupted get() = serverRequestType == USER_INTERRUPTED

    private fun fromString(desc: String) =
      try {
        ServerRequestType.entries.first { it.desc == desc }
      } catch (e: Exception) {
        logger.error { "Invalid ServerMessageType: $desc" }
        UNKNOWN_REQUEST_TYPE
      }
  }
}
