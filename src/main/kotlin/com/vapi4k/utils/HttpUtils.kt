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

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object HttpUtils {
  fun HttpResponse.bodyAsJsonElement(): JsonElement =
    runBlocking { Json.parseToJsonElement(this@bodyAsJsonElement.bodyAsText()) }

  val httpClient by lazy {
    HttpClient(CIO) {
      install(ContentNegotiation) {
        json(
          Json {
            prettyPrint = true
          },
        )
      }

      // TODO: Look into this
//      install(ContentEncoding) {
//        deflate(1.0F)
//        gzip(0.9F)
//      }
    }
  }
}
