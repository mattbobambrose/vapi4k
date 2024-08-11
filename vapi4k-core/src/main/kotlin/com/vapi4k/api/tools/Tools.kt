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
  fun serviceTool(
    obj: Any,
    vararg functions: KFunction<*>,
    block: Tool.() -> Unit = {},
  )

  fun manualTool(block: ManualTool.() -> Unit)

  fun externalTool(block: ExternalTool.() -> Unit)

  fun dtmfTool(block: BaseTool.() -> Unit)

  fun endCallTool(block: BaseTool.() -> Unit)

  fun voiceMailTool(block: BaseTool.() -> Unit)

  fun ghlTool(block: ToolWithMetaData.() -> Unit)

  fun makeTool(block: ToolWithMetaData.() -> Unit)

  fun transferTool(block: TransferTool.() -> Unit)
}
