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

package com.vapi4k.dsl.vapi4k

import com.vapi4k.dsl.assistant.AssistantImpl
import com.vapi4k.dsl.vapi4k.enums.ServerRequestType
import io.ktor.server.config.ApplicationConfig
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

typealias RequestArgs = suspend (JsonElement) -> Unit
typealias ResponseArgs = suspend (requestType: ServerRequestType, JsonElement, Duration) -> Unit

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class Vapi4KDslMarker

@Vapi4KDslMarker
class Vapi4kConfig internal constructor() {
  init {
    AssistantImpl.config = this
  }

  internal lateinit var applicationConfig: ApplicationConfig

  internal val configProperties: Vapi4kConfigProperties = Vapi4kConfigProperties()
  internal val applications = mutableListOf<Vapi4kApplication>()

  internal var globalAllRequests = mutableListOf<(RequestArgs)>()
  internal val globalPerRequests = mutableListOf<Pair<ServerRequestType, RequestArgs>>()
  internal val globalAllResponses = mutableListOf<ResponseArgs>()
  internal val globalPerResponses = mutableListOf<Pair<ServerRequestType, ResponseArgs>>()

  fun vapi4kApplication(block: Vapi4kApplication.() -> Unit) {
    applications += Vapi4kApplication().apply(block)
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
}
