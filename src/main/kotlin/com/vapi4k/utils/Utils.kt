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
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.declaredFunctions

internal object Utils {
  fun String.ensureStartsWith(s: String) = if (startsWith(s)) this else s + this
  fun String.ensureEndsWith(s: String) = if (endsWith(s)) this else this + s
  fun String.trimLeadingSpaces() = lines().joinToString(separator = "\n") { it.trimStart() }

  fun <T> lambda(block: T) = block

  val KFunction<*>.isUnitReturnType get() = returnType.asKClass() == Unit::class
  val Any.functions get() = this::class.declaredFunctions
  val Any.methods get() = this::class.java.declaredMethods
  fun Any.findMethod(methodName: String) = methods.single { it.name == methodName }
  fun Any.findFunction(methodName: String) = functions.single { it.name == methodName }
  fun KType.asKClass() = classifier as KClass<*>

  val Parameter.param: Param? get() = annotations.firstOrNull { it is Param } as Param?
  val Method.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  val KFunction<*>.toolCall: ToolCall? get() = annotations.firstOrNull { it is ToolCall } as ToolCall?
  val Method.hasTool get() = toolCall != null
  val KFunction<*>.hasTool get() = toolCall != null
  val Any.toolFunction get() = functions.first { it.hasTool }
  val Any.toolMethod get() = methods.first { it.hasTool }
}
