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
import com.vapi4k.dsl.functions.FunctionDetails
import com.vapi4k.dsl.functions.FunctionInfo
import com.vapi4k.dsl.functions.FunctionInfoDto
import com.vapi4k.dsl.functions.FunctionInfoDto.Companion.toFunctionInfoDto
import com.vapi4k.dsl.functions.FunctionUtils.ToolCallInfo
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.Utils.isNull
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KFunction

internal class ToolCache {
  //  @Serializable(with = CacheMapSerializer::class)
  private val cacheMap = ConcurrentHashMap<SessionCacheId, FunctionInfo>()
  var cacheIsActive = false

  val asDtoMap: Map<SessionCacheId, FunctionInfoDto>
    get() = cacheMap.map { (k, v) -> k to v.toFunctionInfoDto() }.toMap()

  fun getFromCache(sessionCacheId: SessionCacheId): FunctionInfo =
    cacheMap[sessionCacheId] ?: error("Session cache id not found: $sessionCacheId")

  fun addToCache(
    sessionCacheId: SessionCacheId,
    assistantCacheId: AssistantCacheId,
    obj: Any,
    function: KFunction<*>,
  ) {
    val toolCallInfo = ToolCallInfo(assistantCacheId, function)
    val toolFuncName = toolCallInfo.llmName
    val funcInfo = cacheMap.computeIfAbsent(sessionCacheId) { FunctionInfo() }
    val funcDetails = funcInfo.functions[toolFuncName]

    if (funcDetails.isNull()) {
      val newFuncDetails = FunctionDetails(obj, function)
      funcInfo.functions[toolFuncName] = newFuncDetails
      logger.info { "Added \"$toolFuncName\" (${newFuncDetails.fqName}) to cache [$sessionCacheId]" }
    } else {
      error("\"$toolFuncName\" already declared in cache at ${funcDetails.fqName} [$sessionCacheId]")
    }
  }

  fun removeFromCache(
    sessionCacheId: SessionCacheId,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? =
    cacheMap.remove(sessionCacheId)
      ?.also { block(it) }
      .also {
        if (it.isNull())
          logger.debug { "Entry not found in cache: $sessionCacheId" }
      }

  private fun clearCache() {
    cacheMap.clear()
    cacheIsActive = false
  }

  private fun swapKeys(
    oldSessionCacheId: SessionCacheId,
    newSessionCacheKey: SessionCacheId,
  ) {
    logger.info { "Swapping cache keys: $oldSessionCacheId -> $newSessionCacheKey" }
    cacheMap.remove(oldSessionCacheId)?.also { cacheMap[newSessionCacheKey] = it }
  }

  companion object {
    val toolCallCache = ToolCache()

    fun clearCache() {
      toolCallCache.clearCache()
    }

    fun swapCacheKeys(
      oldSessionCacheId: SessionCacheId,
      newSessionCacheKey: SessionCacheId,
    ) {
      toolCallCache.swapKeys(oldSessionCacheId, newSessionCacheKey)
    }

    fun cacheAsJson() = CacheInfoDto(toolCallCache.asDtoMap)
  }
}

@Serializable
class CacheInfoDto(
  val toolCallCache: Map<SessionCacheId, FunctionInfoDto>? = null,
)
