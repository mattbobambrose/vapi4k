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

import com.vapi4k.BuildConfig
import com.vapi4k.api.vapi4k.Vapi4kConfig
import com.vapi4k.api.vapi4k.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.api.vapi4k.enums.ServerRequestType.END_OF_CALL_REPORT
import com.vapi4k.api.vapi4k.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.api.vapi4k.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.requestType
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonElement
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonString
import com.vapi4k.client.ValidateAssistantResponse.validateAssistantRequestResponse
import com.vapi4k.common.ApplicationId.Companion.toApplicationId
import com.vapi4k.common.Constants.APPLICATION_ID
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.SESSION_CACHE_ID
import com.vapi4k.common.Constants.STYLES_CSS
import com.vapi4k.common.Endpoints.CACHES_PATH
import com.vapi4k.common.Endpoints.CLEAR_CACHES_PATH
import com.vapi4k.common.Endpoints.METRICS_PATH
import com.vapi4k.common.Endpoints.PING_PATH
import com.vapi4k.common.Endpoints.VALIDATE_INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.Endpoints.VERSION_PATH
import com.vapi4k.common.EnvVar.Companion.isProduction
import com.vapi4k.common.EnvVar.Companion.logEnvVarValues
import com.vapi4k.common.EnvVar.Companion.serverBaseUrl
import com.vapi4k.common.Version
import com.vapi4k.common.Version.Companion.versionDesc
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponse.Companion.getToolCallResponse
import com.vapi4k.server.AdminJobs.RequestResponseCallback
import com.vapi4k.server.AdminJobs.invokeRequestCallbacks
import com.vapi4k.server.AdminJobs.invokeResponseCallbacks
import com.vapi4k.server.AdminJobs.startCacheCleaningThread
import com.vapi4k.server.AdminJobs.startCallbackThread
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.JsonUtils.toJsonArray
import com.vapi4k.utils.JsonUtils.toJsonObject
import com.vapi4k.utils.Utils.errorMsg
import com.vapi4k.utils.Utils.getBanner
import com.vapi4k.utils.Utils.isNotNull
import com.vapi4k.utils.Utils.lambda
import com.vapi4k.utils.Utils.toErrorString
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.time.measureTimedValue

@Version(
  version = BuildConfig.VERSION,
  releaseDate = BuildConfig.RELEASE_DATE,
  buildTime = BuildConfig.BUILD_TIME,
)
object Vapi4kServer {
  val logger = KotlinLogging.logger {}
}

typealias KtorCallContext = PipelineContext<Unit, ApplicationCall>

val Vapi4k: ApplicationPlugin<Vapi4kConfig> = createApplicationPlugin(
  name = "Vapi4k",
  createConfiguration = { Vapi4kConfigImpl() },
) {
  val callbackChannel = Channel<RequestResponseCallback>(Channel.UNLIMITED)

  with(logger) {
    info { getBanner("banners/vapi4k-server.banner", logger) }
    info { Vapi4kServer::class.versionDesc() }
  }

  val config = AssistantImpl.config
  config.applicationConfig = environment?.config ?: error("No environment config found")

  logEnvVarValues()

  startCallbackThread(callbackChannel)
  startCacheCleaningThread(config)

  environment?.monitor?.apply {
    val name = Vapi4kServer::class.simpleName
    subscribe(ApplicationStarting) { it.environment.log.info("$name is starting") }
    subscribe(ApplicationStarted) { it.environment.log.info("$name is started") }
    subscribe(ApplicationStopped) { it.environment.log.info("$name is stopped") }
    subscribe(ApplicationStopping) { it.environment.log.info("$name is stopping") }
  }

  with(application) {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    defaultKtorConfig(appMicrometerRegistry)

    routing {

      staticResources("/assets", "static")

      get(PING_PATH) { call.respondText("pong") }
      get(VERSION_PATH) { versionResponse() }

      if (!isProduction) {
        get(METRICS_PATH) { call.respond(appMicrometerRegistry.scrape()) }
        get(CACHES_PATH) {
          call.respond(
            buildJsonObject {
              config.allApplications.forEach { application ->
                put(
                  application.serverPathAsSegment,
                  application.toolCache.cacheAsJson().toJsonElement(),
                )
              }
            },
          )
        }
        get(CLEAR_CACHES_PATH) {
          config.allApplications.forEach { application ->
            application.toolCache.clearToolCache()
          }
          call.respondRedirect(CACHES_PATH)
        }

        get(VALIDATE_PATH) { validateRoot(config) }
        get("$VALIDATE_PATH/{appName}") { validateApplication(config) }
        get(VALIDATE_INVOKE_TOOL_PATH) { validateToolInvokeResponse(config) }
      }

      config.applications.forEach { application ->
        val serverPath = application.serverPath
        logger.info { "Adding POST serverPath endpoint: \"$serverPath\"" }
        get(serverPath) { call.respondText("$serverPath requires a post request", status = MethodNotAllowed) }
        post(serverPath) { processAssistantRequests(application, callbackChannel) }
      }
    }
  }
}

private suspend fun KtorCallContext.versionResponse() {
  call.respondText(ContentType.Application.Json) { Vapi4kServer::class.versionDesc(true) }
}

private suspend fun KtorCallContext.validateApplication(config: Vapi4kConfigImpl) {
  val appName = call.parameters["appName"].orEmpty()
  val application = config.applications.firstOrNull { it.serverPathAsSegment == appName }
  if (application.isNotNull())
    processValidateRequest(application)
  else
    call.respondText("Application not found", status = HttpStatusCode.NotFound)
}

private suspend fun KtorCallContext.validateRoot(config: Vapi4kConfigImpl) {
  if (config.applications.size == 1) {
    val application = config.applications.first()
    call.respondRedirect("$VALIDATE_PATH/${application.serverPathAsSegment}?secret=${application.serverSecret}")
  } else {
    val html = createHTML()
      .html {
        head {
          link {
            rel = "stylesheet"
            href = STYLES_CSS
          }
          title { +"Assistant Request Validation" }
          script { src = HTMX_SOURCE_URL }
        }
        body {
          h2 {
            +"All Vapi4k Applications"
          }
          ul {
            config.allApplications.forEach { application ->
              li {
                a {
                  href = "$VALIDATE_PATH/${application.serverPathAsSegment}?secret=${application.serverSecret}"
                  +application.serverPath
                }
              }
            }
          }
        }
      }
    call.respondText(html, ContentType.Text.Html)
  }
}

private suspend fun KtorCallContext.processAssistantRequests(
  application: Vapi4kApplicationImpl,
  callbackChannel: Channel<RequestResponseCallback>,
) {
  if (isProduction) {
    assistantRequestResponse(application, callbackChannel)
  } else {
    runCatching {
      assistantRequestResponse(application, callbackChannel)
    }.onFailure { e ->
      logger.error(e) { "Error processing serverUrl POST request: ${e.errorMsg}" }
      call.respondText(e.toErrorString(), status = HttpStatusCode.InternalServerError)
    }
  }
}

private suspend fun KtorCallContext.processValidateRequest(application: Vapi4kApplicationImpl) {
  val secret = call.request.queryParameters["secret"].orEmpty()
  val resp = validateAssistantRequestResponse(application, secret)
  call.respondText(resp, ContentType.Text.Html)
}

private suspend fun KtorCallContext.validateToolInvokeResponse(config: Vapi4kConfigImpl) =
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

private fun getToolRequest(params: Parameters): JsonObject {
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

private suspend fun KtorCallContext.assistantRequestResponse(
  application: Vapi4kApplicationImpl,
  requestResponseCallbackChannel: Channel<RequestResponseCallback>,
) {
  if (isValidSecret(application.serverSecret)) {
    val json = call.receive<String>()
    val request = json.toJsonElement()
    val requestType = request.requestType

    invokeRequestCallbacks(application, requestResponseCallbackChannel, requestType, request)

    val (response, duration) = measureTimedValue {
      when (requestType) {
        ASSISTANT_REQUEST -> {
          val response = application.getAssistantResponse(request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        FUNCTION_CALL -> {
          val response = getFunctionCallResponse(application, request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        TOOL_CALL -> {
          val response = getToolCallResponse(application, request)
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        END_OF_CALL_REPORT -> {
          if (application.eocrCacheRemovalEnabled) {
            val sessionCacheId = request.sessionCacheId
            application.toolCache.removeFromCache(sessionCacheId) { funcInfo ->
              logger.info { "EOCR removed ${funcInfo.functions.size} cache entries [${funcInfo.ageSecs}] " }
            } ?: logger.warn { "EOCR unable to find and remove cache entry [$sessionCacheId]" }
          }

          val response = SimpleMessageResponse("End of call report received")
          call.respond(response)
          lambda { response.toJsonElement() }
        }

        else -> {
          val response = SimpleMessageResponse("$requestType received")
          call.respond(response)
          lambda { response.toJsonElement() }
        }
      }
    }

    invokeResponseCallbacks(application, requestResponseCallbackChannel, requestType, response, duration)
  }
}

private suspend fun KtorCallContext.isValidSecret(configPropertiesSecret: String): Boolean {
  val secret = call.request.headers["x-vapi-secret"].orEmpty()
  return if (configPropertiesSecret.isNotEmpty() && secret != configPropertiesSecret) {
    logger.info { "Invalid secret: [$secret] [$configPropertiesSecret]" }
    call.respond(HttpStatusCode.Forbidden, "Invalid secret")
    false
  } else {
    true
  }
}
