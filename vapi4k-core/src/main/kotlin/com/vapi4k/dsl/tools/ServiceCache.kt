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

import com.vapi4k.common.AssistantId
import com.vapi4k.common.CacheKey
import com.vapi4k.common.CacheKey.Companion.cacheKeyValue
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.functions.FunctionDetails
import com.vapi4k.dsl.functions.FunctionInfo
import com.vapi4k.dsl.functions.FunctionInfoDto
import com.vapi4k.dsl.functions.ToolCallInfo
import com.vapi4k.dsl.model.AbstractModel
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.utils.common.Utils.ensureStartsWith
import com.vapi4k.utils.common.Utils.isNull
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KFunction
import kotlin.time.Duration

internal class ServiceCache(
  private val pathBlock: () -> String,
) {
  private var lastCacheCleanInstant = Clock.System.now()
  private val cacheMap = ConcurrentHashMap<CacheKey, FunctionInfo>()

  private val path get() = pathBlock().ensureStartsWith("/")

  fun isNotEmpty() = cacheMap.isNotEmpty()

  fun entriesForSessionId(sessionId: SessionId) =
    cacheMap.entries
      .filter { it.key.value.startsWith(sessionId.value) }
      .map { it.value }

  fun addToCache(
    model: AbstractModel,
    obj: Any,
    function: KFunction<*>,
  ) {
    val sessionId = model.sessionId
    val assistantId = model.assistantId
    val cacheKey = cacheKeyValue(sessionId, assistantId)
    val toolCallInfo = ToolCallInfo(assistantId, function)
    val toolFuncName = toolCallInfo.llmName
    val funcInfo = cacheMap.computeIfAbsent(cacheKey) { FunctionInfo(sessionId, assistantId) }
    val funcDetails = funcInfo.getFunctionOrNull(toolFuncName)

    if (funcDetails.isNull()) {
      val newFuncDetails = FunctionDetails(obj, function, toolCallInfo)
      funcInfo.addFunction(toolFuncName, newFuncDetails)
      logger.info { "Added \"$toolFuncName\" (${newFuncDetails.fqName}) to $path serviceTool cache [$cacheKey]" }
    } else {
      error("\"$toolFuncName\" already declared in cache at ${funcDetails.fqName} [$cacheKey]")
    }
  }

  fun containsIds(
    sessionId: SessionId,
    assistantId: AssistantId,
  ) = cacheMap.containsKey(cacheKeyValue(sessionId, assistantId))

  fun getFromCache(
    sessionId: SessionId,
    assistantId: AssistantId,
  ): FunctionInfo =
    cacheKeyValue(sessionId, assistantId).let { key -> cacheMap[key] ?: error("Session cache id not found: $key") }

  fun removeFromCache(
    sessionId: SessionId,
    assistantId: AssistantId,
    block: (FunctionInfo) -> Unit,
  ): FunctionInfo? =
    cacheKeyValue(sessionId, assistantId).let { key ->
      cacheMap.remove(key)
        ?.also { block(it) }
        .also {
          if (it.isNull())
            logger.debug { "Entry not found in serviceTool cache: $key" }
        }
    }

  fun clearToolCache() {
    cacheMap.clear()
  }

  fun purgeToolCache(maxAge: Duration): Int {
    var count = 0
    lastCacheCleanInstant = Clock.System.now()
    cacheMap.entries.removeIf { (sessionId, funcInfo) ->
      (funcInfo.age > maxAge).also { isOld ->
        if (isOld) {
          count++
          logger.debug { "Purging serviceTool cache entry $sessionId: $funcInfo" }
        }
      }
    }
    logger.debug { "Purged serviceTool cache ($count)" }
    return count
  }

  private val asDtoMap: Map<CacheKey, FunctionInfoDto>
    get() = cacheMap.map { (k, v) -> k to v.toFunctionInfoDto() }.toMap()

  fun cacheAsJson() = CacheInfoDto(lastCacheCleanInstant.toString(), asDtoMap.size, asDtoMap)
}

@Serializable
class CacheInfoDto(
  val lastPurgeTime: String = "",
  val toolCallCacheSize: Int = -1,
  val toolCallCache: Map<CacheKey, FunctionInfoDto>? = null,
)
