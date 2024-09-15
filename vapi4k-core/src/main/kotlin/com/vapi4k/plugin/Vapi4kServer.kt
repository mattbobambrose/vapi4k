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
import com.vapi4k.common.Constants.APP_NAME
import com.vapi4k.common.Constants.APP_TYPE
import com.vapi4k.common.Constants.BS_BASE
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CoreEnvVars.isProduction
import com.vapi4k.common.CoreEnvVars.loadCoreEnvVars
import com.vapi4k.common.CoreEnvVars.vapi4kBaseUrl
import com.vapi4k.common.CssNames.LOG_DIV
import com.vapi4k.common.Endpoints.ADMIN_CONSOLE_ENDPOINT
import com.vapi4k.common.Endpoints.ADMIN_ENV_PATH
import com.vapi4k.common.Endpoints.ADMIN_PATH
import com.vapi4k.common.Endpoints.ADMIN_VERSION_PATH
import com.vapi4k.common.Endpoints.CACHES_PATH
import com.vapi4k.common.Endpoints.CLEAR_CACHES_PATH
import com.vapi4k.common.Endpoints.CONSOLE_PATH
import com.vapi4k.common.Endpoints.ENV_PATH
import com.vapi4k.common.Endpoints.INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.METRICS_PATH
import com.vapi4k.common.Endpoints.PING_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.Endpoints.VERSION_PATH
import com.vapi4k.common.Version
import com.vapi4k.common.Version.Companion.versionDesc
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.pages.AdminPage.adminPage
import com.vapi4k.pages.BootstrapPage2.bootstrapPage2
import com.vapi4k.pages.ConsolePage.consolePage
import com.vapi4k.pages.InvokeTool.invokeTool
import com.vapi4k.pages.ValidateApplication.appEnvVars
import com.vapi4k.pages.ValidateApplication.systemInfo
import com.vapi4k.pages.ValidateApplication.validateApplication
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.server.AdminJobs.startCacheCleaningThread
import com.vapi4k.server.AdminJobs.startCallbackThread
import com.vapi4k.server.CacheActions.cachesRequest
import com.vapi4k.server.CacheActions.clearCaches
import com.vapi4k.server.InboundCallActions.inboundCallRequest
import com.vapi4k.server.OutboundCallAndWebActions.addArgsAndMessage
import com.vapi4k.server.OutboundCallAndWebActions.buildRequestArg
import com.vapi4k.server.OutboundCallAndWebActions.outboundCallAndWebRequest
import com.vapi4k.server.defaultKtorConfig
import com.vapi4k.server.installContentNegotiation
import com.vapi4k.utils.HtmlUtils.html
import com.vapi4k.utils.MiscUtils.getBanner
import com.vapi4k.utils.MiscUtils.removeEnds
import com.vapi4k.utils.SharedDataLoader
import com.vapi4k.utils.envvar.EnvVar.Companion.jsonEnvVarValues
import com.vapi4k.utils.envvar.EnvVar.Companion.logEnvVarValues
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Version(
  version = BuildConfig.VERSION,
  releaseDate = BuildConfig.RELEASE_DATE,
  buildTime = BuildConfig.BUILD_TIME,
)
object Vapi4kServer {
  val logger = KotlinLogging.logger {}
}

val Vapi4k: ApplicationPlugin<Vapi4kConfig> =
  createApplicationPlugin(
    name = "Vapi4k",
    createConfiguration = { Vapi4kConfigImpl() },
  ) {
    loadCoreEnvVars()

    val config =
      Vapi4kConfigImpl.config.apply {
        applicationConfig = environment?.config ?: error("No environment config found")
        callbackChannel = Channel(Channel.UNLIMITED)
      }

    with(logger) {
      info { getBanner("banners/vapi4k-server.banner", logger) }
      info { Vapi4kServer::class.versionDesc() }
    }

    logEnvVarValues { logger.info { it } }

    startCallbackThread(config)
    startCacheCleaningThread(config)

    environment?.monitor?.apply {
      val name = Vapi4kServer::class.simpleName
      subscribe(ApplicationStarting) { it.environment.log.info("$name is starting") }
      subscribe(ApplicationStarted) { it.environment.log.info("$name is started at $vapi4kBaseUrl") }
      subscribe(ApplicationStopped) { it.environment.log.info("$name is stopped") }
      subscribe(ApplicationStopping) { it.environment.log.info("$name is stopping") }
    }

    with(application) {
      val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
      defaultKtorConfig(appMicrometerRegistry)

      routing {
        staticResources(STATIC_BASE, "core_static")
        staticResources(BS_BASE, "bootstrap")

        get(PING_PATH) { call.respondText("pong") }

        get(VERSION_PATH) { call.respondText(Application.Json) { Vapi4kServer::class.versionDesc(true) } }

        get("/bootstrap") { call.respondHtml { bootstrapPage2() } }

        if (!isProduction) {
          get("/") { call.respondRedirect(ADMIN_PATH) }

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

          get(ADMIN_PATH) { call.respondHtml { adminPage(config) } }

          get(CONSOLE_PATH) { call.respondHtml { consolePage(config) } }

          get(ADMIN_ENV_PATH) { call.respondText(appEnvVars()) }

          get(ADMIN_VERSION_PATH) { call.respondText(systemInfo()) }

          get("$VALIDATE_PATH/{$APP_TYPE}/{$APP_NAME}") { call.respondText(validateApplication(config)) }

          get(INVOKE_TOOL_PATH) { call.respondText(invokeTool(config)) }

          webSocket(ADMIN_CONSOLE_ENDPOINT) {
            coroutineScope {
              launch {
                // Clear div on client after a server restart
                val reset =
                  html {
                    div {
                      attributes["hx-swap-oob"] = "innerHTML:#$LOG_DIV"
                      +""
                    }
                  }
                outgoing.send(Frame.Text(reset))

                SharedDataLoader.accessSharedFlow()
                  .onStart { logger.info { "Starting to listen for console updates" } }
                  .onEach { msg ->
                    val s = if (msg.isBlank()) msg else msg.removeEnds("\n")
                    s.split("\n").forEach { line ->
                      val html =
                        html {
                          div {
                            attributes["hx-swap-oob"] = "beforeend:#$LOG_DIV"
                            +"$line\n"
                          }
                        }
                      outgoing.send(Frame.Text(html))
                    }
                  }
                  .collect()
              }

              launch {
                for (frame in incoming) {
                  if (frame is Frame.Text) {
                    val text = frame.readText()
                    // println("Received: $text")
                  }
                }
              }
            }
          }
        }

        // Process Inbound Call requests
        config.inboundCallApplications.forEach { application ->
          val path = "/${application.fullServerPath}"
          route(path) {
            logger.info { """Adding POST endpoint "$path" for ${application.applicationType.displayName} """ }
            installContentNegotiation()
            get { call.respondText("${this@route.parent} requires a post request", status = MethodNotAllowed) }
            post { inboundCallRequest(config, application) }
          }
        }

        // Process Outbound Call and Web requests
        (config.outboundCallApplications + config.webApplications)
          .forEach { application ->
            val path = "/${application.fullServerPath}"
            route(path) {
              installContentNegotiation()
              logger.info { """Adding GET and POST endpoints "$path" for ${application.applicationType.displayName}""" }
              get {
                val request = buildJsonObject { addArgsAndMessage(call) }
                outboundCallAndWebRequest(config, application, request)
              }
              post {
                val json = call.receive<String>().toJsonElement()
                val request = buildRequestArg(json)
                outboundCallAndWebRequest(config, application, request)
              }
            }
          }
      }

      intercept(ApplicationCallPipeline.Call) {
        proceed()
        if (call.response.status() == HttpStatusCode.NotFound) {
          // See if user forgot the inboundCall prefix in the path
          val path = call.request.path().removeEnds("/")
          val match = config.inboundCallApplications.any { application ->
            application.serverPathNoSlash == path
          }
          if (match)
            logger.info {
              "/$path is a valid inboundCallApplication{} serverPath value. " +
                "Did you mean to use \"/inboundCall/$path\" instead of \"/$path\" as the Vapi Server URL in the Vapi dashboard?"
            }
        }
      }
    }
  }
