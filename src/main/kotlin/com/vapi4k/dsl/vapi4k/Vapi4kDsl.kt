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

import com.vapi4k.dsl.vapi4k.Vapi4kDsl.logger
import com.vapi4k.plugin.Vapi4kConfig
import com.vapi4k.responses.AssistantRequestResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class Vapi4KDslMarker

object Vapi4kDsl {
  val logger = KotlinLogging.logger {}

  fun Vapi4kConfig.configure(block: Vapi4kConfigProperties.() -> Unit) {
    configProperties.apply(block)
  }

  fun ToolCallEndpoints.endpoint(block: Endpoint.() -> Unit) {
    config.toolCallEndpoints += Endpoint().apply(block).also { endpoint ->
      when {
        hasName(endpoint) && endpoint.name.isEmpty() -> error("Duplicate blank endpoint names")
        hasName(endpoint) -> error("Duplicate endpoint name: ${endpoint.name}")
        hasUrl(endpoint) -> error("Duplicate endpoint url: ${endpoint.url}")
      }
    }
  }

  fun Vapi4kConfig.toolCallEndpoints(block: ToolCallEndpoints.() -> Unit) {
    ToolCallEndpoints(this).apply(block)
  }

  fun Vapi4kConfig.onAssistantRequest(
    block: suspend (config: Vapi4kConfig, request: JsonElement) -> AssistantRequestResponse,
  ) {
    if (assistantRequest == null) assistantRequest = block
    else error("onAssistantRequest{} can be called only once")
  }

  fun Vapi4kConfig.onAllRequests(block: suspend (requestType: ServerRequestType, request: JsonElement) -> Unit) {
    allRequests += block
  }

  fun Vapi4kConfig.onRequest(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement) -> Unit,
  ) {
    perRequests += requestType to block
    requestTypes.forEach { perRequests += it to block }
  }

  fun Vapi4kConfig.onAllResponses(
    block: suspend (requestType: ServerRequestType, response: JsonElement, elapsed: Duration) -> Unit,
  ) {
    allResponses += block
  }

  fun Vapi4kConfig.onResponse(
    requestType: ServerRequestType,
    vararg requestTypes: ServerRequestType,
    block: suspend (request: JsonElement, elapsed: Duration) -> Unit,
  ) {
    perResponses += requestType to block
    requestTypes.forEach { perResponses += it to block }
  }
}

enum class ServerRequestType(val desc: String) {
  ASSISTANT_REQUEST("assistant-request"),
  CONVERSATION_UPDATE("conversation-update"),
  END_OF_CALL_REPORT("end-of-call-report"),
  FUNCTION_CALL("function-call"),
  HANG("hang"),
  PHONE_CALL_CONTROL("phone-call-control"),
  SPEECH_UPDATE("speech-update"),
  STATUS_UPDATE("status-update"),
  TOOL_CALL("tool-calls"),
  TRANSCRIPT("transcript"),
  TRANSFER_DESTINATION_REQUEST("transfer-destination-request"),
  USER_INTERRUPTED("user-interrupted"),
  UNKNOWN("unknown");

  companion object {
    fun fromString(desc: String) =
      try {
        entries.first { it.desc == desc }
      } catch (e: Exception) {
        logger.error(e) { "Invalid ServerMessageType: $desc" }
        UNKNOWN
      }
  }
}
