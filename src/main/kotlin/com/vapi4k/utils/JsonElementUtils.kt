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

package com.vapi4k.utils

import com.vapi4k.dsl.vapi4k.ServerRequestType
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import kotlinx.serialization.json.JsonElement

object JsonElementUtils {

  val JsonElement.requestType get() = ServerRequestType.fromString(this["message.type"].stringValue)

  val JsonElement.isAssistantRequest get() = requestType == ServerRequestType.ASSISTANT_REQUEST
  val JsonElement.isConversationUpdate get() = requestType == ServerRequestType.CONVERSATION_UPDATE
  val JsonElement.isEndOfCallReport get() = requestType == ServerRequestType.END_OF_CALL_REPORT
  val JsonElement.isFunctionCall get() = requestType == ServerRequestType.FUNCTION_CALL
  val JsonElement.isHang get() = requestType == ServerRequestType.HANG
  val JsonElement.isPhoneCallControl get() = requestType == ServerRequestType.PHONE_CALL_CONTROL
  val JsonElement.isSpeechUpdate get() = requestType == ServerRequestType.SPEECH_UPDATE
  val JsonElement.isStatusUpdate get() = requestType == ServerRequestType.STATUS_UPDATE
  val JsonElement.isToolCall get() = requestType == ServerRequestType.TOOL_CALL
  val JsonElement.isTransferDestinationRequest get() = requestType == ServerRequestType.TRANSFER_DESTINATION_REQUEST
  val JsonElement.isUserInterrupted get() = requestType == ServerRequestType.USER_INTERRUPTED

  val JsonElement.customerNumber
    get() = if (isAssistantRequest)
      this["message.call.customer.number"].stringValue
    else
      error("JsonElement is not an assistant request")

  val JsonElement.statusUpdateError: String
    get() = if (isStatusUpdate)
      this["message.inboundPhoneCallDebuggingArtifacts.assistantRequestError"].stringValue
    else
      error("Not a status update message. Use .isStatusUpdate before calling .statusUpdateError")

  val JsonElement.hasStatusUpdateError: Boolean get() = statusUpdateError.isNotEmpty()
}
