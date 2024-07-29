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
import com.vapi4k.utils.Utils.isNull
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap


//private object CacheMapSerializer : KSerializer<ConcurrentHashMap<SessionCacheId, FunctionInfo>> {
//  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CommonDestinationDto")
//
//  override fun serialize(
//    encoder: Encoder,
//    value: ConcurrentHashMap<SessionCacheId, FunctionInfo>,
//  ) {
//    encoder.encodeStructure(descriptor) {
//      val composite = encoder.beginStructure(descriptor)
//
//      value.forEach { (key, value) ->
//        encodeStringElement(descriptor, 1,
//          encoder.encodeSerializableValue(FunctionInfo.serializer(), value))
//      }
//    }
//    when (value) {
//
//      is NumberDestinationDto -> encoder.encodeSerializableValue(NumberDestinationDto.serializer(), value)
//      is SipDestinationDto -> encoder.encodeSerializableValue(SipDestinationDto.serializer(), value)
//      else -> error("Invalid destination provider: ${value::class.simpleName}")
//    }
//  }
//
//  override fun deserialize(decoder: Decoder): CommonDestinationDto =
//    throw NotImplementedError("Deserialization is not supported")
//}

internal class ToolCache(
  val cacheType: String,
) {
  //  @Serializable(with = CacheMapSerializer::class)
  private val cacheMap = ConcurrentHashMap<SessionCacheId, FunctionInfo>()
  var cacheIsActive = false

  val values get() = cacheMap.values

  fun getFromCache(sessionCacheId: SessionCacheId): FunctionInfo =
    cacheMap[sessionCacheId] ?: error("$cacheType session cache id not found: $sessionCacheId")

  fun addToCache(
    sessionCacheId: SessionCacheId,
    assistantCacheId: AssistantCacheId,
    obj: Any,
  ) {
    val method = obj.toolMethod
    val toolCallInfo = ToolCallInfo(assistantCacheId, method)
    val toolFuncName = toolCallInfo.llmName
    val funcInfo = cacheMap.computeIfAbsent(sessionCacheId) { FunctionInfo() }
    val funcDetails = funcInfo.functions[toolFuncName]

    if (funcDetails.isNull()) {
      val newFuncDetails = FunctionDetails(obj)
      funcInfo.functions[toolFuncName] = newFuncDetails
      logger.info { "Added $cacheType \"$toolFuncName\" (${newFuncDetails.fqName}) to cache [$sessionCacheId]" }
    } else {
      error("$cacheType \"$toolFuncName\" has already been declared in ${funcDetails.fqName} [$sessionCacheId]")
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
          logger.debug { "$cacheType entry not found in cache: $sessionCacheId" }
      }

  private fun resetCache() {
    cacheMap.clear()
    cacheIsActive = false
  }

  private fun swapKeys(
    oldSessionCacheId: SessionCacheId,
    newSessionCacheKey: SessionCacheId,
  ) {
    logger.info { "Swapping $cacheType cache keys: $oldSessionCacheId -> $newSessionCacheKey" }
    cacheMap.remove(oldSessionCacheId)?.also { cacheMap[newSessionCacheKey] = it }
  }

  companion object {
    val toolCallCache = ToolCache("Tool")
    val functionCache = ToolCache("Function")

    val cachesAreActive get() = toolCallCache.cacheIsActive || functionCache.cacheIsActive

    fun resetCaches() {
      toolCallCache.resetCache()
      functionCache.resetCache()
    }

    fun swapCacheKeys(
      oldSessionCacheId: SessionCacheId,
      newSessionCacheKey: SessionCacheId,
    ) {
      toolCallCache.swapKeys(oldSessionCacheId, newSessionCacheKey)
      functionCache.swapKeys(oldSessionCacheId, newSessionCacheKey)
    }

    fun cacheAsJson() = CacheInfoDto()
  }
}

@Serializable
class CacheInfoDto(
  @EncodeDefault
  val toolCallCache: List<FunctionInfoDto> = ToolCache.toolCallCache.values.map { FunctionInfoDto(it) },
  @EncodeDefault
  val functionCache: List<FunctionInfoDto> = ToolCache.functionCache.values.map { FunctionInfoDto(it) },
)
