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

class ManualToolCache(
  val nameBlock: () -> String,
) {
  private val manualTools = mutableMapOf<String, ManualToolImpl>()
  val functionNames get() = manualTools.keys
  val functions get() = manualTools.values

  fun addToCache(
    toolName: String,
    toolImpl: ManualToolImpl,
  ) {
    manualTools[toolName] = toolImpl
  }

  fun containsTool(toolName: String) = manualTools.containsKey(toolName)

  fun getTool(toolName: String): ManualToolImpl = manualTools[toolName] ?: error("Manual tool name found: $toolName")
}
