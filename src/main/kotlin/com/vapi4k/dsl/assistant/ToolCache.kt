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

import com.vapi4k.Vapi4k.logger
import com.vapi4k.dsl.assistant.FunctionUtils.ToolCallInfo
import com.vapi4k.dsl.assistant.FunctionUtils.toolMethod
import com.vapi4k.dsl.assistant.ToolCache.FunctionDetails.Companion.toFunctionDetails
import com.vapi4k.responses.ToolCallMessage
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import com.vapi4k.utils.Utils.asKClass
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.declaredFunctions
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.DurationUnit.SECONDS

internal object ToolCache {
  val toolCallCache = ConcurrentHashMap<String, FunctionInfo>()
  val functionCache = ConcurrentHashMap<String, FunctionInfo>()

  fun addToolCallToCache(
    phoneNumber: String,
    obj: Any,
  ) = addToCache(toolCallCache, "Tool", phoneNumber, obj)

  fun addFunctionToCache(
    phoneNumber: String,
    obj: Any,
  ) = addToCache(functionCache, "Function", phoneNumber, obj)

  fun removeToolCallFromCache(
    messageCallId: String,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? =
    toolCallCache.remove(messageCallId)?.also { it -> block(it) }

  fun removeFunctionFromCache(
    messageCallId: String,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? =
    functionCache.remove(messageCallId)?.also { it -> block(it) }


  private fun addToCache(
    cache: ConcurrentHashMap<String, FunctionInfo>,
    prefix: String,
    phoneNumber: String,
    obj: Any,
  ) {
    val method = obj.toolMethod
    val tci = ToolCallInfo(method)
    val toolFuncName = tci.llmName
    val functionInfo = cache.computeIfAbsent(phoneNumber) { FunctionInfo() }
    val functionDetails = functionInfo.functions[toolFuncName]

    if (functionDetails == null)
      functionInfo.functions[toolFuncName] = obj.toFunctionDetails()
    else
      with(functionDetails) { error("$prefix \"$toolFuncName\" has already been declared in $className.$methodName()") }
  }

  class FunctionInfo() {
    val created: Instant = Clock.System.now()
    val functions = mutableMapOf<String, FunctionDetails>()
    val age get() = Clock.System.now() - created
    val ageSecs get() = age.toString(unit = SECONDS)
    val ageMillis get() = age.toString(unit = MILLISECONDS)

    fun getFunction(funcName: String) = functions[funcName] ?: error("Function not found: \"$funcName\"")
  }

  class FunctionDetails(val obj: Any) {
    val className = obj::class.java.name
    val methodName = obj.toolMethod.name

    fun invokeToolMethod(
      args: JsonElement,
      request: JsonElement,
      message: MutableList<ToolCallMessage> = mutableListOf(),
      errorAction: (String) -> Unit = {},
    ): String {
      val results = runCatching {
        invokeMethod(args)
      }.getOrElse { e ->
        val errorMsg = "Error invoking method $className.$methodName(): ${e.message}"
        errorAction(errorMsg)
        if (obj is ToolCallService)
          message += obj.onRequestFailed(request, errorMsg).messages
        error(errorMsg)
      }

      if (obj is ToolCallService)
        message += obj.onRequestComplete(request, results).messages

      return results
    }

    fun invokeMethod(args: JsonElement): String {
      logger.info { "Invoking method \"$methodName()\" with methods: ${obj::class.java.declaredMethods.map { it.name }}" }
      val method = obj::class.java.declaredMethods.single { it.name == methodName }
      val argNames = args.jsonObject.keys
      val result = method.invoke(obj, *argNames.map { args[it].stringValue }.toTypedArray<String>())

      val kFunction = obj::class.declaredFunctions.single { it.name == methodName }
      val isVoid = kFunction.returnType.asKClass() == Unit::class
      return if (isVoid) "" else result.toString()
    }

    companion object {
      fun Any.toFunctionDetails(): FunctionDetails = FunctionDetails(this)
    }
  }
}
