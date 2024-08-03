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
import com.vapi4k.dsl.functions.FunctionUtils.verifyIsToolCall
import com.vapi4k.dsl.functions.FunctionUtils.verifyIsValidReturnType
import com.vapi4k.dsl.functions.FunctionUtils.verifyObjectHasOnlyOneToolCall
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dsl.tools.ToolCache.Companion.toolCallCache
import com.vapi4k.dsl.tools.enums.ToolType
import com.vapi4k.dsl.vapi4k.Endpoint
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.utils.ReflectionUtils.isUnitReturnType
import com.vapi4k.utils.ReflectionUtils.toolCallFunction
import kotlin.reflect.KFunction

@AssistantDslMarker
interface Tools {
  fun tool(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String = "",
    block: Tool.() -> Unit = {},
  )
}

data class ToolsImpl internal constructor(
  internal val model: AbstractModelProperties,
) : Tools {
  override fun tool(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: Tool.() -> Unit,
  ) {
    val endpoint =
      with(AssistantImpl.config) {
        if (endpointName.isEmpty())
          getEmptyEndpoint() ?: defaultToolCallEndpoint
        else
          getEndpoint(endpointName)
      }
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(endpoint, obj, function, block)
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(endpoint, obj, function, block)
      }
    }
  }

  private fun addTool(
    endpoint: Endpoint,
    obj: Any,
    function: KFunction<*>,
    block: Tool.() -> Unit,
  ) {
    model.toolDtos += ToolDto().also { toolDto ->
      populateFunctionDto(model, obj, function, toolDto.function)
      val sessionCacheId =
        if (model.sessionCacheId.isNotSpecified())
          model.sessionCacheId
        else
          model.messageCallId.toSessionCacheId()
      toolCallCache.addToCache(sessionCacheId, model.assistantCacheId, obj, function)

      with(toolDto) {
        type = ToolType.FUNCTION
        async = function.isUnitReturnType
      }

      // Apply block to tool
      ToolImpl(toolDto).apply(block)

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
}
