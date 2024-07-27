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

import com.vapi4k.common.AssistantCacheId
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dtos.model.FunctionDto
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.functions
import com.vapi4k.utils.ReflectionUtils.hasTool
import com.vapi4k.utils.ReflectionUtils.param
import com.vapi4k.utils.ReflectionUtils.toolCall
import com.vapi4k.utils.ReflectionUtils.toolFunction
import com.vapi4k.utils.ReflectionUtils.toolMethod
import java.lang.reflect.Method
import java.lang.reflect.Parameter

internal object FunctionUtils {
  private val legalTypes = setOf(String::class, Unit::class)

  fun verifyObject(
    isFunction: Boolean,
    obj: Any,
  ) {
    val cnt = obj.functions.count { it.hasTool }

    when {
      cnt == 0 ->
        error("No method with ${ToolCall::class.simpleName} annotation found in class ${obj::class.qualifiedName}")

      cnt > 1 ->
        error("Only one method with ${ToolCall::class.simpleName} annotation is allowed in class ${obj::class.qualifiedName}")
    }

    return with(obj.functions.first { it.hasTool }) {
      val returnClass = returnType.asKClass()
      if (returnClass !in legalTypes) {
        val str = if (isFunction) "Function" else "Tool"
        error("$str $name must return a String or Unit, but instead returns ${returnClass.qualifiedName}")
      }
    }
  }

  fun populateFunctionDto(
    model: AbstractModelProperties,
    obj: Any,
    functionDto: FunctionDto,
  ) {
    val method = obj.toolMethod
    val function = obj.toolFunction

    ToolCallInfo(model.assistantCacheId, method).also { tci ->
      functionDto.name = tci.llmName
      functionDto.description = tci.llmDescription
      // TODO: This might be always object
      functionDto.parameters.type = "object"  // llmReturnType

      // Reduce the size of kparams if the first parameter is the object itself
      val jparams = method.parameters.toList()
      val kparams =
        if (jparams.size == function.parameters.size)
          function.parameters
        else
          function.parameters.subList(1, function.parameters.size)

      jparams
        .zip(kparams)
        .forEach { (jParam, kParam) ->
          val name = kParam.name ?: jParam.name
          if (!kParam.isOptional)
            functionDto.parameters.required += name
          functionDto.parameters.properties[name] = FunctionDto.FunctionParameters.FunctionPropertyDesc(
            type = jParam.llmType,
            description = jParam.param?.description ?: "The $name parameter"
          )
        }
    }
  }

  private val Parameter.llmType: String
    get() = when (type) {
      String::class.java -> "string"
      Int::class.java -> "integer"
      Boolean::class.java -> "boolean"
      else -> "object"
    }

  class ToolCallInfo(
    private val assistantCacheId: AssistantCacheId,
    private val method: Method,
  ) {
    private val toolCall get() = method.toolCall!!
    private val toolHasName get() = toolCall.name.isNotEmpty()
    private val toolHasDescription get() = toolCall.description.isNotEmpty()

    val llmName get() = "${(if (toolHasName) toolCall.name else method.name)}_$assistantCacheId"

    val llmDescription
      get() =
        when {
          toolHasDescription -> toolCall.description
          toolHasName -> toolCall.name
          else -> method.name
        }

    val llmReturnType
      get() = when (method.returnType) {
        String::class.java -> "string"
        Int::class.java -> "integer"
        Boolean::class.java -> "boolean"
        else -> "object"
      }
  }
}
