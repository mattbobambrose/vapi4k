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

import com.vapi4k.Vapi4k.RequestResponseCallback
import com.vapi4k.Vapi4k.invokeRequestCallbacks
import com.vapi4k.Vapi4k.invokeResponseCallbacks
import com.vapi4k.Vapi4k.isValidSecret
import com.vapi4k.Vapi4k.logger
import com.vapi4k.Vapi4k.startCallbackThread
import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.dsl.vapi4k.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.dsl.vapi4k.ServerRequestType.Companion.isToolCall
import com.vapi4k.dsl.vapi4k.ServerRequestType.FUNCTION_CALL
import com.vapi4k.dsl.vapi4k.ServerRequestType.TOOL_CALL
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.responses.AssistantRequestResponse.Companion.getAssistantResponse
import com.vapi4k.responses.FunctionResponse.Companion.getFunctionCallResponse
import com.vapi4k.responses.SimpleMessageResponse
import com.vapi4k.responses.ToolCallResponse.Companion.getToolCallResponse
import com.vapi4k.utils.JsonElementUtils.requestType
import com.vapi4k.utils.JsonUtils.toJsonElement
import com.vapi4k.utils.Utils.lambda
import io.ktor.http.HttpStatusCode
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
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import kotlin.time.measureTimedValue

val Vapi4k: ApplicationPlugin<Vapi4kConfig> = createApplicationPlugin(
  name = "Vapi4k",
  createConfiguration = { Vapi4kConfig() },
) {
  val requestResponseCallbackChannel = Channel<RequestResponseCallback>(Channel.UNLIMITED)

  startCallbackThread(requestResponseCallbackChannel)

  environment?.monitor?.apply {
    subscribe(ApplicationStarting) { it.environment.log.info("Vapi4kServer is starting") }
    subscribe(ApplicationStarted) { it.environment.log.info("Vapi4kServer is started") }
    subscribe(ApplicationStopped) { it.environment.log.info("Vapi4kServer is stopped") }
    subscribe(ApplicationStopping) { it.environment.log.info("Vapi4kServer is stopping") }
  }

  application.routing {
    val config = Assistant.config

    get("/") { call.respondText("Hello World!") }
    get("/ping") { call.respondText("pong") }

    val serverPath = config.configProperties.serverUrlPath
    logger.info { "Adding POST serverUrl endpoint at $serverPath" }
    post(serverPath) {
      if (isValidSecret(config.configProperties.serverUrlSecret)) {
        val json = call.receive<String>()
        val request = Json.parseToJsonElement(json)
        val requestType = request.requestType

        invokeRequestCallbacks(requestResponseCallbackChannel, requestType, request)

        val (response, duration) = measureTimedValue {
          when (requestType) {
            ASSISTANT_REQUEST -> {
              val response = getAssistantResponse(request)
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
          val requestType = request.requestType

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
