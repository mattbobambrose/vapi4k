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

package com.vapi4k.api.vapi4k

import com.vapi4k.api.vapi4k.enums.ServerRequestType
import com.vapi4k.common.ApplicationId
import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.vapi4k.Vapi4KDslMarker
import io.ktor.server.config.ApplicationConfig
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

typealias RequestArgs = suspend (JsonElement) -> Unit
typealias ResponseArgs = suspend (requestType: ServerRequestType, JsonElement, Duration) -> Unit

@Vapi4KDslMarker
class Vapi4kConfig internal constructor() {
  init {
    AssistantImpl.config = this
  }

  internal lateinit var applicationConfig: ApplicationConfig
  internal val applications = mutableListOf<Vapi4kApplication>()
  internal val globalAllRequests = mutableListOf<(RequestArgs)>()
  internal val globalPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val globalAllResponses = mutableListOf<ResponseArgs>()
  internal val globalPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  fun vapi4kApplication(block: Vapi4kApplication.() -> Unit): Vapi4kApplication {
    val application = Vapi4kApplication().apply(block)
    if (applications.any { it.serverPath == application.serverPath })
      error("vapi4kApplication{} with serverPath \"${application.serverPath}\" already exists")
    applications += application
    return application
  }

  fun onAllRequests(block: suspend (request: JsonElement) -> Unit) {
    globalAllRequests += block
  }

  fun onRequest(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement) -> Unit,
  ) {
    globalPerRequests += requestType to block
    requestTypes.forEach { globalPerRequests += it to block }
  }

  fun onAllResponses(
    block: suspend (requestType: ServerRequestType, response: JsonElement, elapsed: Duration) -> Unit,
  ) {
    globalAllResponses += block
  }

  fun onResponse(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (requestType: ServerRequestType, request: JsonElement, elapsed: Duration) -> Unit,
  ) {
    globalPerResponses += requestType to block
    requestTypes.forEach { globalPerResponses += it to block }
  }

  internal fun getApplication(applicationId: ApplicationId): Vapi4kApplication =
    applications.firstOrNull { it.applicationId == applicationId }
      ?: error("Application not found for applicationId: $applicationId")

  internal fun getApplication(serverPath: String): Vapi4kApplication =
    applications.firstOrNull { it.serverPath == serverPath }
      ?: error("Application with serverPath=$serverPath not found")
}
