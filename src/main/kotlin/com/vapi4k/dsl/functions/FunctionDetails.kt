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

import com.vapi4k.dsl.toolservice.ToolRequestService
import com.vapi4k.dtos.tools.CommonToolMessageDto
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.findFunction
import com.vapi4k.utils.ReflectionUtils.findMethod
import com.vapi4k.utils.ReflectionUtils.kParameters
import com.vapi4k.utils.ReflectionUtils.parameterSignature
import com.vapi4k.utils.ReflectionUtils.toolFunction
import com.vapi4k.utils.ReflectionUtils.toolMethod
import com.vapi4k.utils.Utils.errorMsg
import com.vapi4k.utils.booleanValue
import com.vapi4k.utils.get
import com.vapi4k.utils.intValue
import com.vapi4k.utils.stringValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.util.concurrent.atomic.AtomicInteger

class FunctionDetails(
  val obj: Any,
) {
  val invokeCount = AtomicInteger(0)
  val className: String = obj::class.java.name
  val methodName: String = obj.toolMethod.name

  val fqName get() = "$className.$methodName()"
  val methodWithParams get() = "$methodName(${obj.toolFunction.parameterSignature})"

  fun invokeToolMethod(
    args: JsonElement,
    request: JsonElement,
    message: MutableList<CommonToolMessageDto> = mutableListOf(),
    successAction: (String) -> Unit,
    errorAction: (String) -> Unit,
  ) {
    runCatching {
      invokeCount.incrementAndGet()
      val result = invokeMethod(args).also { logger.info { "Tool call result: $it" } }
      successAction(result)
      if (obj is ToolRequestService)
        message.addAll(obj.onToolRequestComplete(request, result).map { it.dto }).also {
          logger.info { "Adding tool request messages $it" }
        }
    }.onFailure { e ->
      val errorMsg = "Error invoking method $fqName: ${e.errorMsg}"
      errorAction(errorMsg)
      if (obj is ToolRequestService)
        message.addAll(obj.onToolRequestFailed(request, errorMsg).map { it.dto })
      logger.error { errorMsg }
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
      fun FunctionDetails.toFunctionDetailsDto() =
        FunctionDetailsDto(fqName, className, methodWithParams, invokeCount.get())
    }
  }

  private fun invokeMethod(args: JsonElement): String {
    // logger.info { "Invoking method $fqName" }
    val method = obj.findMethod(methodName)
    val function = obj.findFunction(methodName)
    val isVoid = function.returnType.asKClass() == Unit::class
    val argNames = args.jsonObject.keys
    val vals = argNames.map { argName -> args[argName].stringValue }
    logger.info { "Invoking method $fqName with args $args and vals $vals" }
    val actualVals =
      function
        .kParameters
        .map { (argName, argType) ->
          val kclass = argType.asKClass()
          when (kclass) {
            String::class -> args.jsonObject.stringValue(argName)
            Int::class -> args.jsonObject.intValue(argName)
            Boolean::class -> args.jsonObject.booleanValue(argName)
            else -> error("Unsupported parameter type: $argType")
          }
        }

    logger.debug { "Actual vals: $actualVals" }
    val result = method.invoke(obj, *actualVals.toTypedArray<Any>())
    return if (isVoid) "" else result.toString()
  }
}
