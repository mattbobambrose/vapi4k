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

import com.vapi4k.api.assistant.ToolCall
import com.vapi4k.api.tools.enums.ToolType
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dtos.functions.FunctionDto
import com.vapi4k.dtos.functions.FunctionPropertyDescDto
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.functions
import com.vapi4k.utils.ReflectionUtils.hasToolCallAnnotation
import com.vapi4k.utils.ReflectionUtils.paramAnnotationWithDefault
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

internal object FunctionUtils {
  private val allowedParamTypes = setOf(String::class, Int::class, Double::class, Boolean::class)
  private val allowedReturnTypes = setOf(String::class, Unit::class)
  private val tcName by lazy { ToolCall::class.simpleName.orEmpty() }

  fun verifyIsToolCall(
    isTool: Boolean,
    function: KFunction<*>,
  ) {
    if (!function.hasToolCallAnnotation) {
      val str = if (isTool) "Tool" else "Function"
      error("$str ${function.name} is missing @$tcName annotation")
    }
  }

  fun verifyObjectHasOnlyOneToolCall(obj: Any) {
    val cnt = obj.functions.count { it.hasToolCallAnnotation }
    when {
      cnt == 0 -> error("No method with @$tcName annotation found in class ${obj::class.qualifiedName}")
      cnt > 1 -> error("Only one method with @$tcName annotation is allowed in class ${obj::class.qualifiedName}")
    }
  }

  fun verifyIsValidReturnType(
    isTool: Boolean,
    function: KFunction<*>,
  ) {
    val returnClass = function.returnType.asKClass()
    if (returnClass !in allowedReturnTypes) {
      val str = if (isTool) "Tool" else "Function"
      error("$str ${function.name} returns a ${returnClass.qualifiedName}. Allowed return types are String or Unit")
    }
  }

  fun populateFunctionDto(
    toolType: ToolType,
    model: AbstractModelProperties,
    obj: Any,
    function: KFunction<*>,
    functionDto: FunctionDto,
  ) {
    ToolCallInfo(model.assistantCacheId, function).also { toolCallInfo ->
      functionDto.name = toolCallInfo.llmName
      functionDto.description = toolCallInfo.llmDescription
      // TODO: This might always be object
      functionDto.parameters.type = "object" // llmReturnType

      val params = function.parameters.filter { it.kind == KParameter.Kind.VALUE }

      params.forEach { param ->
        val kclass = param.asKClass()
        if (kclass !in allowedParamTypes) {
          val fqName = FunctionDetails(toolType, obj, function).fqName
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
            description = param.paramAnnotationWithDefault,
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
}
