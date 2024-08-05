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

package com.vapi4k.api.tools

import com.vapi4k.dsl.assistant.AssistantDslMarker
import kotlin.reflect.KFunction

@AssistantDslMarker
interface Tools {
  fun tool(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: Tool.() -> Unit = {},
  )

  fun dtmf(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: Tool.() -> Unit = {},
  )

  fun endCall(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: Tool.() -> Unit = {},
  )

  fun voiceMail(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: Tool.() -> Unit = {},
  )

  fun ghl(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: ToolWithMetaData.() -> Unit = {},
  )

  fun make(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: ToolWithMetaData.() -> Unit = {},
  )

  fun transfer(
    obj: Any,
    vararg functions: KFunction<*>,
    toolServerName: String = "",
    block: TransferTool.() -> Unit = {},
  )
}
