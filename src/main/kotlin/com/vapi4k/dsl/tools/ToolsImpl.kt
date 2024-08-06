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

import com.vapi4k.api.tools.Tool
import com.vapi4k.api.tools.ToolWithMetaData
import com.vapi4k.api.tools.Tools
import com.vapi4k.api.tools.TransferTool
import com.vapi4k.api.tools.enums.ToolType
import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.dsl.functions.FunctionUtils.populateFunctionDto
import com.vapi4k.dsl.functions.FunctionUtils.verifyIsToolCall
import com.vapi4k.dsl.functions.FunctionUtils.verifyIsValidReturnType
import com.vapi4k.dsl.functions.FunctionUtils.verifyObjectHasOnlyOneToolCall
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
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
    block: Tool.() -> Unit,
  ) {
    processFunctions(ToolType.FUNCTION, functions, obj, block)
  }

  override fun dtmf(
    obj: Any,
    vararg functions: KFunction<*>,
    block: Tool.() -> Unit,
  ) {
    processFunctions(ToolType.DTMF, functions, obj, block)
  }

  override fun endCall(
    obj: Any,
    vararg functions: KFunction<*>,
    block: Tool.() -> Unit,
  ) {
    processFunctions(ToolType.END_CALL, functions, obj, block)
  }

  override fun voiceMail(
    obj: Any,
    vararg functions: KFunction<*>,
    block: Tool.() -> Unit,
  ) {
    processFunctions(ToolType.VOICEMAIL, functions, obj, block)
  }

  override fun ghl(
    obj: Any,
    vararg functions: KFunction<*>,
    block: ToolWithMetaData.() -> Unit,
  ) {
    processFunctionsWithMetaData(ToolType.GHL, functions, obj, block)
  }

  override fun make(
    obj: Any,
    vararg functions: KFunction<*>,
    block: ToolWithMetaData.() -> Unit,
  ) {
    processFunctionsWithMetaData(ToolType.MAKE, functions, obj, block)
  }

  override fun transfer(
    obj: Any,
    vararg functions: KFunction<*>,
    block: TransferTool.() -> Unit,
  ) {
    processFunctionsWithDestinations(ToolType.TRANSFER_CALL, functions, obj, block)
  }

  private fun processFunctions(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    block: Tool.() -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function) {
        ToolImpl(it).apply(block)
      }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function) {
          ToolImpl(it).apply(block)
        }
      }
    }
  }

  private fun processFunctionsWithMetaData(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    block: ToolWithMetaData.() -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function) {
        ToolWithMetaDataImpl(it).apply(block)
      }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function) {
          ToolWithMetaDataImpl(it).apply(block)
        }
      }
    }
  }

  private fun processFunctionsWithDestinations(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    block: TransferTool.() -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function) {
        TransferToolImpl(it).apply(block)
      }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function) {
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
    implInitBlock: (ToolDto) -> Unit,
  ) {
    model.toolDtos += ToolDto().also { toolDto ->
      populateFunctionDto(model, obj, function, toolDto.function)
      val sessionCacheId = getSessionCacheId()
      val application = (model.application as Vapi4kApplicationImpl)
      application.toolCache.addToCache(sessionCacheId, model.assistantCacheId, obj, function)

      with(toolDto) {
        type = toolType
        async = function.isUnitReturnType
      }

      // Apply block to tool
      implInitBlock(toolDto)
    }.also { toolDto ->
      if (model.toolDtos.any { toolDto.function.name == it.function.name }) {
        error("Duplicate tool name declared: ${toolDto.function.name}")
      }
    }
  }
}
