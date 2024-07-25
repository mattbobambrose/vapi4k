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

package com.vapi4k.dsl.assistant.api

import com.typesafe.config.ConfigFactory
import com.vapi4k.common.Constants.VAPI_API_URL
import com.vapi4k.common.SessionId.Companion.UNSPECIFIED_SESSION_ID
import com.vapi4k.common.SessionId.Companion.toSessionId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.api.enums.ApiObjectType
import com.vapi4k.dsl.assistant.tools.ToolCache.swapCacheKeys
import com.vapi4k.plugin.Vapi4kLogger.logger
import com.vapi4k.responses.api.CallRequestDto
import com.vapi4k.utils.HttpUtils.bodyAsJsonElement
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonElementUtils.id
import com.vapi4k.utils.JsonUtils.toJsonString
import com.vapi4k.utils.Utils.errorMsg
import com.vapi4k.utils.Utils.nextCacheId
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonObject


@AssistantDslMarker
class VapiApi private constructor(
  val config: ApplicationConfig,
  private val authString: String,
) {
  fun phone(block: Phone.() -> CallRequestDto): HttpResponse {
    val phone = Phone()
    val httpResponse =
      runBlocking {

        val callRequest =
          phone.runCatching(block)
            .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
            .onFailure { e -> logger.error { "Failed to create call request: ${e.errorMsg}" } }
            .getOrThrow()

        runCatching {
          httpClient.post("$VAPI_API_URL/call/phone") {
            configCall(authString)
            setBody(callRequest)
          }
        }.onSuccess { logger.info { "Call made successfully" } }
          .onFailure { e -> logger.error { "Failed to make call: ${e.errorMsg}" } }
          .getOrThrow()
      }

    val jsonElement = httpResponse.bodyAsJsonElement()
    val hasId = jsonElement.jsonObject.containsKey("id")
    if (hasId) {
      logger.info { "Call ID: ${jsonElement.id}" }
      swapCacheKeys(phone.cacheId, jsonElement.id.toSessionId())
    } else {
      logger.warn { "No call ID found in response" }
    }

    return httpResponse
  }

  internal fun test(block: Phone.() -> CallRequestDto) =
    runBlocking {
      Phone().runCatching(block)
        .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
        .onFailure { e -> logger.error { "Failed to create call request: ${e.errorMsg}" } }
        .getOrThrow()
    }

  fun save(block: Save.() -> CallRequestDto) =
    runBlocking {
      val callRequest =
        Save().runCatching(block)
          .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
          .onFailure { e -> logger.error { "Failed to create call request: ${e.errorMsg}" } }
          .getOrThrow()

      runCatching {
        httpClient.post("$VAPI_API_URL/call") {
          configCall(authString)
          setBody(callRequest)
        }
      }.onSuccess { logger.info { "Call saved successfully" } }
        .onFailure { e -> logger.error { "Failed to save call: ${e.errorMsg}" } }
        .getOrThrow()
    }

  fun list(objectType: ApiObjectType) =
    runBlocking {
      runCatching {
        runCatching {
          httpClient.get("$VAPI_API_URL/${objectType.endpoint}") { configCall(authString) }
        }.onSuccess { logger.info { "$objectType objects fetched successfully" } }
          .onFailure { e -> logger.error { "Failed to fetch $objectType objects: ${e.errorMsg}" } }
          .getOrThrow()
      }
    }.getOrThrow()

  fun delete(callId: String) =
    runBlocking {
      runCatching {
        runCatching {
          httpClient.get("$VAPI_API_URL/call/$callId") { configCall(authString) }
        }.onSuccess { logger.info { "Call deleted successfully" } }
          .onFailure { e -> logger.error { "Failed to delete call: ${e.errorMsg}" } }
          .getOrThrow()
      }
    }.getOrThrow()


  companion object {
    internal fun HttpRequestBuilder.configCall(authString: String) {
      contentType(Application.Json)
      bearerAuth(authString)
    }

    fun vapiApi(authString: String = ""): VapiApi {
      val config = HoconApplicationConfig(ConfigFactory.load())
      val apiAuth =
        authString.ifEmpty {
          config.propertyOrNull("vapi.api.privateKey")?.getString()
            ?: System.getenv("VAPI_PRIVATE_KEY")
            ?: error("No API key found in application.conf")
        }

      return VapiApi(config, apiAuth)
    }
  }
}

@AssistantDslMarker
class Phone {
  internal val cacheId = nextCacheId()
  fun call(block: Call.() -> Unit): CallRequestDto = CallRequestDto().also { CallImpl(cacheId, it).apply(block) }
}

@AssistantDslMarker
class Save {
  fun call(block: Call.() -> Unit): CallRequestDto =
    CallRequestDto().also { CallImpl(UNSPECIFIED_SESSION_ID, it).apply(block) }
}
