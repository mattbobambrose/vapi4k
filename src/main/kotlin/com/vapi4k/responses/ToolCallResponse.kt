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

import com.vapi4k.common.CacheId.Companion.toCacheId
import com.vapi4k.dsl.assistant.tools.ToolCache.getToolCallFromCache
import com.vapi4k.dsl.vapi4k.ToolCallMessageType
import com.vapi4k.dsl.vapi4k.ToolCallRoleType
import com.vapi4k.plugin.Vapi4kLogger.logger
import com.vapi4k.responses.assistant.ToolMessageConditionDto
import com.vapi4k.utils.JsonElementUtils.messageCallId
import com.vapi4k.utils.JsonElementUtils.toolCallArguments
import com.vapi4k.utils.JsonElementUtils.toolCallId
import com.vapi4k.utils.JsonElementUtils.toolCallList
import com.vapi4k.utils.JsonElementUtils.toolCallName
import com.vapi4k.utils.Utils.errorMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCallResponse(private var messageResponse: MessageResponse = MessageResponse()) {
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
                    val cacheKey = request.messageCallId.toCacheId()
                    val funcName = toolCall.toolCallName
                    val args = toolCall.toolCallArguments
                    toolCallResult.toolCallId = toolCall.toolCallId
                    toolCallResult.name = funcName
                    toolCallResult.result =
                      runCatching {
                        getToolCallFromCache(cacheKey)
                          .getFunction(funcName)
                          .invokeToolMethod(args, request, toolCallResult.message) { errorMsg ->
                            toolCallResult.error = errorMsg
                            errorMessage = errorMsg
                          }
                      }.getOrElse { e ->
                        val errorMsg = e.message ?: "Error invoking tool"
                        logger.error { errorMsg }
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
      }.getOrElse { e ->
        logger.error { "Error receiving tool call: ${e.errorMsg}" }
        error("Error receiving tool call: ${e.errorMsg}")
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
  var error: String = "",
  val message: MutableList<ToolCallMessageDto> = mutableListOf(),
)

@Serializable
data class ToolCallMessageDto(
  var type: ToolCallMessageType = ToolCallMessageType.UNSPECIFIED,
  var role: ToolCallRoleType = ToolCallRoleType.UNSPECIFIED,
  var content: String = "",
  val conditions: MutableList<ToolMessageConditionDto> = mutableListOf(),
)
