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

package com.vapi4k.dsl.toolservice

import com.vapi4k.api.tools.ToolMessageComplete
import com.vapi4k.api.tools.ToolMessageFailed
import com.vapi4k.api.toolservice.RequestCompleteMessages
import com.vapi4k.api.toolservice.RequestFailedMessages
import kotlinx.serialization.json.JsonElement

abstract class ToolCallService {
  lateinit var toolCallServiceInfo: String

  open fun onToolCallComplete(
    toolCallRequest: JsonElement,
    result: String,
  ): List<ToolMessageComplete> = emptyList()

  open fun onToolCallFailed(
    toolCallRequest: JsonElement,
    errorMessage: String,
  ): List<ToolMessageFailed> = emptyList()

  fun requestCompleteMessages(block: RequestCompleteMessages.() -> Unit) =
    RequestCompleteMessages().apply(block).messageList

  fun requestFailedMessages(block: RequestFailedMessages.() -> Unit) = RequestFailedMessages().apply(block).messageList
}
