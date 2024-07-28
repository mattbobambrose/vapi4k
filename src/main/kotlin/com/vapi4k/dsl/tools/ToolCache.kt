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

package com.vapi4k.dsl.tools

import com.vapi4k.common.AssistantCacheId
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.tools.FunctionUtils.ToolCallInfo
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.ReflectionUtils.toolMethod
import java.util.concurrent.ConcurrentHashMap

internal object ToolCache {
  private val toolCallCache = ConcurrentHashMap<SessionCacheId, FunctionInfo>()
  private val functionCache = ConcurrentHashMap<SessionCacheId, FunctionInfo>()

  private var toolCallCacheIsActive = false
  private var functionCacheIsActive = false

  val cacheIsActive get() = toolCallCacheIsActive || functionCacheIsActive

  fun getToolCallFromCache(sessionCacheId: SessionCacheId): FunctionInfo =
    toolCallCache[sessionCacheId] ?: error("Tool session cache id not found: $sessionCacheId")

  fun getFunctionFromCache(sessionCacheId: SessionCacheId): FunctionInfo =
    functionCache[sessionCacheId] ?: error("Function session cache id key not found: $sessionCacheId")

  fun resetCaches() {
    toolCallCache.clear()
    functionCache.clear()
    toolCallCacheIsActive = false
    functionCacheIsActive = false
  }

  fun addToolCallToCache(
    sessionCacheId: SessionCacheId,
    assistantCacheId: AssistantCacheId,
    obj: Any,
  ) {
    toolCallCacheIsActive = true
    addToCache(toolCallCache, "Tool", sessionCacheId, assistantCacheId, obj)
  }

  fun addFunctionToCache(
    sessionCacheId: SessionCacheId,
    assistantCacheId: AssistantCacheId,
    obj: Any,
  ) {
    functionCacheIsActive = true
    addToCache(functionCache, "Function", sessionCacheId, assistantCacheId, obj)
  }

  private fun addToCache(
    cache: ConcurrentHashMap<SessionCacheId, FunctionInfo>,
    prefix: String,
    sessionCacheId: SessionCacheId,
    assistantCacheId: AssistantCacheId,
    obj: Any,
  ) {
    val method = obj.toolMethod
    val toolCallInfo = ToolCallInfo(assistantCacheId, method)
    val toolFuncName = toolCallInfo.llmName

    val funcInfo = cache.computeIfAbsent(sessionCacheId) { FunctionInfo() }
    val funcDetails = funcInfo.functions[toolFuncName]

    if (funcDetails == null) {
      val newFuncDetails = FunctionDetails(obj)
      funcInfo.functions[toolFuncName] = newFuncDetails
      logger.info { "Added $prefix \"$toolFuncName\" (${newFuncDetails.fqName}) to cache [$sessionCacheId]" }
    } else {
      error("$prefix \"$toolFuncName\" has already been declared in ${funcDetails.fqName} [$sessionCacheId]")
    }
  }

  fun removeToolCallFromCache(
    sessionCacheId: SessionCacheId,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? =
    toolCallCache.remove(sessionCacheId)
      ?.also { block(it) }
      .also {
        if (it == null)
          logger.debug { "Tool entry not found in cache: $sessionCacheId" }
      }

  fun removeFunctionFromCache(
    sessionCacheId: SessionCacheId,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? =
    functionCache.remove(sessionCacheId)
      ?.also { block(it) }
      .also { funcInfo ->
        if (funcInfo == null)
          logger.debug { "Function entry not found in cache: $sessionCacheId" }
      }

  fun swapCacheKeys(
    oldSessionCacheId: SessionCacheId,
    newSessionCacheKey: SessionCacheId,
  ) {
    logger.info { "Swapping cache keys: $oldSessionCacheId -> $newSessionCacheKey" }
    toolCallCache.remove(oldSessionCacheId)?.also { toolCallCache[newSessionCacheKey] = it }
    functionCache.remove(oldSessionCacheId)?.also { functionCache[newSessionCacheKey] = it }
  }
}
