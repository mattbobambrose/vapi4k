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

package com.vapi4k.dsl.assistant

import com.vapi4k.common.Constants.VAPI_API_URL
import com.vapi4k.plugin.Vapi4kLogger.logger
import com.vapi4k.responses.CallRequest
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonUtils.toJsonString
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking

@AssistantDslMarker
class VapiApi private constructor(val authString: String) {
  fun make(block: () -> CallRequest) {
    val callRequest =
      runCatching(block)
        .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
        .onFailure { logger.error(it) { "Failed to create call request: ${it.message}" } }
        .getOrThrow()

    runBlocking {
      runCatching {
        val response = httpClient.post("$VAPI_API_URL/call/phone") {
          contentType(Application.Json)
          bearerAuth(authString)
          setBody(callRequest)
        }
      }.onSuccess { logger.info { "Call made successfully" } }
        .onFailure { logger.error(it) { "Failed to make call: ${it.message}" } }
        .getOrThrow()
    }

    fun call(block: Call.() -> Unit): CallRequest = CallRequest().also { Call(it).apply(block) }
  }

  companion object {
    fun vapiApi(
      authString: String,
      block: VapiApi.() -> Unit,
    ) = VapiApi(authString).apply(block)
  }
}

