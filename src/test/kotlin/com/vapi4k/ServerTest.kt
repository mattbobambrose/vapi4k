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

import com.vapi4k.common.Constants.DEFAULT_SERVER_PATH
import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.dsl.model.enums.GroqModelType
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import com.vapi4k.utils.JsonUtils.toJsonElement
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerTest {

  private fun HttpRequestBuilder.configPost() {
    contentType(Application.Json)
  }

  @Test
  fun `ping request`() {
    testApplication {
      application {
        install(Vapi4k)
      }
      val response = client.get("/ping")
      assertEquals(HttpStatusCode.OK, response.status)
      assertEquals("pong", response.bodyAsText())
    }
  }

  fun readRequest(fileName: String): String {
    return this::class.java.getResource(fileName)?.readText() ?: error("File not found: $fileName")
  }

  @Test
  fun `simple assistant request`() {
    testApplication {
      application {
        install(Vapi4k) {
          onAssistantRequest { request ->
            assistant(request) {
              groqModel {
                modelType = GroqModelType.LLAMA3_70B
              }
            }
          }
        }
      }

      val response =
        client.post("/$DEFAULT_SERVER_PATH") {
          configPost()
          setBody(readRequest("/json/assistantRequest.json"))
        }

      val je = response.bodyAsText().toJsonElement()
      assertEquals(HttpStatusCode.OK, response.status)
      assertEquals(GroqModelType.LLAMA3_70B.desc, je["assistant.model.model"].stringValue)
    }
  }
}
