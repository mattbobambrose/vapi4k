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

package com.vapi4k.utils.json

import com.vapi4k.utils.json.JsonElementUtils.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// This is outside the object to improve auto-imports prompts in IJ
operator fun JsonElement.get(vararg keys: String): JsonElement =
  keys.flatMap { it.split(".") }
    .fold(this) { acc, key -> acc.element(key) }

object JsonElementUtils {
  val prettyFormat by lazy {
    Json {
      prettyPrint = true
      prettyPrintIndent = "  "
    }
  }
  val rawFormat by lazy { Json { prettyPrint = false } }

  val JsonElement.stringValue get() = jsonPrimitive.content
  val JsonElement.intValue get() = jsonPrimitive.content.toInt()
  val JsonElement.doubleValue get() = jsonPrimitive.content.toDouble()
  val JsonElement.booleanValue get() = jsonPrimitive.content.toBoolean()
  val JsonElement.keys get() = jsonObject.keys

  fun JsonElement.stringValue(key: String) = get(key).stringValue

  fun JsonElement.intValue(key: String) = get(key).intValue

  fun JsonElement.doubleValue(key: String) = get(key).doubleValue

  fun JsonElement.booleanValue(key: String) = get(key).booleanValue

  fun JsonElement.jsonElementList(key: String) = get(key).toJsonElementList()

  internal fun JsonElement.element(key: String) =
    jsonObject[key] ?: throw IllegalArgumentException("JsonElement key \"$key\" not found")

  fun JsonElement.property(vararg keys: String): JsonElement =
    keys.flatMap { it.split(".") }
      .fold(this) { acc, key -> acc.element(key) }

  fun JsonElement.containsKey(vararg keys: String): Boolean {
    val ks = keys.flatMap { it.split(".") }
    var currElement = this
    for (k in ks) {
      if (k !in currElement.keys)
        return false
      else
        currElement = currElement[k]
    }
    return true
  }

  val JsonElement.size get() = jsonObject.size

  val JsonElement.isEmpty get() = jsonObject.isEmpty()

  fun JsonElement.toJsonElementList() = jsonArray.toList()

  inline fun <reified T> T.toJsonString(prettyPrint: Boolean = true) =
    (if (prettyPrint) prettyFormat else rawFormat).encodeToString(this)

  inline fun <reified T> T.toJsonElement() = Json.encodeToJsonElement(this)

  fun String.toJsonElement() = Json.parseToJsonElement(this)

  fun String.toJsonString() = toJsonElement().toJsonString(true)
}
