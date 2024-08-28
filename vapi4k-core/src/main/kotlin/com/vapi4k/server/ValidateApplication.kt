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

import com.vapi4k.client.ValidateAssistantResponse.validateAssistantRequestPage
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.Constants.APPLICATION_ID
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.SESSION_CACHE_ID
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.common.QueryParams.SECRET_QUERY_PARAM
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.DslUtils.getRandomString
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonUtils.toJsonArray
import com.vapi4k.utils.JsonUtils.toJsonObject
import com.vapi4k.utils.common.Utils.isNotNull
import com.vapi4k.utils.common.Utils.toErrorString
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import kotlinx.html.body
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.net.ConnectException

internal object ValidateApplication {
  suspend fun KtorCallContext.validateApplication(config: Vapi4kConfigImpl) =
    runCatching {
      val appName = call.parameters["appName"].orEmpty()
      val application = config.allApplications.firstOrNull { it.serverPathAsSegment == appName }
      if (application.isNotNull())
        processValidateRequest(config, application, appName)
      else
        call.respondText("Application for /$appName not found", status = HttpStatusCode.NotFound)
    }.getOrElse {
      if (it is ConnectException) {
        val html = serverBasePage()
        call.respondText(html, ContentType.Text.Html)
      } else {
        logger.error(it) { "Error validating application" }
        call.respondText(it.toErrorString(), status = HttpStatusCode.InternalServerError)
      }
    }

  private fun serverBasePage() =
    createHTML()
      .html {
        head {
          title { +"Assistant Request Validation" }
        }
        body {
          h2 { +"Configuration Error" }
          p {
            +"Please set the environment variable VAPI4K_BASE_URL =  "
            span {
              id = "serverBaseUrl"
            }
          }
          script { src = "$STATIC_BASE/js/server-base.js" }
        }
      }

  suspend fun KtorCallContext.processValidateRequest(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    appName: String,
  ) {
    val secret = call.request.queryParameters[SECRET_QUERY_PARAM].orEmpty()
    val html = validateAssistantRequestPage(config, application, appName, secret)
    call.respondText(html, ContentType.Text.Html)
  }

  suspend fun KtorCallContext.validateToolInvokeRequest(config: Vapi4kConfigImpl) =
    runCatching {
      val params = call.request.queryParameters
      val applicationId = params[APPLICATION_ID]?.toApplicationId() ?: error("No $APPLICATION_ID found")
      val application = config.getApplication(applicationId)
      val toolRequest = getToolRequest(params)

      httpClient.post(application.serverUrl) {
        // logger.info { "Assigning secret: ${application.serverSecret}" }
        headers.append(VAPI_SECRET_HEADER, application.serverSecret)
        setBody(toolRequest.toJsonString<JsonObject>())
      }
    }.onSuccess { response ->
      call.respondText(response.bodyAsText().toJsonString())
    }.onFailure { e ->
      logger.error(e) { "Error validating tool invoke request" }
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
                "id" to JsonPrimitive("call_${getRandomString(24)}"),
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

  internal fun KtorCallContext.isValidSecret(configPropertiesSecret: String): Boolean {
    val secret = call.request.headers[VAPI_SECRET_HEADER].orEmpty()
    return (configPropertiesSecret.isBlank() || secret.trim() == configPropertiesSecret.trim()).also {
      if (!it) {
        logger.info { """Invalid secret. Found: "$secret" Expected: "$configPropertiesSecret"""" }
      }
    }
  }
}
