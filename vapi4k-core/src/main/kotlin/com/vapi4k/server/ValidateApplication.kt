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

import com.vapi4k.client.ToolType
import com.vapi4k.client.ValidateAssistantResponse.validateAssistantRequestPage
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.ApplicationName
import com.vapi4k.common.ApplicationName.Companion.toApplicationName
import com.vapi4k.common.Constants.APP_NAME
import com.vapi4k.common.Constants.APP_TYPE
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.common.QueryParams.APPLICATION_ID
import com.vapi4k.common.QueryParams.ASSISTANT_ID
import com.vapi4k.common.QueryParams.SECRET_PARAM
import com.vapi4k.common.QueryParams.SESSION_ID
import com.vapi4k.common.QueryParams.SYSTEM_IDS
import com.vapi4k.common.QueryParams.TOOL_TYPE
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.ApplicationType.INBOUND_CALL
import com.vapi4k.dsl.vapi4k.ApplicationType.OUTBOUND_CALL
import com.vapi4k.dsl.vapi4k.ApplicationType.WEB
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.DslUtils.getRandomString
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonUtils.getSessionIdFromQueryParameters
import com.vapi4k.utils.JsonUtils.toJsonArray
import com.vapi4k.utils.JsonUtils.toJsonObject
import com.vapi4k.utils.MiscUtils.appendQueryParams
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
      val appType = call.parameters[APP_TYPE].orEmpty()
      val appName = call.parameters[APP_NAME].orEmpty().toApplicationName()
      val app =
        when (appType) {
          WEB.pathPrefix -> config.webApplications
          INBOUND_CALL.pathPrefix -> config.inboundCallApplications
          OUTBOUND_CALL.pathPrefix -> config.outboundCallApplications
          else -> error("Invalid application type: $appType")
        }.firstOrNull { it.serverPathAsSegment == appName.value }

      if (app.isNotNull())
        processValidateRequest(config, app, appName)
      else
        call.respondText("Application for /${appName.value} not found", status = HttpStatusCode.NotFound)
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
    appName: ApplicationName,
  ) {
    val secret = call.request.queryParameters[SECRET_PARAM].orEmpty()
    val html = validateAssistantRequestPage(config, application, appName, secret)
    call.respondText(html, ContentType.Text.Html)
  }

  suspend fun KtorCallContext.validateToolInvokeRequest(config: Vapi4kConfigImpl) =
    runCatching {
      val params = call.request.queryParameters
      val applicationId = params[APPLICATION_ID]?.toApplicationId() ?: error("No $APPLICATION_ID found")
      val assistantId = params[ASSISTANT_ID]?.toApplicationId() ?: error("No $ASSISTANT_ID found")
      val app = config.getApplicationById(applicationId)
      val toolType = ToolType.valueOf(params[TOOL_TYPE] ?: error("No $TOOL_TYPE found"))
      val toolRequest = generateToolRequest(toolType, params)
      val sessionId = call.getSessionIdFromQueryParameters() ?: error("No $SESSION_ID found")
      val serverUrl =
        app.serverUrl.appendQueryParams(
          SESSION_ID to sessionId.value,
          ASSISTANT_ID to assistantId.value,
        )
      httpClient.post(serverUrl) {
        headers.append(VAPI_SECRET_HEADER, app.serverSecret)
        setBody(toolRequest.toJsonString<JsonObject>())
      }
    }.onSuccess { response ->
      val resp = response.bodyAsText()
      call.respondText(resp.toJsonString())
    }.onFailure { e ->
      logger.error(e) { "Error validating tool invoke request" }
      call.respondText(e.toErrorString(), status = HttpStatusCode.InternalServerError)
    }

  private fun functionParams(
    params: Parameters,
    argName: String,
  ) = mapOf(
    "name" to JsonPrimitive(params[FUNCTION_NAME] ?: error("No $FUNCTION_NAME found")),
    argName to
      params
        .names()
        .filterNot { it in SYSTEM_IDS }
        .filter { params[it].orEmpty().isNotEmpty() }.associateWith { JsonPrimitive(params[it]) }
        .toJsonObject(),
  ).toJsonObject()

  fun generateToolRequest(
    toolType: ToolType,
    params: Parameters,
  ): JsonObject {
    val sessionId = params[SESSION_ID] ?: error("No $SESSION_ID found")
    return buildJsonObject {
      put(
        "message",
        mapOf(
          "type" to JsonPrimitive(toolType.messageType.desc),
          "call" to mapOf("id" to JsonPrimitive(sessionId)).toJsonObject(),
          if (toolType == ToolType.FUNCTION)
            toolType.funcName to functionParams(params, toolType.paramName)
          else
            "toolCallList" to
              listOf(
                mapOf(
                  "id" to JsonPrimitive("call_${getRandomString(24)}"),
                  "type" to JsonPrimitive("function"),
                  toolType.funcName to functionParams(params, toolType.paramName),
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
