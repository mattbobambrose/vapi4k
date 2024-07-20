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

package com.vapi4k.dsl.assistant.enums

import com.vapi4k.dsl.assistant.Assistant
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantId
import com.vapi4k.responses.CallRequest
import com.vapi4k.responses.CallUnion
import com.vapi4k.responses.CustomerDto
import com.vapi4k.responses.CustomerUnion
import com.vapi4k.utils.JsonElementUtils.emptyJsonElement
import com.vapi4k.utils.JsonUtils.toJsonString
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

@AssistantDslMarker
class Vapi4kApi(val authString: String) {
  fun make(block: Vapi4kApi.() -> CallRequest) {
    val callRequest = block()
    val json = callRequest.toJsonString(true)
    println(json)
    runBlocking {
      val client = HttpClient(CIO) {
        install(ContentNegotiation) {
          json(
            Json {
              prettyPrint = true
            },
          )
        }

        install(ContentEncoding) {
          deflate(1.0F)
          gzip(0.9F)
        }
      }
      val response = client.post("https://api.vapi.ai/call/phone") {
        contentType(ContentType.Application.Json)
        bearerAuth(authString)
        setBody(callRequest)
      }
      println(response.status)
      println(response.bodyAsText())
    }
  }

  fun call(block: Call.() -> Unit): CallRequest {
    val callRequest = CallRequest()
    val call = Call(callRequest).apply(block)
    return callRequest
  }
}

@AssistantDslMarker
class Call internal constructor(val callRequest: CallRequest) : CallUnion by callRequest {
  fun assistant(block: Assistant.() -> Unit) {
    Assistant(emptyJsonElement(), callRequest.assistantDto, callRequest.assistantOverridesDto).apply(block)
  }

  fun assistantId(block: AssistantId.() -> Unit) {
    AssistantId(emptyJsonElement(), callRequest).apply(block)
  }

  fun customer(block: Customer.() -> Unit) {
    Customer(callRequest.customerDto).apply(block)
  }
}

@AssistantDslMarker
class Customer(customerDto: CustomerDto) : CustomerUnion by customerDto
