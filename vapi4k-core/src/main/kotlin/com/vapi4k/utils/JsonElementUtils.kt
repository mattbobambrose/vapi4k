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

import com.vapi4k.api.vapi4k.AssistantRequestUtils.messageCallId
import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.isToolCall
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import io.ktor.http.Parameters
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object JsonElementUtils {
  val JsonElement.sessionCacheId get() = messageCallId.toSessionCacheId()

  val JsonElement.toolCallList
    get() = if (isToolCall)
      jsonElementList("message.toolCallList")
    else
      error("JsonElement is not a tool call request")

  val JsonElement.assistantClientMessages get() = jsonElementList("messageResponse.assistant.clientMessages")
  val JsonElement.assistantServerMessages get() = jsonElementList("messageResponse.assistant.serverMessages")

  private val EMPTY_JSON_ELEMENT = "{}".toJsonElement()

  fun emptyJsonElement() = EMPTY_JSON_ELEMENT

  internal fun JsonObjectBuilder.addArgsAndMessage(parameters: Parameters) {
    put("query-args", queryParametersAsArgs(parameters))
    put(
      "message",
      buildJsonObject {
        put("type", ASSISTANT_REQUEST.desc)
        put(
          "call",
          buildJsonObject {
            put("id", getRandomSecret(8, 4, 4, 12))
          },
        )
      },
    )
  }

  internal fun queryParametersAsArgs(parameters: Parameters): JsonObject =
    buildJsonObject {
      parameters.forEach { key, value ->
        put(
          key,
          if (value.size > 1)
            buildJsonArray { value.forEach { add(JsonPrimitive(it)) } }
          else
            JsonPrimitive(
              value.firstOrNull().orEmpty(),
            ),
        )
      }
    }
}
