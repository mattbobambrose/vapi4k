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

import com.vapi4k.dsl.tools.ToolCache.Companion.toolCallCache
import com.vapi4k.dtos.tools.CommonToolMessageDto
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.JsonElementUtils.toolCallArguments
import com.vapi4k.utils.JsonElementUtils.toolCallId
import com.vapi4k.utils.JsonElementUtils.toolCallList
import com.vapi4k.utils.JsonElementUtils.toolCallName
import com.vapi4k.utils.Utils.errorMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCallResponse(
  var results: MutableList<ToolCallResult> = mutableListOf(),
  var error: String = "",
) {
  companion object {
    fun getToolCallResponse(request: JsonElement) =
      runCatching {
        ToolCallResponse()
          .also { response ->
            var errorMessage = ""
            val toolCallList = request.toolCallList

            for (toolCall in toolCallList) {
              response.also { toolCallResponse ->
                response.results +=
                  ToolCallResult()
                    .also { toolCallResult ->
                      val sessionCacheId = request.sessionCacheId
                      val funcName = toolCall.toolCallName
                      val args = toolCall.toolCallArguments
                      toolCallResult.toolCallId = toolCall.toolCallId
                      toolCallResult.name = funcName
                      runCatching {
                        toolCallCache.getFromCache(sessionCacheId)
                          .getFunction(funcName)
                          .also { func -> logger.info { "Invoking $funcName on method ${func.fqName}" } }
                          .invokeToolMethod(
                            args = args,
                            request = request,
                            message = toolCallResult.message,
                            successAction = { result -> toolCallResult.result = result },
                            errorAction = { errorMsg ->
                              toolCallResult.error = errorMsg
                              errorMessage = errorMsg
                            },
                          )
                      }.getOrElse { e ->
                        val errorMsg = e.message ?: "Error invoking tool $funcName"
                        logger.error { errorMsg }
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
  val message: MutableList<CommonToolMessageDto> = mutableListOf(),
)
