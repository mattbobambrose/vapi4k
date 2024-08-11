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

import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.id
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.toolCallArguments
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.toolCallName
import com.vapi4k.dsl.assistant.ExternalToolCallResponseImpl
import com.vapi4k.dsl.tools.ManualToolImpl
import com.vapi4k.dsl.toolservice.RequestCompleteMessagesImpl
import com.vapi4k.dsl.toolservice.RequestFailedMessagesImpl
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import com.vapi4k.dtos.tools.CommonToolMessageDto
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.JsonElementUtils.toolCallList
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCallResponse(
  var results: MutableList<ToolCallResult> = mutableListOf(),
  var error: String = "",
) {
  companion object {
    fun getToolCallResponse(
      application: Vapi4kApplicationImpl,
      request: JsonElement,
    ) = runCatching {
      ToolCallResponse()
        .also { response ->
          var errorMessage = ""

          request.toolCallList
            .forEach { toolCall ->
              response.also { toolCallResponse ->
                response.results +=
                  ToolCallResult()
                    .also { toolCallResult ->
                      val sessionCacheId = request.sessionCacheId
                      val funcName = toolCall.toolCallName
                      val args = toolCall.toolCallArguments
                      toolCallResult.toolCallId = toolCall.id
                      toolCallResult.name = funcName
                      val errorAction = { errorMsg: String ->
                        toolCallResult.error = errorMsg
                        errorMessage = errorMsg
                      }
                      runCatching {
                        if (application.containsFunctionInCache(sessionCacheId, funcName)) {
                          application.getFunctionInfoFromCache(sessionCacheId)
                            .getFunction(funcName)
                            .also { func -> logger.info { "Invoking $funcName on method ${func.fqName}" } }
                            .invokeToolMethod(
                              isTool = true,
                              args = args,
                              request = request,
                              messageDtos = toolCallResult.messageDtos,
                              successAction = { result -> toolCallResult.result = result },
                              errorAction = errorAction,
                            )
                        } else {
                          // Invoke external tool
                          if (!application.manualToolCache.containsTool(funcName)) {
                            error("Tool $funcName not found")
                          } else {
                            val func: ManualToolImpl = application.manualToolCache.getTool(funcName)

                            if (!func.isToolCallRequestInitialized()) {
                              error("onToolCallRequest{} not declared in $funcName")
                            } else {
                              runBlocking {
                                val completeMessages = RequestCompleteMessagesImpl()
                                val failedMessages = RequestFailedMessagesImpl()
                                val resp =
                                  ExternalToolCallResponseImpl(completeMessages, failedMessages, toolCallResult)
                                runCatching {
                                  func.toolCallRequest.invoke(resp, request)
                                  toolCallResult.messageDtos.addAll(completeMessages.messageList.map { it.dto })
                                }.onFailure {
                                  toolCallResult.messageDtos.addAll(failedMessages.messageList.map { it.dto })
                                }
                                toolCallResult.apply {
                                  toolCallId = toolCall.stringValue("id")
                                }
                              }
                            }
                          }
                        }
                      }.getOrElse { e ->
                        val errorMsg = e.message ?: "Error invoking tool $funcName"
                        logger.error { errorMsg }
                        errorAction(errorMsg)
                      }
                    }

                if (errorMessage.isNotEmpty()) {
                  response.error = errorMessage
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
data class ToolCallResult(
  var toolCallId: String = "",
  var name: String = "",
  var result: String = "",
  // TODO: Ask Vapi if this should be messages (plural)
  var error: String = "",
  @SerialName("message")
  val messageDtos: MutableList<CommonToolMessageDto> = mutableListOf(),
)
