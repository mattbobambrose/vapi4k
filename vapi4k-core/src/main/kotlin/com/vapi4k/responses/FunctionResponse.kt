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

import com.vapi4k.api.vapi4k.AssistantRequestUtils.functionName
import com.vapi4k.api.vapi4k.AssistantRequestUtils.functionParameters
import com.vapi4k.common.AssistantId
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.utils.common.Utils.errorMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class FunctionResponse(
  var result: String = "",
) {
  companion object {
    suspend fun getFunctionCallResponse(
      application: AbstractApplicationImpl,
      request: JsonElement,
      sessionId: SessionId,
      assistantId: AssistantId,
    ) = FunctionResponse()
      .also { response ->
        val funcName = request.functionName
        val args = request.functionParameters
        runCatching {
          if (application.containsFunctionInCache(sessionId, assistantId, funcName)) {
            application.getFunctionFromCache(sessionId, assistantId, funcName)
              .invokeToolMethod(
                isTool = false,
                request = request,
                args = args,
                messageDtos = mutableListOf(),
                successAction = { result -> response.result = result },
                errorAction = { result -> response.result = result },
              )
          } else {
            error("Function not found: $funcName")
          }
        }.getOrElse { e ->
          val errorMsg = e.message ?: "Error invoking function: $funcName ${e.errorMsg}"
          logger.error(e) { errorMsg }
        }
      }
  }
}
