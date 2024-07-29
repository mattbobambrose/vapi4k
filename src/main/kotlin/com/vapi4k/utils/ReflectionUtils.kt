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

package com.vapi4k.utils

import com.vapi4k.dsl.assistant.Param
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.utils.Utils.isNotNull
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.declaredFunctions

internal object ReflectionUtils {
  val Parameter.param: Param? get() = annotations.firstOrNull { it is Param } as Param?
  val Method.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  val KFunction<*>.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  val Method.hasTool get() = toolCall.isNotNull()
  val KFunction<*>.hasTool get() = toolCall.isNotNull()
  val Any.toolFunction get() = functions.first { it.hasTool }
  val Any.toolMethod get() = methods.first { it.hasTool }
  val KFunction<*>.isUnitReturnType get() = returnType.asKClass() == Unit::class
  val Any.functions get() = this::class.declaredFunctions
  val Any.methods get() = this::class.java.declaredMethods
  val KFunction<*>.kParameters: List<Pair<String, KType>>
    get() = parameters
      .map { it.name to it.type }
      .filter { (name, _) -> name.isNotNull() }
      .map { (name, type) -> name!! to type }
  val KFunction<*>.parameterSignature
    get() =
      kParameters.map { (name, type) ->
        "$name: ${type.asKClass().simpleName}"
      }.joinToString(", ")


  fun String.ensureStartsWith(s: String) = if (startsWith(s)) this else s + this

  fun String.ensureEndsWith(s: String) = if (endsWith(s)) this else this + s

  fun String.trimLeadingSpaces() = lines().joinToString(separator = "\n") { it.trimStart() }

  fun <T> lambda(block: T) = block

  fun Any.findMethod(methodName: String) =
    methods.singleOrNull { it.name == methodName } ?: error("Method $methodName not found")

  fun Any.findFunction(methodName: String) =
    functions.singleOrNull { it.name == methodName } ?: error("Method $methodName not found")

  fun KType.asKClass() = classifier as KClass<*>
}
