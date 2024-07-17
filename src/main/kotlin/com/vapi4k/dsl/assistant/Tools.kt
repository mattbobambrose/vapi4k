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

package com.vapi4k.dsl.assistant

import com.vapi4k.AssistantDslMarker
import com.vapi4k.dsl.assistant.FunctionUtils.populateFunctionDto
import com.vapi4k.dsl.assistant.FunctionUtils.verifyObject
import com.vapi4k.dsl.assistant.ToolCache.addToolCallToCache
import com.vapi4k.dsl.assistant.enums.ToolMessageType
import com.vapi4k.dsl.vapi4k.Endpoint
import com.vapi4k.responses.assistant.ToolDto
import com.vapi4k.utils.JsonElementUtils.messageCallId
import com.vapi4k.utils.Utils.isUnitReturnType
import com.vapi4k.utils.Utils.toolFunction

@AssistantDslMarker
data class Tools internal constructor(val model: Model) {
  private fun addTool(
    endpoint: Endpoint,
    obj: Any,
    block: Tool.() -> Unit,
  ) {
    model.tools += ToolDto().also { toolDto ->
      verifyObject(false, obj)
      populateFunctionDto(obj, toolDto.function)
      addToolCallToCache(model.assistant.request.messageCallId, obj)

      with(toolDto) {
        type = "function"
        async = obj.toolFunction.isUnitReturnType
      }

      // Apply block to tool
      val tool = Tool(toolDto).apply(block)

      if (toolDto.messages.firstOrNull { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type } == null) {
        if (tool.futureDelay != -1) {
          error("delayedMillis must be set when using requestDelayedMessage")
        }
      }

      with(toolDto.server) {
        url = endpoint.url
        secret = endpoint.secret
        if (endpoint.timeoutSeconds != -1) {
          timeoutSeconds = endpoint.timeoutSeconds
        }
      }
    }.also { toolDto ->
      if (model.tools.any { toolDto.function.name == it.function.name }) {
        error("Duplicate tool name declared: ${toolDto.function.name}")
      }
    }
  }

  fun tool(
    endpointName: String = "",
    obj: Any,
    block: Tool.() -> Unit = {},
  ) {
    val endpoint = Assistant.config.getEndpoint(endpointName)
    addTool(endpoint, obj, block)
  }

  fun tool(
    obj: Any,
    block: Tool.() -> Unit = {},
  ) {
    val endpoint = with(Assistant.config) { getEmptyEndpoint() ?: defaultToolCallEndpoint }
    addTool(endpoint, obj, block)
  }
}
