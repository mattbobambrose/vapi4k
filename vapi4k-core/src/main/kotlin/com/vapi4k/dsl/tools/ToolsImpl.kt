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

import com.vapi4k.api.tools.BaseTool
import com.vapi4k.api.tools.ExternalTool
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
  override fun vapi4kTool(
    obj: Any,
    vararg functions: KFunction<*>,
    block: Tool.() -> Unit,
  ) = processFunctions(ToolType.FUNCTION, functions, obj) {
    ToolImpl("vapi4kTool", it).apply(block)
  }

  override fun externalTool(block: ExternalTool.() -> Unit) {
    val toolDto = ToolDto(ToolType.FUNCTION).also { model.toolDtos += it }
    ExternalToolImpl("externalTool", toolDto).apply(block).checkIfServerCalled()
    if (toolDto.functionDto.name.isBlank()) error("externalTool{} parameter name is required")
  }

  override fun dtmfTool(block: BaseTool.() -> Unit) {
    val toolDto = ToolDto(ToolType.DTMF).also { model.toolDtos += it }
    BaseToolImpl("dtmfTool", toolDto).apply(block).checkIfServerCalled()
  }

  override fun endCallTool(block: BaseTool.() -> Unit) {
    val toolDto = ToolDto(ToolType.END_CALL).also { model.toolDtos += it }
    BaseToolImpl("endCallTool", toolDto).apply(block).checkIfServerCalled()
  }

  override fun voiceMailTool(block: BaseTool.() -> Unit) {
    val toolDto = ToolDto(ToolType.VOICEMAIL).also { model.toolDtos += it }
    BaseToolImpl("voiceMailTool", toolDto).apply(block).checkIfServerCalled()
  }

  override fun ghlTool(block: ToolWithMetaData.() -> Unit) {
    val toolDto = ToolDto(ToolType.GHL).also { model.toolDtos += it }
    ToolWithMetaDataImpl("ghlTool", toolDto).apply(block).checkIfServerCalled()
  }

  override fun makeTool(block: ToolWithMetaData.() -> Unit) {
    val toolDto = ToolDto(ToolType.MAKE).also { model.toolDtos += it }
    ToolWithMetaDataImpl("makeTool", toolDto).apply(block).checkIfServerCalled()
  }

  override fun transferTool(block: TransferTool.() -> Unit) {
    val toolDto = ToolDto(ToolType.TRANSFER_CALL).also { model.toolDtos += it }
    TransferToolImpl("transferTool", toolDto).apply(block)
  }

  private fun processFunctions(
    toolType: ToolType,
    functions: Array<out KFunction<*>>,
    obj: Any,
    block: (ToolDto) -> Unit,
  ) {
    if (functions.isEmpty()) {
      verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      verifyIsValidReturnType(true, function)
      addTool(toolType, obj, function) { block(it) }
    } else {
      functions.forEach { function ->
        verifyIsToolCall(true, function)
        verifyIsValidReturnType(true, function)
        addTool(toolType, obj, function) { block(it) }
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
      populateFunctionDto(model, obj, function, toolDto.functionDto)
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
      if (model.toolDtos.any { toolDto.functionDto.name == it.functionDto.name }) {
        error("Duplicate tool name declared: ${toolDto.functionDto.name}")
      }
    }
  }
}
