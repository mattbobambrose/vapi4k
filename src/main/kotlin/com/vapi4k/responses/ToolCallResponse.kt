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

package com.vapi4k.responses

import com.vapi4k.Vapi4k.logger
import com.vapi4k.dsl.assistant.ToolCache.toolCache
import com.vapi4k.dsl.vapi4k.ToolCallMessageType
import com.vapi4k.dsl.vapi4k.ToolCallRoleType
import com.vapi4k.responses.assistant.ToolMessageCondition
import com.vapi4k.utils.JsonElementUtils.phoneNumber
import com.vapi4k.utils.Utils.toolCallArguments
import com.vapi4k.utils.Utils.toolCallId
import com.vapi4k.utils.Utils.toolCallList
import com.vapi4k.utils.Utils.toolCallName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCallResponse(var messageResponse: MessageResponse = MessageResponse()) {
  companion object {
    fun getToolCallResponse(request: JsonElement): ToolCallResponse =
      runCatching {
        ToolCallResponse()
          .also { response ->
            var errorMessage = ""
            val toolCallList = request.toolCallList

            for (toolCall in toolCallList) {
              response.also { toolCallResponse ->
                toolCallResponse.messageResponse.also { messageResponse ->
                  messageResponse.results += ToolCallResult().also { toolCallResult ->

                    val phoneNumber = request.phoneNumber
                    val funcName = toolCall.toolCallName
                    val args = toolCall.toolCallArguments

                    toolCallResult.toolCallId = toolCall.toolCallId
                    toolCallResult.name = funcName

                    toolCallResult.result =
                      runCatching {
                        val functionInfo = toolCache[phoneNumber] ?: error("Session not found: $phoneNumber")
                        functionInfo.getFunction(funcName)
                          .invokeToolMethod(args, request, toolCallResult.message) { errorMsg ->
                            toolCallResult.error = errorMsg
                            errorMessage = errorMsg
                          }
                      }.getOrElse { e ->
                        val errorMsg = e.message ?: "Error invoking tool"
                        logger.error(e) { errorMsg }
                        errorMsg
                      }
                  }

                  if (errorMessage.isNotEmpty()) {
                    messageResponse.error = errorMessage
                  }
                }
              }
            }
          }
      }.getOrElse {
        logger.error(it) { "Error receiving tool call: ${it.message}" }
        error("Error receiving tool call: ${it.message}")
      }
  }
}

@Serializable
data class MessageResponse(
  var results: MutableList<ToolCallResult> = mutableListOf(),
  var error: String = "",
)

@Serializable
data class ToolCallResult(
  var toolCallId: String = "",
  var name: String = "",
  var result: String = "",
  // TODO: Ask Vapi if this should be messages (plural)
  var message: MutableList<ToolCallMessage> = mutableListOf(),
  var error: String = "",
)

@Serializable
data class ToolCallMessage(
  var type: ToolCallMessageType? = null,
  var role: ToolCallRoleType? = null,
  var content: String = "",
  var conditions: List<ToolMessageCondition> = emptyList(),
)
