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

import com.vapi4k.common.SessionId
import com.vapi4k.dsl.model.AbstractModel
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.common.Utils.ensureStartsWith

internal class ManualToolCache(
  private val pathBlock: () -> String,
) {
  private val manualTools = mutableMapOf<String, ManualToolImpl>()
  private val createMap = mutableMapOf<String, SessionId>()

  val functions get() = manualTools.values
  val path get() = pathBlock().ensureStartsWith("/")

  fun addToCache(
    model: AbstractModel,
    toolName: String,
    toolImpl: ManualToolImpl,
  ) {
    if (createMap.containsKey(toolName)) {
      // Manual tools do not get stored per session
      // Skip adding the tool after the first time it is declared
      logger.debug { "Manual tool name already declared: $toolName" }
    } else {
      createMap[toolName] = model.sessionId

      if (manualTools.containsKey(toolName)) {
        error("Manual tool name already declared: $toolName")
      } else {
        manualTools[toolName] = toolImpl
        logger.info { "Added \"$toolName\" to $path manualTool cache" }
      }
    }
  }

  fun containsTool(toolName: String) = manualTools.containsKey(toolName)

  fun getTool(toolName: String): ManualToolImpl = manualTools[toolName] ?: error("Manual tool name found: $toolName")
}
