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

import com.typesafe.config.ConfigFactory
import com.vapi4k.common.Constants.VAPI_API_URL
import com.vapi4k.plugin.Vapi4kLogger.logger
import com.vapi4k.responses.CallRequest
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonUtils.toJsonString
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import kotlinx.coroutines.runBlocking


enum class ApiObjectType(val endpoint: String) {
  CALLS("call"),
  ASSISTANTS("assistant"),
  PHONE_NUMBERS("phone-number"),
  FILES("file"),
  SQUADS("squad"),
  TOOLS("tool");

}

@AssistantDslMarker
class VapiApi private constructor(
  val config: ApplicationConfig,
  val authString: String,
) {
  fun phone(block: Phone.(ApplicationConfig) -> CallRequest) =
    runBlocking {
      val phone = Phone()
      val callRequest =
        phone.runCatching { block(this@VapiApi.config) }
          .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
          .onFailure { logger.error(it) { "Failed to create call request: ${it.message}" } }
          .getOrThrow()

      runCatching {
        httpClient.post("$VAPI_API_URL/call/phone") {
          contentType(Application.Json)
          bearerAuth(authString)
          setBody(callRequest)
        }
      }.onSuccess { logger.info { "Call made successfully" } }
        .onFailure { logger.error(it) { "Failed to make call: ${it.message}" } }
        .getOrThrow()
    }

  fun save(block: Save.() -> CallRequest) =
    runBlocking {
      val save = Save()
      val callRequest =
        save.runCatching(block)
          .onSuccess { logger.info { "Created call request: ${it.toJsonString()}" } }
          .onFailure { logger.error(it) { "Failed to create call request: ${it.message}" } }
          .getOrThrow()

      runCatching {
        httpClient.post("$VAPI_API_URL/call") {
          contentType(Application.Json)
          bearerAuth(authString)
          setBody(callRequest)
        }
      }.onSuccess { logger.info { "Call saved successfully" } }
        .onFailure { logger.error(it) { "Failed to save call: ${it.message}" } }
        .getOrThrow()
    }

  fun list(objectType: ApiObjectType) =
    runBlocking {
      runCatching {
        runCatching {
          httpClient.get("$VAPI_API_URL/${objectType.endpoint}") {
            contentType(Application.Json)
            bearerAuth(authString)
          }
        }.onSuccess { logger.info { "${objectType} objects fetched successfully" } }
          .onFailure { logger.error(it) { "Failed to fetch ${objectType} objects: ${it.message}" } }
          .getOrThrow()
      }
    }.getOrThrow()

  fun delete(callId: String) =
    runBlocking {
      runCatching {
        runCatching {
          httpClient.get("$VAPI_API_URL/call/$callId") {
            contentType(Application.Json)
            bearerAuth(authString)
          }
        }.onSuccess { logger.info { "Call deleted successfully" } }
          .onFailure { logger.error(it) { "Failed to delete call: ${it.message}" } }
          .getOrThrow()
      }
    }.getOrThrow()


  companion object {

    fun vapiApi(
      authString: String = "",
      block: VapiApi.() -> Unit = {},
    ): VapiApi {
      val config: HoconApplicationConfig = HoconApplicationConfig(ConfigFactory.load())
      val apiAuth = if (authString.isNotEmpty())
        authString
      else
        config.propertyOrNull("vapi.api.privateKey")?.getString() ?: error("No API key found in application.conf")

      return VapiApi(config, apiAuth).apply(block)
    }
  }
}

@AssistantDslMarker
class Phone {
  fun call(block: Call.() -> Unit): CallRequest = CallRequest().also { Call(it).apply(block) }
}

@AssistantDslMarker
class Save {
  fun call(block: Call.() -> Unit): CallRequest = CallRequest().also { Call(it).apply(block) }
}
