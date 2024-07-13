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

import com.vapi4k.common.JsonExtensions.get
import com.vapi4k.common.JsonExtensions.jsonList
import com.vapi4k.common.JsonExtensions.stringValue
import com.vapi4k.dsl.assistant.ToolCallService
import com.vapi4k.enums.ToolCallMessageType
import com.vapi4k.enums.ToolCallMessageType.REQUEST_COMPLETE
import com.vapi4k.enums.ToolCallMessageType.REQUEST_FAILED
import com.vapi4k.enums.ToolCallRoleType
import com.vapi4k.enums.ToolCallRoleType.ASSISTANT
import com.vapi4k.plugin.Vapi4kPlugin.logger
import com.vapi4k.responses.ResponseUtils.deriveNames
import com.vapi4k.responses.ResponseUtils.invokeMethod
import com.vapi4k.responses.assistant.ToolMessageCondition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCallResponse(var messageResponse: MessageResponse = MessageResponse()) {
  companion object {
    fun getToolCallResponse(request: JsonElement): ToolCallResponse {
      val result = runCatching {
        ToolCallResponse()
          .also { response ->
            var errorMessage = ""
            val toolCallList = request["message.toolCallList"].jsonList

            for (toolCall in toolCallList) {
              response.apply {
                messageResponse.apply {
                  results += ToolCallResult().apply {
                    toolCallId = toolCall["id"].stringValue
                    val funcName = toolCall["function.name"].stringValue
                    val args = toolCall["function.arguments"]
                    name = funcName

                    val (className, methodName) = deriveNames(funcName)
                    val serviceInstance = runCatching {
                      with(Class.forName(className)) {
                        kotlin.objectInstance ?: constructors.toList().first().newInstance()
                      }
                    }

                    if (serviceInstance.isSuccess) {
                      val service = serviceInstance.getOrNull() ?: error("Error creating instance of $className")
                      val toolResult = runCatching { invokeMethod(service, methodName, args) }
                      if (toolResult.isSuccess) {
                        result = toolResult.getOrNull().orEmpty()
                        if (service is ToolCallService) {
                          val (content, role) = service.onRequestComplete(request, result)
                          requestCompleteMessage(content, role)
                        }
                      } else {
                        val errorMsg =
                          "Error invoking method $className.$methodName: ${toolResult.exceptionOrNull()?.message}"
                        error = errorMsg
                        if (service is ToolCallService) {
                          val content = service.onRequestFailed(request, error)
                          requestFailedMessage(content)
                        }
                        logger.error(toolResult.exceptionOrNull()) { errorMsg }
                        errorMessage = errorMsg
                      }
                    } else {
                      val errorMsg =
                        "Error creating instance of $className: ${serviceInstance.exceptionOrNull()?.message}"
                      error = errorMsg
                      logger.error(serviceInstance.exceptionOrNull()) { errorMsg }
                      errorMessage = errorMsg
                    }
                  }

                  if (errorMessage.isNotEmpty()) {
                    error = errorMessage
                  }
                }
              }
            }
          }
      }
      return if (result.isSuccess) {
        result.getOrNull() ?: error("Error receiving tool call response: null result value")
      } else {
        logger.error(result.exceptionOrNull()) { "Error receiving tool call: ${result.exceptionOrNull()?.message}" }
        error("Error receiving tool call: ${result.exceptionOrNull()?.message}")
      }
    }

    private fun ToolCallResult.requestCompleteMessage(
      msg: String,
      roleType: ToolCallRoleType = ASSISTANT,
      vararg conditions: ToolMessageCondition,
    ) {
      message += ToolCallMessage().apply {
        type = REQUEST_COMPLETE
        role = roleType
        content = msg
        if (conditions.isNotEmpty()) {
          this.conditions = conditions.toList()
        }
      }
    }

    private fun ToolCallResult.requestFailedMessage(
      msg: String,
      vararg conditions: ToolMessageCondition,
    ) {
      message += ToolCallMessage().apply {
        type = REQUEST_FAILED
        content = msg
        if (conditions.isNotEmpty()) {
          this.conditions = conditions.toList()
        }
      }
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
  // TODO: Ask Vapi if this should be messages
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
