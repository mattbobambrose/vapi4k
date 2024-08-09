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

package com.vapi4k.server

import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonString
import com.vapi4k.client.ValidateAssistantResponse.validateAssistantRequestResponse
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.Constants.APPLICATION_ID
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.SESSION_CACHE_ID
import com.vapi4k.common.EnvVar.Companion.serverBaseUrl
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonUtils.toJsonArray
import com.vapi4k.utils.JsonUtils.toJsonObject
import com.vapi4k.utils.Utils.isNotNull
import com.vapi4k.utils.Utils.toErrorString
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

internal object ValidateApplication {
  suspend fun KtorCallContext.validateApplication(config: Vapi4kConfigImpl) {
    val appName = call.parameters["appName"].orEmpty()
    val application = config.applications.firstOrNull { it.serverPathAsSegment == appName }
    if (application.isNotNull())
      processValidateRequest(application)
    else
      call.respondText("Application for /$appName found", status = HttpStatusCode.NotFound)
  }

  suspend fun KtorCallContext.processValidateRequest(application: Vapi4kApplicationImpl) {
    val secret = call.request.queryParameters["secret"].orEmpty()
    val resp = validateAssistantRequestResponse(application, secret)
    call.respondText(resp, ContentType.Text.Html)
  }

  suspend fun KtorCallContext.validateToolInvokeResponse(config: Vapi4kConfigImpl) =
    runCatching {
      val params = call.request.queryParameters
      val applicationId = params[APPLICATION_ID]?.toApplicationId() ?: error("No $APPLICATION_ID found")
      val application = config.getApplication(applicationId)
      val toolRequest = getToolRequest(params)

      httpClient.post("$serverBaseUrl/${application.serverPathAsSegment}") {
        headers.append("x-vapi-secret", application.serverSecret)
        setBody(toolRequest.toJsonString<JsonObject>())
      }
    }.onSuccess { response ->
      call.respondText(response.bodyAsText().toJsonString())
    }.onFailure { e ->
      call.respondText(e.toErrorString(), status = HttpStatusCode.InternalServerError)
    }

  fun getToolRequest(params: Parameters): JsonObject {
    val sessionCacheId = params[SESSION_CACHE_ID] ?: error("No $SESSION_CACHE_ID found")
    return buildJsonObject {
      put(
        "message",
        mapOf(
          "type" to JsonPrimitive("tool-calls"),
          "call" to mapOf("id" to JsonPrimitive(sessionCacheId)).toJsonObject(),
          "toolCallList" to
            listOf(
              mapOf(
                "id" to JsonPrimitive("call_${getRandomSecret(24)}"),
                "type" to JsonPrimitive("function"),
                "function" to mapOf(
                  "name" to JsonPrimitive(params[FUNCTION_NAME] ?: error("No $FUNCTION_NAME found")),
                  "arguments" to
                    params
                      .names()
                      .filterNot { it in setOf(APPLICATION_ID, SESSION_CACHE_ID, FUNCTION_NAME) }
                      .filter { params[it].orEmpty().isNotEmpty() }.associateWith { JsonPrimitive(params[it]) }
                      .toJsonObject(),
                ).toJsonObject(),
              ).toJsonObject(),
            ).toJsonArray(),
        ).toJsonObject(),
      )
    }
  }
}
