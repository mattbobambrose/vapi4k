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
import com.vapi4k.dsl.functions.FunctionUtils
import com.vapi4k.dsl.functions.FunctionUtils.verifyIsToolCall
import com.vapi4k.dsl.functions.FunctionUtils.verifyIsValidReturnType
import com.vapi4k.dsl.functions.FunctionUtils.verifyObjectHasOnlyOneToolCall
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dsl.tools.enums.ToolType
import com.vapi4k.dsl.vapi4k.Endpoint
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.utils.ReflectionUtils.isUnitReturnType
import com.vapi4k.utils.ReflectionUtils.toolCallFunction
import kotlin.reflect.KFunction

data class ToolsImpl internal constructor(
  internal val model: AbstractModelProperties,
) : Tools {
  override fun tool(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: Tool.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctions(ToolType.FUNCTION, functions, obj, endpoint, block)
  }

  override fun dtmf(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: Tool.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctions(ToolType.DTMF, functions, obj, endpoint, block)
  }

  override fun endCall(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: Tool.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctions(ToolType.END_CALL, functions, obj, endpoint, block)
  }

  override fun voiceMail(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: Tool.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctions(ToolType.VOICEMAIL, functions, obj, endpoint, block)
  }

  override fun ghl(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: ToolWithMetaData.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctionsWithMetaData(ToolType.GHL, functions, obj, endpoint, block)
  }

  override fun make(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: ToolWithMetaData.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctionsWithMetaData(ToolType.MAKE, functions, obj, endpoint, block)
  }

  override fun transfer(
    obj: Any,
    vararg functions: KFunction<*>,
    endpointName: String,
    block: TransferTool.() -> Unit,
  ) {
    val endpoint = model.application.getEndpoint(endpointName)
    processFunctionsWithDestinations(ToolType.TRANSFER_CALL, functions, obj, endpoint, block)
  }

  private fun processFunctions(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    endpoint: Endpoint,
    block: Tool.() -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function, endpoint) {
        ToolImpl(it).apply(block)
      }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function, endpoint) {
          ToolImpl(it).apply(block)
        }
      }
    }
  }

  private fun processFunctionsWithMetaData(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    endpoint: Endpoint,
    block: ToolWithMetaData.() -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function, endpoint) {
        ToolWithMetaDataImpl(it).apply(block)
      }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function, endpoint) {
          ToolWithMetaDataImpl(it).apply(block)
        }
      }
    }
  }

  private fun processFunctionsWithDestinations(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    endpoint: Endpoint,
    block: TransferTool.() -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function, endpoint) {
        TransferToolImpl(it).apply(block)
      }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function, endpoint) {
          TransferToolImpl(it).apply(block)
        }
      }
    }
  }

  private fun getSessionCacheId() =
    if (model.sessionCacheId.isNotSpecified())
      model.sessionCacheId
    else
      model.messageCallId.toSessionCacheId()

  private fun addTool(
    toolType: ToolType,
    obj: Any,
    function: KFunction<*>,
    endpoint: Endpoint,
    implInitBlock: (ToolDto) -> Unit,
  ) {
    model.toolDtos += ToolDto().also { toolDto ->
      FunctionUtils.populateFunctionDto(toolType, model, obj, function, toolDto.function)
      val sessionCacheId = getSessionCacheId()
      ToolCache.toolCallCache.addToCache(sessionCacheId, model.assistantCacheId, toolType, obj, function)

      with(toolDto) {
        type = toolType
        async = function.isUnitReturnType
      }

      // Apply block to tool
      implInitBlock(toolDto)

      with(toolDto.server) {
        url = endpoint.serverUrl
        secret = endpoint.serverSecret
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
