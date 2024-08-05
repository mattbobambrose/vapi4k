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

package com.vapi4k.utils

import com.vapi4k.api.vapi4k.AssistantRequestContext
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.isToolCall
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.messageCallId
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.get
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonElement
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonElementList
import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

object JsonElementUtils {
  val JsonElement.sessionCacheId get() = messageCallId.toSessionCacheId()

  val JsonElement.toolCallList
    get() = if (isToolCall)
      this["message.toolCallList"].toJsonElementList()
    else
      error("JsonElement is not a tool call request")


  val JsonElement.assistantClientMessages get() = this["assistant.clientMessages"].jsonArray
  val JsonElement.assistantServerMessages get() = this["assistant.serverMessages"].jsonArray

  private val EMPTY_JSON_ELEMENT = "{}".toJsonElement()

  fun emptyJsonElement() = EMPTY_JSON_ELEMENT

  private val EMPTY_REQUEST_CONTEXT = AssistantRequestContext(Vapi4kApplicationImpl(), emptyJsonElement())

  fun emptyRequestContext() = EMPTY_REQUEST_CONTEXT
}
