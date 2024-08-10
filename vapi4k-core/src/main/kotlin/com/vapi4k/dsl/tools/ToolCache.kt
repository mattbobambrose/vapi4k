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
import com.vapi4k.dsl.functions.ToolCallInfo
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.common.Utils.isNull
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KFunction
import kotlin.time.Duration

internal class ToolCache(
  val nameBlock: () -> String,
) {
  private val cacheMap = ConcurrentHashMap<SessionCacheId, FunctionInfo>()
  private var lastCacheCleanInstant = Clock.System.now()

  val asDtoMap: Map<SessionCacheId, FunctionInfoDto>
    get() = cacheMap.map { (k, v) -> k to v.toFunctionInfoDto() }.toMap()

  val name get() = nameBlock()

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
    logger.debug { "Added to toolCache: $name [$sessionCacheId] [$cacheMap]" }

    if (funcDetails.isNull()) {
      val newFuncDetails = FunctionDetails(obj, function)
      funcInfo.functions[toolFuncName] = newFuncDetails
      logger.info { "Added \"$toolFuncName\" (${newFuncDetails.fqName}) to cache [$sessionCacheId]" }
    } else {
      error("\"$toolFuncName\" already declared in cache at ${funcDetails.fqName} [$sessionCacheId]")
    }
  }

  fun containsSessionCacheId(sessionCacheId: SessionCacheId) = cacheMap.containsKey(sessionCacheId)

  fun getFromCache(sessionCacheId: SessionCacheId): FunctionInfo =
    cacheMap[sessionCacheId] ?: error("Session cache id not found: $sessionCacheId")

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

  internal fun clearToolCache() {
    cacheMap.clear()
  }

  fun purgeToolCache(maxAge: Duration): Int {
    var count = 0
    lastCacheCleanInstant = Clock.System.now()
    cacheMap.entries.removeIf { (sessionCacheId, funcInfo) ->
      (funcInfo.age > maxAge).also { isOld ->
        if (isOld) {
          count++
          logger.debug { "Purging toolCall cache entry $sessionCacheId: $funcInfo" }
        }
      }
    }
    logger.debug { "Purged toolCall Cache ($count)" }
    return count
  }

  private fun swapKeys(
    oldSessionCacheId: SessionCacheId,
    newSessionCacheKey: SessionCacheId,
  ) {
    logger.info { "Swapping cache keys: $oldSessionCacheId -> $newSessionCacheKey" }
    cacheMap.remove(oldSessionCacheId)?.also { cacheMap[newSessionCacheKey] = it }
  }

  fun cacheAsJson() =
    CacheInfoDto(
      lastCacheCleanInstant.toString(),
      asDtoMap.size,
      asDtoMap,
    )

  fun swapCacheKeys(
    oldSessionCacheId: SessionCacheId,
    newSessionCacheKey: SessionCacheId,
  ) = swapKeys(oldSessionCacheId, newSessionCacheKey)
}

@Serializable
class CacheInfoDto(
  val lastPurgeTime: String = "",
  val toolCallCacheSize: Int = -1,
  val toolCallCache: Map<SessionCacheId, FunctionInfoDto>? = null,
)
