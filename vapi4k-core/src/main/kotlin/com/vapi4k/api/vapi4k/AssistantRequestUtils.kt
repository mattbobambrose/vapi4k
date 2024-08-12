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

package com.vapi4k.api.vapi4k

import com.vapi4k.utils.enums.ServerRequestType.Companion.isFunctionCall
import com.vapi4k.utils.enums.ServerRequestType.Companion.isStatusUpdate
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.get
import kotlinx.serialization.json.JsonElement

/**
Utility functions for assistant requests.
 */
object AssistantRequestUtils {
  /**
  Check if the JsonElement is an assistant response.
   */
  val JsonElement.isAssistantResponse get() = containsKey("messageResponse.assistant")

  /**
  Check if the JsonElement is an assistant id response.
   */
  val JsonElement.isAssistantIdResponse get() = containsKey("messageResponse.assistantId")

  /**
  Check if the JsonElement is a squad response.
   */
  val JsonElement.isSquadResponse get() = containsKey("messageResponse.squad")

  /**
  Check if the JsonElement is a squad id response.
   */
  val JsonElement.isSquadIdResponse get() = containsKey("messageResponse.squadId")

  /**
  Extract the id from a JsonElement and throws an error if the JsonElement doesn't have an id.
   */
  val JsonElement.id get() = stringValue("id")

  /**
  Extract the call id from a JsonElement and throws an error if the JsonElement is not a call.
   */
  val JsonElement.messageCallId get() = stringValue("message.call.id")

  /**
  Extract the phone number from a JsonElement and throws an error if the JsonElement is not a phone number.
   */
  val JsonElement.phoneNumber get() = stringValue("message.call.customer.number")

  /**
  Extract the tool call name from a tool call and throws an error if the JsonElement is not a tool call.
   */
  val JsonElement.toolCallName get() = stringValue("function.name")

  /**
  Extract the tool call arguments from a tool call and throws an error if the JsonElement is not a tool call.
   */
  val JsonElement.toolCallArguments get() = this["function.arguments"]

  /**
  Extract the assistant request error message from a status update message and throws an error if the JsonElement is not a status update message.
   */
  val JsonElement.statusUpdateError: String
    get() = if (isStatusUpdate)
      runCatching {
        stringValue("message.inboundPhoneCallDebuggingArtifacts.assistantRequestError")
      }.getOrElse { "" }
    else
      error("Not a status update message. Use .isStatusUpdate before calling .statusUpdateError")

  /**
  Check if the JsonElement has a status update error message.
   */
  fun JsonElement.hasStatusUpdateError(): Boolean = statusUpdateError.isNotEmpty()

  /**
  Extract the function name from a function call and throws an error if the JsonElement is not a function call.
   */
  val JsonElement.functionName
    get() = if (isFunctionCall)
      stringValue("message.functionCall.name")
    else
      error("JsonElement is not a function call")

  /**
  Extract the function parameters from a function call and throws an error if the JsonElement is not a function call.
   */
  val JsonElement.functionParameters
    get() = if (isFunctionCall)
      this["message.functionCall.parameters"]
    else
      error("JsonElement is not a function call")
}
