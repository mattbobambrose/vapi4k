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

package com.vapi4k.dsl.tools

import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.functions.FunctionUtils.populateFunctionDto
import com.vapi4k.dsl.functions.FunctionUtils.verifyObject
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dsl.tools.ToolCache.Companion.toolCallCache
import com.vapi4k.dsl.tools.enums.ToolType
import com.vapi4k.dsl.vapi4k.Endpoint
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.utils.ReflectionUtils.isUnitReturnType
import com.vapi4k.utils.ReflectionUtils.toolFunction

@AssistantDslMarker
interface Tools {
  fun tool(
    endpointName: String,
    obj: Any,
    block: Tool.() -> Unit = {},
  )

  fun tool(
    obj: Any,
    block: Tool.() -> Unit = {},
  )
}

data class ToolsImpl internal constructor(
  internal val model: AbstractModelProperties,
) : Tools {
  private fun addTool(
    endpoint: Endpoint,
    obj: Any,
    block: Tool.() -> Unit,
  ) {
    model.toolDtos += ToolDto().also { toolDto ->
      verifyObject(false, obj)
      populateFunctionDto(model, obj, toolDto.function)
      val sessionCacheId =
        if (model.sessionCacheId.isNotSpecified())
          model.sessionCacheId
        else
          model.messageCallId.toSessionCacheId()
      toolCallCache.addToCache(sessionCacheId, model.assistantCacheId, obj)

      with(toolDto) {
        type = ToolType.FUNCTION
        async = obj.toolFunction.isUnitReturnType
      }

      // Apply block to tool
      ToolImpl(toolDto).apply(block)
//        .apply { verifyFutureDelay(toolDto) }

      with(toolDto.server) {
        url = endpoint.serverUrl
        secret = endpoint.serverUrlSecret
        if (endpoint.timeoutSeconds != -1) {
          timeoutSeconds = endpoint.timeoutSeconds
        }
      }
    }.also { toolDto ->
      if (model.toolDtos.any { toolDto.function.name == it.function.name }) {
        error("Duplicate tool name declared: ${toolDto.function.name}")
      }
    }
  }

  override fun tool(
    endpointName: String,
    obj: Any,
    block: Tool.() -> Unit,
  ) {
    val endpoint = AssistantImpl.config.getEndpoint(endpointName)
    addTool(endpoint, obj, block)
  }

  override fun tool(
    obj: Any,
    block: Tool.() -> Unit,
  ) {
    val endpoint = with(AssistantImpl.config) { getEmptyEndpoint() ?: defaultToolCallEndpoint }
    addTool(endpoint, obj, block)
  }

  companion object {
//    private fun ToolImpl.verifyFutureDelay(toolDto: ToolDto) {
//      if (toolDto.messages.firstOrNull { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc } == null) {
//        if (futureDelay != -1) {
//          error("delayedMillis must be set when using requestDelayedMessage")
//        }
//      }
//    }
  }
}
