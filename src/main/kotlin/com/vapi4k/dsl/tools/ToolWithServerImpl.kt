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

import com.vapi4k.api.tools.ToolWithServer
import com.vapi4k.api.vapi4k.Server
import com.vapi4k.dsl.vapi4k.ServerImpl
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.utils.DuplicateChecker

open class ToolWithServerImpl internal constructor(
  callerName: String,
  toolDto: ToolDto,
) : ToolImpl(callerName, toolDto),
  ToolWithServer {
  internal val serverChecker = DuplicateChecker()

  override fun server(block: Server.() -> Unit): Server {
    serverChecker.check("$callerName{} contains multiple server{} decls")
    return ServerImpl(toolDto.server).apply(block)
  }

  internal fun checkIfServerCalled() {
    if (!serverChecker.wasCalled)
      error("$callerName{} must contain a server{} decl")
  }
}
