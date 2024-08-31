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

import com.vapi4k.common.AssistantId.Companion.toAssistantId
import com.vapi4k.common.Constants.QUERY_ARGS
import com.vapi4k.common.QueryParams.ASSISTANT_ID
import com.vapi4k.common.QueryParams.SESSION_ID
import com.vapi4k.common.QueryParams.SYSTEM_IDS
import com.vapi4k.common.SessionId.Companion.toSessionId
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.isToolCall
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonElementList
import com.vapi4k.utils.json.get
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.util.filter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

object JsonUtils {
  inline fun <reified T> JsonElement.toObject() = Json.decodeFromJsonElement<T>(this)

  inline fun <reified T> String.toObject() = Json.decodeFromString<T>(this)

  inline fun <reified T> JsonElement.toObjectList() = jsonArray.map { Json.decodeFromJsonElement<T>(it) }

  fun JsonElement.firstInList() = toJsonElementList().first()

  fun Map<String, JsonElement>.toJsonObject() = JsonObject(this)

  fun List<JsonElement>.toJsonArray() = JsonArray(this)

  fun JsonElement.modifyObjectWith(
    key: String,
    block: (MutableMap<String, JsonElement>) -> Unit,
  ): JsonObject = this[key].jsonObject.toMutableMap().also(block).toJsonObject()

  fun Map<String, Any>.toJsonPrimitives() =
    mapValues {
      if (it.value is String) JsonPrimitive(it.value as String) else it.value as JsonPrimitive
    }

  val JsonElement.toolCallList
    get() = if (isToolCall)
      jsonElementList("message.toolCallList")
    else
      error("JsonElement is not a tool call request")

  val JsonElement.assistantClientMessages get() = jsonElementList("messageResponse.assistant.clientMessages")
  val JsonElement.assistantServerMessages get() = jsonElementList("messageResponse.assistant.serverMessages")

  val EMPTY_JSON_ELEMENT = "{}".toJsonElement()

  fun emptyJsonElement() = EMPTY_JSON_ELEMENT

  internal fun JsonObjectBuilder.addArgsAndMessage(call: ApplicationCall) {
    put(QUERY_ARGS, queryParametersAsArgs(call.request.queryParameters))
    put(
      "message",
      buildJsonObject {
        put("type", ASSISTANT_REQUEST.desc)
//        put(
//          "call",
//          buildJsonObject {
//            put("id", call.getSessionIdFromQueryParameters().value)
//          },
//        )
      },
    )
  }

  internal fun queryParametersAsArgs(parameters: Parameters): JsonObject =
    buildJsonObject {
      parameters
        .filter { key, value -> key !in SYSTEM_IDS }
        .forEach { key, value ->
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

  internal fun ApplicationCall.getSessionIdFromQueryParameters() = request.queryParameters[SESSION_ID]?.toSessionId()

  internal fun ApplicationCall.getAssistantIdFromQueryParameters() =
    request.queryParameters[ASSISTANT_ID]?.toAssistantId()
}
