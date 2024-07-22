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

package com.vapi4k.dsl.assistant.tools

import com.vapi4k.dsl.assistant.tools.FunctionUtils.ToolCallInfo
import com.vapi4k.dsl.assistant.tools.ToolCache.FunctionDetails.Companion.toFunctionDetails
import com.vapi4k.plugin.Vapi4kLogger.logger
import com.vapi4k.responses.ToolCallMessageDto
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.findFunction
import com.vapi4k.utils.ReflectionUtils.findMethod
import com.vapi4k.utils.ReflectionUtils.toolMethod
import com.vapi4k.utils.Utils.errorMsg
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.DurationUnit.SECONDS

internal object ToolCache {
  val toolCallCache = ConcurrentHashMap<String, FunctionInfo>()
  val functionCache = ConcurrentHashMap<String, FunctionInfo>()

  private var toolCallCacheIsActive = false
  private var functionCacheIsActive = false

  val cacheIsActive get() = toolCallCacheIsActive || functionCacheIsActive

  fun resetCaches() {
    toolCallCache.clear()
    functionCache.clear()
    toolCallCacheIsActive = false
    functionCacheIsActive = false
  }

  fun addToolCallToCache(
    messageCallId: String,
    obj: Any,
  ) {
    toolCallCacheIsActive = true
    addToCache(toolCallCache, "Tool", messageCallId, obj)
  }

  fun addFunctionToCache(
    messageCallId: String,
    obj: Any,
  ) {
    functionCacheIsActive = true
    addToCache(functionCache, "Function", messageCallId, obj)
  }

  fun removeToolCallFromCache(
    messageCallId: String,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? = toolCallCache.remove(messageCallId)?.also { block(it) }

  fun removeFunctionFromCache(
    messageCallId: String,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? = functionCache.remove(messageCallId)?.also { block(it) }


  private fun addToCache(
    cache: ConcurrentHashMap<String, FunctionInfo>,
    prefix: String,
    messageCallId: String,
    obj: Any,
  ) {
    val method = obj.toolMethod
    val tci = ToolCallInfo(method)
    val toolFuncName = tci.llmName
    val funcInfo = cache.computeIfAbsent(messageCallId) { FunctionInfo() }
    val funcDetails = funcInfo.functions[toolFuncName]

    if (funcDetails == null) {
      val newFuncDetails = obj.toFunctionDetails()
      funcInfo.functions[toolFuncName] = newFuncDetails
      logger.info { "Added $prefix \"$toolFuncName\" (${newFuncDetails.fqName}) to cache [$messageCallId]" }
    } else {
      error("$prefix \"$toolFuncName\" has already been declared in ${funcDetails.fqName}")
    }
  }

  internal class FunctionInfo {
    val created: Instant = Clock.System.now()
    val functions = mutableMapOf<String, FunctionDetails>()
    val age get() = Clock.System.now() - created
    val ageSecs get() = age.toString(unit = SECONDS)
    val ageMillis get() = age.toString(unit = MILLISECONDS)

    fun getFunction(funcName: String) = functions[funcName] ?: error("Function not found: \"$funcName\"")
  }

  internal class FunctionDetails(val obj: Any) {
    val className = obj::class.java.name
    val methodName = obj.toolMethod.name
    val fqName get() = "$className.$methodName()"

    fun invokeToolMethod(
      args: JsonElement,
      request: JsonElement,
      message: MutableList<ToolCallMessageDto> = mutableListOf(),
      errorAction: (String) -> Unit = {},
    ): String {
      val results =
        runCatching {
          invokeMethod(args)
        }.getOrElse { e ->
          val errorMsg = "Error invoking method $fqName: ${e.errorMsg}"
          errorAction(errorMsg)
          if (obj is ToolCallService)
            message += obj.onRequestFailed(request, errorMsg).messages
          error(errorMsg)
        }

      if (obj is ToolCallService)
        message += obj.onRequestComplete(request, results).messages

      return results
    }

    private fun invokeMethod(args: JsonElement): String {
      logger.info { "Invoking method $fqName" }
      val method = obj.findMethod(methodName)
      val function = obj.findFunction(methodName)
      val isVoid = function.returnType.asKClass() == Unit::class
      val argNames = args.jsonObject.keys
      val vals = argNames.map { args[it].stringValue }
      // TODO Fix ordering
      logger.info { "Invoking method $fqName with args $args" }
      logger.info { "Invoking method $fqName with vals $vals" }
      val result = method.invoke(obj, *vals.toTypedArray<String>())
      return if (isVoid) "" else result.toString()
    }

    companion object {
      fun Any.toFunctionDetails(): FunctionDetails = FunctionDetails(this)
    }
  }
}
