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

import com.vapi4k.AssistantDslMarker
import com.vapi4k.common.Constants.NAME_SEPARATOR
import com.vapi4k.dsl.assistant.AssistantDsl.populateFunctionDto
import com.vapi4k.dsl.assistant.AssistantDsl.verifyObject
import com.vapi4k.responses.assistant.AssistantRequestMessageResponse
import com.vapi4k.responses.assistant.FunctionDto
import com.vapi4k.responses.assistant.FunctionDto.FunctionParameters.FunctionPropertyDesc
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions

object AssistantDsl {
  private val Parameter.param: Param? get() = annotations.firstOrNull { it is Param } as Param?
  private val Method.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  private val KFunction<*>.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  private val Method.hasTool get() = toolCall != null
  private val KFunction<*>.hasTool get() = toolCall != null
  internal val KFunction<*>.isAsync get() = returnType.classifier as KClass<*> == Unit::class

  fun assistant(block: Assistant.() -> Unit) =
    AssistantRequestMessageResponse().apply {
      Assistant(messageResponse.assistant).apply(block)
    }.messageResponse

  fun assistantId(id: String) =
    AssistantRequestMessageResponse()
      .apply { messageResponse.assistantId = id }.messageResponse

  private val legalTypes = setOf(String::class, Unit::class)

  internal fun verifyObject(
    isFunction: Boolean,
    obj: Any,
  ): KFunction<*> {
    val constructors = obj::class.java.constructors
    if (constructors.size != 1) {
      error("Only one constructor is allowed. Found ${constructors.size}")
    } else if (constructors.first().parameterCount != 0) {
      error("Constructor must have no parameters")
    }

    val methods = obj::class.declaredFunctions
    val cnt = methods.count { it.hasTool }

    when {
      cnt == 0 ->
        error("No method with ${ToolCall::class.simpleName} annotation found in class ${obj::class.qualifiedName}")

      cnt > 1 ->
        error("Only one method with ${ToolCall::class.simpleName} annotation is allowed in class ${obj::class.qualifiedName}")
    }

    return with(methods.first { it.hasTool }) {
      val returnClass = returnType.classifier as KClass<*>
      if (returnClass in legalTypes) this
      else {
        val str = if (isFunction) "Function" else "Tool"
        error("$str $name must return a String or Unit, but instead returns ${returnClass.qualifiedName}")
      }
    }
  }

  internal fun populateFunctionDto(
    obj: Any,
    function: FunctionDto = FunctionDto(),
  ) =
    function.apply {
      val method = obj::class.java.declaredMethods.first { it.hasTool }
      val kFunc = obj::class.declaredFunctions.first { it.hasTool }
      with(ToolAssist(method.toolCall!!, method)) {
        with(function) {
          name = "${obj::class.java.name}$NAME_SEPARATOR$llmName"
          description = llmDescription
          // TODO: This might be always object
          parameters.type = "object"//llmReturnType

          parameters.properties = mutableMapOf()

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
                parameters.required += name
              parameters.properties!![name] = FunctionPropertyDesc(
                type = jParam.llmType,
                description = jParam.param?.description ?: "The $name parameter"
              )
            }
        }
      }
    }

  private class ToolAssist(
    val toolCall: ToolCall,
    val method: Method,
  ) {
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

  private val Parameter.llmType: String
    get() = when (type) {
      String::class.java -> "string"
      Int::class.java -> "integer"
      Boolean::class.java -> "boolean"
      else -> "object"
    }
}

@Retention(RUNTIME)
@Target(AnnotationTarget.FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class ToolCall(
  val description: String = "",
  val name: String = "",
)

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
annotation class Param(val description: String)

@AssistantDslMarker
data class Functions internal constructor(val model: Model) {
  fun function(obj: Any) {
    model.modelDto.functions += FunctionDto().apply {
      verifyObject(true, obj)
      populateFunctionDto(obj, this)
    }.also { func ->
      if (model.modelDto.functions.any { func.name == it.name }) {
        error("Duplicate function name declared: ${func.name}")
      }
    }
  }
}
