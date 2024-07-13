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

import com.vapi4k.dsl.assistant.AssistantDsl.isAsync
import com.vapi4k.dsl.vapi4k.Endpoint
import com.vapi4k.enums.ToolMessageType
import com.vapi4k.responses.assistant.ToolDto

@AssistantDslMarker
data class Tools(val model: Model) {
  private fun addTool(endpoint: Endpoint, obj: Any, block: Tool.() -> Unit) {
    model.modelDto.tools += ToolDto().apply {
      val method = AssistantDsl.verifyObject(false, obj)
      type = "function"
      async = method.isAsync
      messages = mutableListOf()
      AssistantDsl.populateFunctionDto(obj, function)
      val tool = Tool(this)
      block(tool)
      if (messages.firstOrNull { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type } == null) {
        if (tool.futureDelay != -1) {
          error("delayedMillis must be set when using requestDelayedMessage")
        }
      }

      with(server) {
        url = endpoint.url
        secret = endpoint.secret
        if (endpoint.timeoutSeconds != -1) {
          timeoutSeconds = endpoint.timeoutSeconds
        }
      }
    }.also { tool ->
      if (model.modelDto.tools.any { tool.function.name == it.function.name }) {
        error("Duplicate tool name declared: ${tool.function.name}")
      }
    }
  }

  fun tool(endpointName: String = "", obj: Any, block: Tool.() -> Unit = {}) {
    val endpoint = model.config.getEndpoint(endpointName)
    addTool(endpoint, obj, block)
  }

  fun tool(obj: Any, block: Tool.() -> Unit = {}) {
    val endpoint = model.config.getEmptyEndpoint() ?: model.config.defaultToolCallEndpoint
    addTool(endpoint, obj, block)
  }
}
