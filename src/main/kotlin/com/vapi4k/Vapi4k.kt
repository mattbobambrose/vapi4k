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

package com.vapi4k

import com.vapi4k.Vapi4k.RequestResponseCallback.Companion.requestCallback
import com.vapi4k.Vapi4k.RequestResponseCallback.Companion.responseCallback
import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.dsl.vapi4k.RequestResponseType
import com.vapi4k.dsl.vapi4k.RequestResponseType.REQUEST
import com.vapi4k.dsl.vapi4k.RequestResponseType.RESPONSE
import com.vapi4k.dsl.vapi4k.ServerRequestType
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlin.concurrent.thread
import kotlin.time.Duration

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class Vapi4KDslMarker

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class AssistantDslMarker

object Vapi4k {
  val logger = KotlinLogging.logger {}

  internal fun startCallbackThread(callbackChannel: Channel<RequestResponseCallback>) {
    thread {
      val config = Assistant.config
      while (true) {
        runCatching {
          runBlocking {
            for (callback in callbackChannel) {
              coroutineScope {
                with(callback) {
                  when (callback.type) {
                    REQUEST ->
                      with(config) {
                        allRequests.forEach { launch { it.invoke(request!!) } }
                        perRequests
                          .filter { it.first == requestType }
                          .forEach { (reqType, block) ->
                            launch { block(request!!) }
                          }
                      }

                    RESPONSE ->
                      with(config) {
                        if (allResponses.isNotEmpty() || perResponses.isNotEmpty()) {
                          val resp =
                            runCatching {
                              response!!.invoke()
                            }.onFailure { e ->
                              logger.error(e) { "Error creating response" }
                              error("Error creating response")
                            }.getOrThrow()

                          allResponses.forEach { launch { it.invoke(requestType, resp, elapsed) } }
                          perResponses
                            .filter { it.first == requestType }
                            .forEach { (reqType, block) ->
                              launch { block(reqType, resp, elapsed) }
                            }
                        }
                      }
                  }
                }
              }
            }
          }
        }.onFailure {
          logger.error(it) { "Error processing request response callback: ${it.message}" }
        }
      }
    }
  }

  internal data class RequestResponseCallback(
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

  internal suspend fun PipelineContext<Unit, ApplicationCall>.isValidSecret(
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

  internal suspend fun invokeRequestCallbacks(
    channel: Channel<RequestResponseCallback>,
    requestType: ServerRequestType,
    request: JsonElement,
  ) = channel.send(requestCallback(requestType, request))

  internal suspend fun invokeResponseCallbacks(
    channel: Channel<RequestResponseCallback>,
    requestType: ServerRequestType,
    response: () -> JsonElement,
    elapsed: Duration,
  ) = channel.send(responseCallback(requestType, response, elapsed))
}
