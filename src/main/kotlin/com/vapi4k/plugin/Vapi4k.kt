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

import com.vapi4k.dsl.vapi4k.ServerRequestType
import com.vapi4k.dsl.vapi4k.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.dsl.vapi4k.ServerRequestType.Companion.isToolCall
import com.vapi4k.dsl.vapi4k.ServerRequestType.FUNCTION_CALL
import com.vapi4k.dsl.vapi4k.ServerRequestType.TOOL_CALL
import com.vapi4k.plugin.RequestResponseCallback.Companion.requestCallback
import com.vapi4k.plugin.RequestResponseCallback.Companion.responseCallback
import com.vapi4k.plugin.RequestResponseType.REQUEST
import com.vapi4k.plugin.RequestResponseType.RESPONSE
import com.vapi4k.plugin.Vapi4kPlugin.logger
import com.vapi4k.responses.AssistantRequestResponse.Companion.getAssistantResponse
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponse.Companion.getToolCallResponse
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import com.vapi4k.utils.JsonUtils.toJsonElement
import com.vapi4k.utils.Utils.lambda
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.measureTimedValue

object Vapi4kPlugin {
  val logger = KotlinLogging.logger {}
}

val Vapi4k: ApplicationPlugin<Vapi4kConfig> = createApplicationPlugin(
  name = "Vapi4k",
  createConfiguration = { Vapi4kConfig() },
) {
  val config = pluginConfig
  val requestResponseCallbackChannel = Channel<RequestResponseCallback>(Channel.UNLIMITED)

  startRequestCallbackThread(requestResponseCallbackChannel, config)

  environment?.monitor?.apply {
    subscribe(ApplicationStarting) { it.environment.log.info("Vapi4kServer is starting") }
    subscribe(ApplicationStarted) { it.environment.log.info("Vapi4kServer is started") }
    subscribe(ApplicationStopped) { it.environment.log.info("Vapi4kServer is stopped") }
    subscribe(ApplicationStopping) { it.environment.log.info("Vapi4kServer is stopping") }
  }

  application.routing {
    get("/") { call.respondText("Hello World!") }
    get("/ping") { call.respondText("pong") }

    val serverPath = config.configProperties.serverUrlPath
    logger.info { "Adding POST serverUrl endpoint at $serverPath" }
    post(serverPath) {
      if (isValidSecret(config.configProperties.serverUrlSecret)) {
        val json = call.receive<String>()
        val request = Json.parseToJsonElement(json)
        val requestType = ServerRequestType.fromString(request["message.type"].stringValue)

        invokeRequestCallbacks(requestResponseCallbackChannel, requestType, request)

        val (response, duration) = measureTimedValue {
          when (requestType) {
            ASSISTANT_REQUEST -> {
              val response = getAssistantResponse(config, request)
              call.respond(response)
              lambda { response.toJsonElement() }
            }

            FUNCTION_CALL -> {
              val response = getFunctionCallResponse(request)
              call.respond(response)
              lambda { response.toJsonElement() }
            }

            TOOL_CALL -> {
              val response = getToolCallResponse(request)
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

        invokeResponseCallbacks(requestResponseCallbackChannel, requestType, response, duration)
      }
    }

    config.toolCallEndpoints.forEach { endpoint ->
      val toolCallPath = endpoint.path
      logger.info { "Adding POST toolCall endpoint ${endpoint.name}: ${endpoint.path}" }
      post(toolCallPath) {
        if (isValidSecret(endpoint.secret)) {
          val json = call.receive<String>()
          val request = Json.parseToJsonElement(json)
          val requestType = ServerRequestType.fromString(request["message.type"].stringValue)

          invokeRequestCallbacks(requestResponseCallbackChannel, requestType, request)

          if (requestType.isToolCall) {
            call.respond(HttpStatusCode.BadRequest, "Invalid message type: requires ToolCallRequest")
          } else {
            val (response, duration) = measureTimedValue {
              val response = getToolCallResponse(request)
              call.respond(response)
              lambda { response.toJsonElement() }
            }
            invokeResponseCallbacks(requestResponseCallbackChannel, requestType, response, duration)
          }
        }
      }
    }
  }
}

fun startRequestCallbackThread(
  requestResponseCallbackChannel: Channel<RequestResponseCallback>,
  config: Vapi4kConfig,
) {
  thread {
    while (true) {
      runCatching {
        runBlocking {
          for (callback in requestResponseCallbackChannel) {
            coroutineScope {
              with(callback) {
                when (callback.type) {
                  REQUEST -> {
                    config.allRequests.forEach { launch { it.invoke(requestType, request!!) } }
                    config.perRequests
                      .filter { it.first == requestType }
                      .forEach { (_, block) ->
                        launch { block(request!!) }
                      }
                  }

                  RESPONSE -> {
                    if (config.allResponses.isNotEmpty() || config.perResponses.isNotEmpty()) {
                      val resp = try {
                        response!!.invoke()
                      } catch (e: Exception) {
                        logger.error(e) { "Error creating response" }
                        error("Error creating response")
                      }
                      config.allResponses.forEach { launch { it.invoke(requestType, resp, elapsed) } }
                      config.perResponses
                        .filter { it.first == requestType }
                        .forEach { (_, block) ->
                          launch { block(resp, elapsed) }
                        }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.isValidSecret(
  configPropertiesSecret: String,
): Boolean {
  val secret = call.request.headers["x-vapi-secret"]
  return if (configPropertiesSecret.isNotEmpty() && secret != configPropertiesSecret) {
    logger.info { "Invalid secret: [$secret] [$configPropertiesSecret]" }
    call.respond(HttpStatusCode.Forbidden, "Invalid secret")
    false
  } else {
    true
  }
}

data class RequestResponseCallback(
  val type: RequestResponseType,
  val requestType: ServerRequestType,
  val request: JsonElement? = null,
  val response: (() -> JsonElement)? = null,
  val elapsed: Duration = Duration.ZERO,
) {
  companion object {
    fun requestCallback(
      requestType: ServerRequestType,
      request: JsonElement,
    ) = RequestResponseCallback(REQUEST, requestType, request)

    fun responseCallback(
      requestType: ServerRequestType,
      response: () -> JsonElement,
      elapsed: Duration,
    ) = RequestResponseCallback(RESPONSE, requestType, response = response, elapsed = elapsed)
  }
}

enum class RequestResponseType {
  REQUEST,
  RESPONSE
}

private suspend fun invokeRequestCallbacks(
  channel: Channel<RequestResponseCallback>,
  requestType: ServerRequestType,
  request: JsonElement,
) = channel.send(requestCallback(requestType, request))

private suspend fun invokeResponseCallbacks(
  channel: Channel<RequestResponseCallback>,
  requestType: ServerRequestType,
  response: () -> JsonElement,
  elapsed: Duration,
) = channel.send(responseCallback(requestType, response, elapsed))
