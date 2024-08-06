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

import com.vapi4k.api.vapi4k.enums.ServerRequestType
import com.vapi4k.common.EnvVar.TOOL_CACHE_CLEAN_PAUSE_MINS
import com.vapi4k.common.EnvVar.TOOL_CACHE_MAX_AGE_MINS
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.vapi4k.RequestResponseType
import com.vapi4k.dsl.vapi4k.RequestResponseType.REQUEST
import com.vapi4k.dsl.vapi4k.RequestResponseType.RESPONSE
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.server.AdminJobs.RequestResponseCallback.Companion.requestCallback
import com.vapi4k.server.AdminJobs.RequestResponseCallback.Companion.responseCallback
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.JsonElementUtils.emptyJsonElement
import com.vapi4k.utils.Utils.errorMsg
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal object AdminJobs {
  fun startCacheCleaningThread(config: Vapi4kConfigImpl) {
    thread {
      val pause = TOOL_CACHE_CLEAN_PAUSE_MINS.toInt().minutes
      val maxAge = TOOL_CACHE_MAX_AGE_MINS.toInt().minutes
      while (true) {
        runCatching {
          Thread.sleep(pause.inWholeMilliseconds)
          config.allApplications.forEach { application ->
            logger.info { "Purging cache for ${application.serverPath}" }
            application.toolCache.purgeToolCache(maxAge)
          }
        }.onFailure { e ->
          logger.error(e) { "Error clearing cache: ${e.errorMsg}" }
        }
      }
    }
  }

  fun startCallbackThread(callbackChannel: Channel<RequestResponseCallback>) {
    thread {
      val config = AssistantImpl.config
      while (true) {
        runCatching {
          runBlocking {
            for (callback in callbackChannel) {
              coroutineScope {
                when (callback.type) {
                  REQUEST -> {
                    config.allApplications
                      .filter { it == callback.application }
                      .forEach { application ->
                        with(application) {
                          applicationAllRequests.forEach { launch { it.invoke(callback.request) } }
                          applicationPerRequests
                            .filter { it.first == callback.requestType }
                            .forEach { (_, block) -> launch { block(callback.request) } }
                        }
                      }
                    with(config) {
                      globalAllRequests.forEach { launch { it.invoke(callback.request) } }
                      globalPerRequests
                        .filter { it.first == callback.requestType }
                        .forEach { (_, block) -> launch { block(callback.request) } }
                    }
                  }

                  RESPONSE -> {
                    config.allApplications.forEach { application ->
                      with(application) {
                        if (applicationAllResponses.isNotEmpty() || applicationPerResponses.isNotEmpty()) {
                          val resp =
                            runCatching {
                              callback.response.invoke()
                            }.onFailure { e ->
                              logger.error { "Error creating response" }
                              error("Error creating response")
                            }.getOrThrow()

                          applicationAllResponses.forEach {
                            launch {
                              it.invoke(callback.requestType, resp, callback.elapsed)
                            }
                          }
                          applicationPerResponses
                            .filter { it.first == callback.requestType }
                            .forEach { (reqType, block) ->
                              launch { block(reqType, resp, callback.elapsed) }
                            }
                        }
                      }
                    }
                    with(config) {
                      if (globalAllResponses.isNotEmpty() || globalPerResponses.isNotEmpty()) {
                        val resp =
                          runCatching {
                            callback.response.invoke()
                          }.onFailure { e ->
                            logger.error { "Error creating response" }
                            error("Error creating response")
                          }.getOrThrow()

                        globalAllResponses.forEach {
                          launch {
                            it.invoke(
                              callback.requestType,
                              resp,
                              callback.elapsed,
                            )
                          }
                        }
                        globalPerResponses
                          .filter { it.first == callback.requestType }
                          .forEach { (reqType, block) ->
                            launch { block(reqType, resp, callback.elapsed) }
                          }
                      }
                    }
                  }
                }
              }
            }
          }
        }.onFailure { e ->
          logger.error(e) { "Error processing request response callback: ${e.errorMsg}" }
        }
      }
    }
  }

  suspend fun invokeRequestCallbacks(
    application: Vapi4kApplicationImpl,
    channel: Channel<RequestResponseCallback>,
    requestType: ServerRequestType,
    request: JsonElement,
  ) = channel.send(requestCallback(application, requestType, request))

  suspend fun invokeResponseCallbacks(
    application: Vapi4kApplicationImpl,
    channel: Channel<RequestResponseCallback>,
    requestType: ServerRequestType,
    response: () -> JsonElement,
    elapsed: Duration,
  ) = channel.send(responseCallback(application, requestType, response, elapsed))

  data class RequestResponseCallback(
    val application: Vapi4kApplicationImpl,
    val type: RequestResponseType,
    val requestType: ServerRequestType,
    val request: JsonElement = emptyJsonElement(),
    val response: (() -> JsonElement) = { emptyJsonElement() },
    val elapsed: Duration = Duration.ZERO,
  ) {
    companion object {
      fun requestCallback(
        application: Vapi4kApplicationImpl,
        requestType: ServerRequestType,
        request: JsonElement,
      ) = RequestResponseCallback(application, REQUEST, requestType, request)

      fun responseCallback(
        application: Vapi4kApplicationImpl,
        requestType: ServerRequestType,
        response: () -> JsonElement,
        elapsed: Duration,
      ) = RequestResponseCallback(application, RESPONSE, requestType, response = response, elapsed = elapsed)
    }
  }
}
