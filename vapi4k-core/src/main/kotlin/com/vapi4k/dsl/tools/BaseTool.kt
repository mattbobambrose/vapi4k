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

import com.vapi4k.api.tools.ToolCondition
import com.vapi4k.api.tools.ToolMessageComplete
import com.vapi4k.api.tools.ToolMessageDelayed
import com.vapi4k.api.tools.ToolMessageFailed
import com.vapi4k.api.tools.ToolMessageStart
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dtos.tools.ToolMessageCondition

@AssistantDslMarker
interface BaseTool {
  fun requestStartMessage(block: ToolMessageStart.() -> Unit): ToolMessageStart

  fun requestCompleteMessage(block: ToolMessageComplete.() -> Unit): ToolMessageComplete

  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed

  fun requestDelayedMessage(block: ToolMessageDelayed.() -> Unit): ToolMessageDelayed

  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg additionalConditions: ToolMessageCondition,
    block: ToolCondition.() -> Unit,
  )
}
