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

package com.vapi4k.plugin

import com.github.mattbobambrose.vapi4k.BuildConfig
import com.vapi4k.api.vapi4k.Vapi4kConfig
import com.vapi4k.common.AssistantId.Companion.EMPTY_ASSISTANT_ID
import com.vapi4k.common.Constants.POST_ARGS
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CoreEnvVars.isProduction
import com.vapi4k.common.CoreEnvVars.loadCoreEnvVars
import com.vapi4k.common.Endpoints.CACHES_PATH
import com.vapi4k.common.Endpoints.CLEAR_CACHES_PATH
import com.vapi4k.common.Endpoints.ENV_PATH
import com.vapi4k.common.Endpoints.METRICS_PATH
import com.vapi4k.common.Endpoints.PING_PATH
import com.vapi4k.common.Endpoints.VALIDATE_INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.Endpoints.VERSION_PATH
import com.vapi4k.common.SessionId.Companion.toSessionId
import com.vapi4k.common.Version
import com.vapi4k.common.Version.Companion.versionDesc
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.vapi4k.ApplicationType.INBOUND_CALL
import com.vapi4k.dsl.vapi4k.InboundCallApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.server.AdminJobs.startCacheCleaningThread
import com.vapi4k.server.AdminJobs.startCallbackThread
import com.vapi4k.server.CacheActions.cachesRequest
import com.vapi4k.server.CacheActions.clearCaches
import com.vapi4k.server.InboundCallActions.inboundCallRequest
import com.vapi4k.server.OutboundCallAndWebActions.outboundCallAndWebRequest
import com.vapi4k.server.defaultKtorConfig
import com.vapi4k.server.installContentNegotiation
import com.vapi4k.utils.JsonUtils.addArgsAndMessage
import com.vapi4k.utils.JsonUtils.getAssistantIdFromQueryParameters
import com.vapi4k.utils.JsonUtils.getSessionIdFromQueryParameters
import com.vapi4k.utils.MiscUtils.getBanner
import com.vapi4k.utils.envvar.EnvVar.Companion.jsonEnvVarValues
import com.vapi4k.utils.envvar.EnvVar.Companion.logEnvVarValues
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.getOrNull
import com.vapi4k.utils.json.JsonElementUtils.isNotEmpty
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.validate.ValidateApplication.validateApplication
import com.vapi4k.validate.ValidateApplication.validateToolInvokeRequest
import com.vapi4k.validate.ValidateRoot.validateRootPage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

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
  loadCoreEnvVars()

  with(logger) {
    info { getBanner("banners/vapi4k-server.banner", logger) }
    info { Vapi4kServer::class.versionDesc() }
  }

  val config = AssistantImpl.config
  config.applicationConfig = environment?.config ?: error("No environment config found")
  config.callbackChannel = Channel(Channel.UNLIMITED)

  logEnvVarValues { logger.info { it } }

  startCallbackThread(config)
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

    intercept(ApplicationCallPipeline.Call) {
      // println("Request received: ${call.request.uri}")
      proceed()
      // println("Response sent with status: ${call.response.status()}")
    }

    routing {
      staticResources(STATIC_BASE, "core_static")

      get(PING_PATH) { call.respondText("pong") }

      get(VERSION_PATH) {
        call.respondText(ContentType.Application.Json) { Vapi4kServer::class.versionDesc(true) }
      }

      if (!isProduction) {
        get("/") { call.respondRedirect(VALIDATE_PATH) }

        route(ENV_PATH) {
          installContentNegotiation {
            prettyPrint = true
            prettyPrintIndent = "  "
          }
          get {
            call.respond<JsonObject>(jsonEnvVarValues())
          }
        }
        route(METRICS_PATH) {
          installContentNegotiation()
          get { call.respond(appMicrometerRegistry.scrape()) }
        }
        route(CACHES_PATH) {
          installContentNegotiation()
          get { cachesRequest(config) }
        }
        route(CLEAR_CACHES_PATH) {
          installContentNegotiation()
          get { clearCaches(config) }
        }
        get(VALIDATE_PATH) { validateRootPage(config) }
        get("$VALIDATE_PATH/{appType}/{appName}") { validateApplication(config) }
        get(VALIDATE_INVOKE_TOOL_PATH) { validateToolInvokeRequest(config) }
      }

      // Process Inbound Call requests
      config.inboundCallApplications.forEach { app ->
        val path = "/${app.fullServerPath}"
        route(path) {
          logger.info { """Adding POST endpoint "$path" for ${app.applicationType.displayName} """ }
          installContentNegotiation()
          get { call.respondText("${this@route.parent} requires a post request", status = MethodNotAllowed) }
          post {
            val request = call.receive<String>().toJsonElement()
            val sessionId = call.getSessionIdFromQueryParameters() ?: INBOUND_CALL.defaultSessionId().toSessionId()
            val assistantId = call.getAssistantIdFromQueryParameters() ?: EMPTY_ASSISTANT_ID
            inboundCallRequest(config, app as InboundCallApplicationImpl, request, sessionId, assistantId)
          }
        }
      }

      // Process Outbound Call and Web requests
      (config.outboundCallApplications + config.webApplications)
        .forEach { app ->
          val path = "/${app.fullServerPath}"
          route(path) {
            installContentNegotiation()
            logger.info { """Adding GET  endpoint "$path" for ${app.applicationType.displayName}""" }
            get {
              val request = buildJsonObject { addArgsAndMessage(call) }
              val sessionId = call.getSessionIdFromQueryParameters() ?: error("Missing sessionId in query parameters")
              val assistantId = call.getAssistantIdFromQueryParameters() ?: EMPTY_ASSISTANT_ID
              outboundCallAndWebRequest(config, app, request, sessionId, assistantId)
            }
            logger.info { """Adding POST endpoint "$path" for ${app.applicationType.displayName}""" }
            post {
              val json = call.receive<String>().toJsonElement()
              val request =
                if (json.isNotEmpty() && json.containsKey("message.type")) {
                  json
                } else {
                  buildJsonObject {
                    // Add values from the JSON object passed in with the POST request
                    put(
                      POST_ARGS,
                      buildJsonObject {
                        if (json.isNotEmpty()) {
                          json.keys.forEach { key ->
                            put(key, json.getOrNull(key)?.toJsonElement() ?: JsonPrimitive(""))
                          }
                        }
                      },
                    )
                    addArgsAndMessage(call)
                  }
                }
              val sessionId = call.getSessionIdFromQueryParameters() ?: error("No session ID found in query parameters")
              val assistantId = call.getAssistantIdFromQueryParameters() ?: EMPTY_ASSISTANT_ID
              outboundCallAndWebRequest(config, app, request, sessionId, assistantId)
            }
          }
        }
    }
  }
}
