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

package com.vapi4k.dsl.vapi4k

enum class ServerRequestType(val desc: String) {
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
  UNKNOWN("unknown");

  companion object {
    internal val ServerRequestType.isToolCall get() = this == TOOL_CALL

    internal fun fromString(desc: String) =
      try {
        entries.first { it.desc == desc }
      } catch (e: Exception) {
        Vapi4kDsl.logger.error(e) { "Invalid ServerMessageType: $desc" }
        UNKNOWN
      }
  }
}
