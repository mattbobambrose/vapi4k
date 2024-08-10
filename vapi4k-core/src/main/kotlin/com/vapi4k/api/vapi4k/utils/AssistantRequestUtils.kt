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

package com.vapi4k.api.vapi4k.utils

import com.vapi4k.utils.enums.ServerRequestType.Companion.isFunctionCall
import com.vapi4k.utils.enums.ServerRequestType.Companion.isStatusUpdate
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.get
import kotlinx.serialization.json.JsonElement

object AssistantRequestUtils {
  val JsonElement.isAssistantResponse get() = containsKey("assistant")
  val JsonElement.isAssistantIdResponse get() = containsKey("assistantId")
  val JsonElement.isSquadResponse get() = containsKey("squad")
  val JsonElement.isSquadIdResponse get() = containsKey("squadId")

  val JsonElement.id get() = this["id"].stringValue
  val JsonElement.messageCallId get() = this["message.call.id"].stringValue
  val JsonElement.phoneNumber get() = this["message.call.customer.number"].stringValue

  val JsonElement.toolCallName get() = this["function.name"].stringValue
  val JsonElement.toolCallArguments get() = this["function.arguments"]

  val JsonElement.statusUpdateError: String
    get() = if (isStatusUpdate)
      runCatching {
        this.get("dd")["message.inboundPhoneCallDebuggingArtifacts.assistantRequestError"].stringValue
      }.getOrElse { "" }
    else
      error("Not a status update message. Use .isStatusUpdate before calling .statusUpdateError")

  fun JsonElement.hasStatusUpdateError(): Boolean = statusUpdateError.isNotEmpty()

  val JsonElement.functionName
    get() = if (isFunctionCall)
      this["message.functionCall.name"].stringValue
    else
      error("JsonElement is not a function call")

  val JsonElement.functionParameters
    get() = if (isFunctionCall)
      this["message.functionCall.parameters"]
    else
      error("JsonElement is not a function call")
}
