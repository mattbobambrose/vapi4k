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

import com.vapi4k.responses.assistant.FunctionDto
import com.vapi4k.utils.Utils.asKClass
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions

internal object FunctionUtils {
  private val Parameter.param: Param? get() = annotations.firstOrNull { it is Param } as Param?
  private val Method.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  internal val KFunction<*>.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  private val Method.hasTool get() = toolCall != null
  internal val KFunction<*>.hasTool get() = toolCall != null
  internal val KFunction<*>.isAsync get() = returnType.asKClass() == Unit::class
  internal val Any.toolKFunction get() = this::class.declaredFunctions.first { it.hasTool }
  internal val Any.toolMethod get() = this::class.java.declaredMethods.first { it.hasTool }

  private val legalTypes = setOf(String::class, Unit::class)


  internal fun verifyObject(
    isFunction: Boolean,
    obj: Any,
  ) {
    val methods = obj::class.declaredFunctions
    val cnt = methods.count { it.hasTool }

    when {
      cnt == 0 ->
        error("No method with ${ToolCall::class.simpleName} annotation found in class ${obj::class.qualifiedName}")

      cnt > 1 ->
        error("Only one method with ${ToolCall::class.simpleName} annotation is allowed in class ${obj::class.qualifiedName}")
    }

    return with(methods.first { it.hasTool }) {
      val returnClass = returnType.asKClass()
      if (returnClass !in legalTypes) {
        val str = if (isFunction) "Function" else "Tool"
        error("$str $name must return a String or Unit, but instead returns ${returnClass.qualifiedName}")
      }
    }
  }

  internal fun populateFunctionDto(
    obj: Any,
    function: FunctionDto = FunctionDto(),
  ) =
    function.also { dto ->
      val method = obj::class.java.declaredMethods.first { it.hasTool }
      val kFunc = obj::class.declaredFunctions.first { it.hasTool }
      ToolCallInfo(method).also { tci ->

        dto.name = tci.llmName
        dto.description = tci.llmDescription
        // TODO: This might be always object
        dto.parameters.type = "object"  // llmReturnType
        dto.parameters.properties = mutableMapOf()

        // Reduce the size of kparams if the first parameter is the object itself
        val jparams = method.parameters.toList()
        val kparams =
          if (jparams.size == kFunc.parameters.size)
            kFunc.parameters
          else
            kFunc.parameters.subList(1, kFunc.parameters.size)

        jparams
          .zip(kparams)
          .forEach { (jParam, kParam) ->
            val name = kParam.name ?: jParam.name
            if (!kParam.isOptional)
              dto.parameters.required += name
            dto.parameters.properties!![name] = FunctionDto.FunctionParameters.FunctionPropertyDesc(
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

  internal class ToolCallInfo(val method: Method) {
    private val toolCall get() = method.toolCall!!
    private val toolHasName: Boolean get() = toolCall.name.isNotEmpty()
    private val toolHasDescription: Boolean get() = toolCall.description.isNotEmpty()

    val llmName: String
      get() = if (toolHasName) toolCall.name else method.name

    val llmDescription: String
      get() =
        when {
          toolHasDescription -> toolCall.description
          toolHasName -> toolCall.name
          else -> method.name
        }

    val llmReturnType: String
      get() = when (method.returnType) {
        String::class.java -> "string"
        Int::class.java -> "integer"
        Boolean::class.java -> "boolean"
        else -> "object"
      }
  }
}
