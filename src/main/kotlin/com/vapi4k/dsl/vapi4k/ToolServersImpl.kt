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

package com.vapi4k.dsl.vapi4k

import com.vapi4k.api.vapi4k.ToolServer
import com.vapi4k.api.vapi4k.ToolServers
import com.vapi4k.dtos.vapi4k.ToolServerDto

interface ToolServerProperties {
  var name: String
  var serverUrl: String
  var serverSecret: String
  var timeoutSeconds: Int
}

class ToolServersImpl internal constructor(
  internal val application: Vapi4kApplicationImpl,
) : ToolServers {
  private fun hasName(toolServer: ToolServer) = application.toolServers.any { it.name == toolServer.name }

  private fun hasUrl(toolServer: ToolServer) = application.toolServers.any { it.serverUrl == toolServer.serverUrl }

  override fun toolServer(block: ToolServer.() -> Unit) {
    application.toolServers += ToolServer(ToolServerDto()).apply(block).also { toolServer ->
      when {
        hasName(toolServer) && toolServer.name.isEmpty() -> error("Multiple blank toolServer names")
        hasName(toolServer) -> error("Duplicate toolServer name: ${toolServer.name}")
        hasUrl(toolServer) -> error("Duplicate toolServer url: ${toolServer.serverUrl}")
      }
    }
  }
}
