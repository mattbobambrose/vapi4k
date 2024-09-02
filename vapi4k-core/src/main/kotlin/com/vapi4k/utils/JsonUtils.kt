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

import com.vapi4k.common.Constants.POST_ARGS
import com.vapi4k.common.Constants.QUERY_ARGS
import com.vapi4k.common.QueryParams.SYSTEM_IDS
import com.vapi4k.utils.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.utils.enums.ServerRequestType.Companion.isToolCall
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.getOrNull
import com.vapi4k.utils.json.JsonElementUtils.isNotEmpty
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.stringValueOrNull
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonElementList
import com.vapi4k.utils.json.get
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.filter
import io.ktor.util.pipeline.PipelineContext
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

  internal fun PipelineContext<Unit, ApplicationCall>.buildRequestArg(json: JsonElement) =
    if (json.isNotEmpty() && json.containsKey("message.type")) {
      json
    } else {
      buildJsonObject {
        // Add values from the JSON object passed in with the POST request
        put(
          POST_ARGS,
          buildJsonObject {
            if (json.isNotEmpty()) {
              json.keys.forEach { key ->
                put(key, json.getOrNull(key)?.toJsonElement() ?: JsonPrimitive(""))
              }
            }
          },
        )
        addArgsAndMessage(call)
      }
    }

  internal fun JsonObjectBuilder.addArgsAndMessage(call: ApplicationCall) {
    put(QUERY_ARGS, call.queryParametersAsArgs())
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

  private fun ApplicationCall.queryParametersAsArgs(): JsonObject =
    buildJsonObject {
      request.queryParameters
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

  internal fun JsonElement.getToolNames(key: String) =
    if (containsKey("$key.tools"))
      jsonElementList(key, "tools").mapNotNull { it.stringValueOrNull("function.name") }
    else
      emptyList()

  internal fun JsonElement.getFunctionNames(key: String) =
    if (containsKey("$key.functions"))
      jsonElementList(key, "functions").mapNotNull { it.stringValueOrNull("name") }
    else
      emptyList()
}
