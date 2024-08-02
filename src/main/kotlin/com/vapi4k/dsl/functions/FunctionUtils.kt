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

package com.vapi4k.dsl.functions

import com.vapi4k.common.AssistantCacheId
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dtos.functions.FunctionDto
import com.vapi4k.dtos.functions.FunctionPropertyDescDto
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.functions
import com.vapi4k.utils.ReflectionUtils.hasToolCallAnnotation
import com.vapi4k.utils.ReflectionUtils.paramAnnotation
import com.vapi4k.utils.ReflectionUtils.toolCallAnnotation
import com.vapi4k.utils.ReflectionUtils.toolCallFunction
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

internal object FunctionUtils {
  private val allowedParamTypes = setOf(String::class, Int::class, Double::class, Boolean::class)
  private val allowedReturnTypes = setOf(String::class, Unit::class)

  fun verifyObject(
    isFunction: Boolean,
    obj: Any,
  ) {
    val cnt = obj.functions.count { it.hasToolCallAnnotation }

    when {
      cnt == 0 ->
        error(
          "No method with ${ToolCall::class.simpleName} annotation found in class ${obj::class.qualifiedName}",
        )

      cnt > 1 ->
        error(
          "Only one method with ${ToolCall::class.simpleName} annotation is allowed in class ${obj::class.qualifiedName}",
        )
    }

    return with(obj.functions.first { it.hasToolCallAnnotation }) {
      val returnClass = returnType.asKClass()
      if (returnClass !in allowedReturnTypes) {
        val str = if (isFunction) "Function" else "Tool"
        error("$str $name returns a ${returnClass.qualifiedName}. Allowed return types are String or Unit")
      }
    }
  }

  fun populateFunctionDto(
    model: AbstractModelProperties,
    obj: Any,
    functionDto: FunctionDto,
  ) {
    val function = obj.toolCallFunction

    ToolCallInfo(model.assistantCacheId, function).also { toolCallInfo ->
      functionDto.name = toolCallInfo.llmName
      functionDto.description = toolCallInfo.llmDescription
      // TODO: This might be always object
      functionDto.parameters.type = "object" // llmReturnType

      val params = function.parameters.filter { it.kind == KParameter.Kind.VALUE }

      params.forEach { param ->
        val kclass = param.type.asKClass()
        if (kclass !in allowedParamTypes) {
          val fqName = FunctionDetails(obj).fqName
          val simpleName = kclass.simpleName
          error(
            "Parameter \"${param.name}\" in $fqName is a $simpleName. Allowed types are String, Int, and Boolean",
          )
        }
      }

      params
        .forEach { param ->
          val name = param.name ?: error("Parameter name is null")
          if (!param.isOptional)
            functionDto.parameters.required += name
          functionDto.parameters.properties[name] = FunctionPropertyDescDto(
            type = param.llmType,
            description = param.paramAnnotation?.description ?: "The $name parameter",
          )
        }
    }
  }

  private val KParameter.llmType: String
    get() = when (type.asKClass()) {
      String::class -> "string"
      Int::class -> "integer"
      Double::class -> "double"
      Boolean::class -> "boolean"
      else -> "object"
    }

  class ToolCallInfo(
    private val assistantCacheId: AssistantCacheId,
    private val function: KFunction<*>,
  ) {
    private val toolCall get() = function.toolCallAnnotation!!
    private val toolHasName get() = toolCall.name.isNotEmpty()
    private val toolHasDescription get() = toolCall.description.isNotEmpty()
    private val cacheName get() = if (toolHasName) toolCall.name else function.name

    val llmName get() = "${cacheName}_$assistantCacheId"

    val llmDescription
      get() =
        when {
          toolHasDescription -> toolCall.description
          toolHasName -> toolCall.name
          else -> function.name
        }

    val llmReturnType
      get() = when (function.returnType.asKClass()) {
        String::class -> "string"
        Int::class -> "integer"
        Double::class -> "double"
        Boolean::class -> "boolean"
        else -> "object"
      }
  }
}
