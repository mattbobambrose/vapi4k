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

package com.vapi4k.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JsonExtensions {
  val JsonElement.stringValue get() = jsonPrimitive.content

  private fun JsonElement.element(key: String) = jsonObject[key] ?: throw IllegalArgumentException("Key $key not found")

  operator fun JsonElement.get(vararg keys: String): JsonElement =
    keys.flatMap { it.split(".") }
      .fold(this) { acc, key -> acc.element(key) }


  val JsonElement.jsonList get() = jsonArray.toList()

  inline fun <reified T> JsonElement.toObjList() = jsonArray.map { Json.decodeFromJsonElement<T>(it) }


}
