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

import com.vapi4k.dsl.toolservice.ToolCallService
import com.vapi4k.dtos.tools.CommonToolMessageDto
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.MiscUtils.findFunction
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.instanceParameter
import com.vapi4k.utils.ReflectionUtils.isUnitReturnType
import com.vapi4k.utils.ReflectionUtils.parameterSignature
import com.vapi4k.utils.ReflectionUtils.toolCallAnnotation
import com.vapi4k.utils.ReflectionUtils.valueParameters
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.json.JsonElementUtils.booleanValue
import com.vapi4k.utils.json.JsonElementUtils.doubleValue
import com.vapi4k.utils.json.JsonElementUtils.intValue
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspendBy

class FunctionDetails internal constructor(
  val obj: Any,
  val function: KFunction<*>,
  val toolCallInfo: ToolCallInfo,
) {
  private val invokeCounter = AtomicInteger(0)
  val className: String = obj::class.qualifiedName.orEmpty()
  val functionName: String = function.name
  val toolCall = function.toolCallAnnotation

  val invokeCount get() = invokeCounter.get()
  val fqName get() = "$className.$functionName()"
  val fqNameWithParams get() = "$className.$functionName(${function.parameterSignature})"
  val methodWithParams get() = "$functionName(${function.parameterSignature})"
  val params get() = function.valueParameters

  override fun toString() = fqNameWithParams

  suspend fun invokeToolMethod(
    isTool: Boolean,
    request: JsonElement,
    args: JsonElement,
    messageDtos: MutableList<CommonToolMessageDto> = mutableListOf(),
    successAction: (String) -> Unit,
    errorAction: (String) -> Unit,
  ) {
    runCatching {
      invokeCounter.incrementAndGet()
      val result = invokeMethod(request, args).also { logger.info { "Tool call result: $it" } }
      successAction(result)
      if (isTool && obj is ToolCallService)
        messageDtos.addAll(obj.onToolCallComplete(request, result).map { it.dto })
          .also { logger.debug { "Adding onToolCallComplete messages $it" } }
    }.onFailure { e ->
      val eMsg = if (e is InvocationTargetException) e.cause?.errorMsg ?: e.errorMsg else e.errorMsg
      val errorMsg = "Error invoking method $fqName: $eMsg"
      errorAction(errorMsg)
      if (isTool && obj is ToolCallService)
        messageDtos.addAll(obj.onToolCallFailed(request, errorMsg).map { it.dto })
          .also { logger.debug { "Adding onToolCallFailed messages $it" } }
      logger.info { errorMsg }
    }
  }

  @Serializable
  data class FunctionDetailsDto(
    val fqName: String = "",
    val className: String = "",
    val method: String = "",
    val invokeCount: Int = -1,
  ) {
    companion object {
      fun FunctionDetails.toFunctionDetailsDto() = FunctionDetailsDto(fqName, className, methodWithParams, invokeCount)
    }
  }

  private fun getArgValue(
    args: JsonElement,
    argName: String,
    argType: KType,
  ) = when (argType.asKClass()) {
    String::class -> args.stringValue(argName)
    Int::class -> args.intValue(argName)
    Double::class -> args.doubleValue(argName)
    Boolean::class -> args.booleanValue(argName)
    else -> error("Unsupported parameter type: $argType")
  }

  private suspend fun invokeMethod(
    request: JsonElement,
    args: JsonElement,
  ): String {
    val function = obj.findFunction(functionName)
    val argNames = args.keys
    logger.info { "Invoking method $fqName with args $argNames" }
    val paramMap = function.valueParameters.toMap()
    val valueMap =
      argNames.associate { argName ->
        val param = paramMap[argName] ?: error("Parameter $argName not found in method $fqName")
        param to getArgValue(args, argName, param.type)
      }

    // Check if the function has a request JsonElement parameter
    val requestParam = function.valueParameters.firstOrNull { it.second.asKClass() == JsonElement::class }?.second

    // If the function has a request parameter, add it to the valueMap
    val valueMapWithRequest =
      requestParam?.let { param -> valueMap.toMutableMap().also { it[param] = request } } ?: valueMap

    val callMap =
      function.instanceParameter?.let { param ->
        valueMapWithRequest.toMutableMap().also { it[param] = obj }
      } ?: valueMapWithRequest

    val args = if (valueMapWithRequest.isEmpty()) {
      "with no args"
    } else {
      valueMapWithRequest
        .mapNotNull { (param, value) ->
          when (param.type.asKClass()) {
            JsonElement::class -> "${param.name}: Request Value"
            String::class -> "${param.name}: \"$value\""
            Int::class -> "${param.name}: $value"
            Double::class -> "${param.name}: $value"
            Boolean::class -> "${param.name}: $value"
            else -> null
          }
        }.joinToString(", ")
    }
    logger.info { "Calling \"${toolCall?.description.orEmpty()}\" tool service: ${functionName}($args)" }

    // Invoke the function with the arguments
    val result =
      if (function.isSuspend)
        function.callSuspendBy(callMap)
      else
        function.callBy(callMap)
    return if (function.isUnitReturnType) "" else result?.toString().orEmpty()
  }
}
