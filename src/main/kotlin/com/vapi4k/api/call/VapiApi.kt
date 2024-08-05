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

package com.vapi4k.api.call

import com.typesafe.config.ConfigFactory
import com.vapi4k.api.call.enums.ApiObjectType
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.id
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonElement
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonString
import com.vapi4k.common.Constants.VAPI_API_URL
import com.vapi4k.common.SessionCacheId.Companion.UNSPECIFIED_SESSION_CACHE_ID
import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.tools.ToolCache.Companion.toolCallCache
import com.vapi4k.dtos.api.CallRequestDto
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.Utils.errorMsg
import com.vapi4k.utils.Utils.nextSessionCacheId
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
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
  fun phone(block: Phone.() -> CallRequestDto): HttpResponse =
    runBlocking {
      val phone = Phone()
      val callRequest =
        phone.runCatching(block)
          .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
          .onFailure { e -> logger.error { "Failed to create call request: ${e.errorMsg}" } }
          .getOrThrow()

      val httpResponse = runCatching {
        httpClient.post("$VAPI_API_URL/call/phone") {
          configCall(authString)
          setBody(callRequest)
        }
      }.onSuccess { logger.info { "Call made successfully" } }
        .onFailure { e -> logger.error { "Failed to make call: ${e.errorMsg}" } }
        .getOrThrow()

      val jsonElement = httpResponse.bodyAsText().toJsonElement()
      val hasId = jsonElement.jsonObject.containsKey("id")
      if (hasId) {
        logger.info { "Call ID: ${jsonElement.id}" }
        toolCallCache.swapCacheKeys(phone.sessionCacheId, jsonElement.id.toSessionCacheId())
      } else {
        logger.warn { "No call ID found in response" }
      }

      httpResponse
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
  internal val sessionCacheId = nextSessionCacheId()
  private val assistantCacheIdSource: AssistantCacheIdSource = AssistantCacheIdSource()

  fun call(block: Call.() -> Unit): CallRequestDto =
    CallRequestDto().also { CallImpl(sessionCacheId, assistantCacheIdSource, it).apply(block) }
}

@AssistantDslMarker
class Save {
  private val assistantCacheIdSource: AssistantCacheIdSource = AssistantCacheIdSource()

  fun call(block: Call.() -> Unit): CallRequestDto =
    CallRequestDto().also { CallImpl(UNSPECIFIED_SESSION_CACHE_ID, assistantCacheIdSource, it).apply(block) }
}
