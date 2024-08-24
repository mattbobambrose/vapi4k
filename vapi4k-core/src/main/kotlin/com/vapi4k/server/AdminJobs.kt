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

import com.vapi4k.common.ApplicationId
import com.vapi4k.common.CoreEnvVars.TOOL_CACHE_CLEAN_PAUSE_MINS
import com.vapi4k.common.CoreEnvVars.TOOL_CACHE_MAX_AGE_MINS
import com.vapi4k.dsl.vapi4k.RequestResponseType
import com.vapi4k.dsl.vapi4k.RequestResponseType.REQUEST
import com.vapi4k.dsl.vapi4k.RequestResponseType.RESPONSE
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.server.AdminJobs.RequestResponseCallback.Companion.requestCallback
import com.vapi4k.server.AdminJobs.RequestResponseCallback.Companion.responseCallback
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.JsonElementUtils.emptyJsonElement
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.enums.ServerRequestType
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
            logger.debug { "Purging cache for ${application.serverPath}" }
            with(application) {
              serviceToolCache.purgeToolCache(maxAge)
              functionCache.purgeToolCache(maxAge)
            }
          }
        }.onFailure { e ->
          logger.error(e) { "Error clearing cache: ${e.errorMsg}" }
        }
      }
    }
  }

  fun startCallbackThread(config: Vapi4kConfigImpl) {
    thread {
      while (true) {
        runCatching {
          runBlocking {
            for (callback in config.callbackChannel) {
              coroutineScope {
                when (callback.type) {
                  REQUEST -> {
                    config.allApplications
                      .filter { it.applicationId == callback.applicationId }
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
    config: Vapi4kConfigImpl,
    applicationId: ApplicationId,
    requestType: ServerRequestType,
    request: JsonElement,
  ) = config.callbackChannel.send(requestCallback(applicationId, requestType, request))

  suspend fun invokeResponseCallbacks(
    config: Vapi4kConfigImpl,
    applicationId: ApplicationId,
    requestType: ServerRequestType,
    response: () -> JsonElement,
    elapsed: Duration,
  ) = config.callbackChannel.send(responseCallback(applicationId, requestType, response, elapsed))

  data class RequestResponseCallback(
    val applicationId: ApplicationId,
    val type: RequestResponseType,
    val requestType: ServerRequestType,
    val request: JsonElement = emptyJsonElement(),
    val response: (() -> JsonElement) = { emptyJsonElement() },
    val elapsed: Duration = Duration.ZERO,
  ) {
    companion object {
      fun requestCallback(
        applicationId: ApplicationId,
        requestType: ServerRequestType,
        request: JsonElement,
      ) = RequestResponseCallback(applicationId, REQUEST, requestType, request)

      fun responseCallback(
        applicationId: ApplicationId,
        requestType: ServerRequestType,
        response: () -> JsonElement,
        elapsed: Duration,
      ) = RequestResponseCallback(applicationId, RESPONSE, requestType, response = response, elapsed = elapsed)
    }
  }
}
