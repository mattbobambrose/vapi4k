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

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JsonUtils {
  val JsonElement.stringValue get() = jsonPrimitive.content
  val JsonElement.jsonList get() = jsonArray.toList()

  private fun JsonElement.element(key: String) =
    jsonObject[key] ?: throw IllegalArgumentException("JsonElement key $key not found")

  operator fun JsonElement.get(vararg keys: String): JsonElement =
    keys.flatMap { it.split(".") }
      .fold(this) { acc, key -> acc.element(key) }


  inline fun <reified T> JsonElement.toObjectList() = jsonArray.map { Json.decodeFromJsonElement<T>(it) }

  val prettyFormat by lazy { Json { prettyPrint = true } }
  val rawFormat by lazy { Json { prettyPrint = false } }

  inline fun <reified T> T.toJsonString(prettyPrint: Boolean = false) =
    (if (prettyPrint) prettyFormat else rawFormat).encodeToString(this)

  inline fun <reified T> T.toJsonElement() = Json.encodeToJsonElement(this)

  inline fun <reified T> JsonElement.toObject() = Json.decodeFromJsonElement<T>(this)

  inline fun <reified T> String.toObject() = Json.decodeFromString<T>(this)
}
