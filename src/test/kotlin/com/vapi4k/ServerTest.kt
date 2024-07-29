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

import com.vapi4k.DoubleToolAssistant.doubleToolAssistant
import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.dsl.model.enums.GroqModelType
import com.vapi4k.server.Vapi4k
import com.vapi4k.utils.JsonFilenames
import com.vapi4k.utils.TestUtils.withTestApplication
import com.vapi4k.utils.firstInList
import com.vapi4k.utils.get
import com.vapi4k.utils.stringValue
import com.vapi4k.utils.toJsonString
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerTest {
  companion object {
    fun HttpRequestBuilder.configPost() {
      contentType(Application.Json)
    }
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

  @Test
  fun `simple assistant request`() {
    val (response, jsonElement) =
      withTestApplication("/json/assistantRequest.json") { request ->
        assistant(request) {
          groqModel {
            modelType = GroqModelType.LLAMA3_70B
          }
        }
      }

    assertEquals(HttpStatusCode.OK, response.status)
    assertEquals(GroqModelType.LLAMA3_70B.desc, jsonElement["assistant.model.model"].stringValue)
  }

  @Test
  fun `Tool requests arg ordering`() {
    val responses =
      withTestApplication(
        JsonFilenames.JSON_ASSISTANT_REQUEST,
        "/json/toolRequest1.json",
        "/json/toolRequest2.json",
        "/json/toolRequest3.json",
        "/json/toolRequest4.json",
        "/json/endOfCallReportRequest.json",
      ) { request ->
        doubleToolAssistant(request)
      }

    responses.forEachIndexed { i, (response, jsonElement) ->
      assertEquals(HttpStatusCode.OK, response.status)

      println(jsonElement.toJsonString())

      if (i in listOf(1, 2))
        assertEquals(
          "The weather in Danville, California is windy",
          jsonElement["results"].firstInList().stringValue("result"),
        )

      if (i in listOf(3, 4))
        assertEquals(
          "The weather in Boston, Massachusetts is rainy",
          jsonElement["results"].firstInList().stringValue("result"),
        )
    }
    // Make sure EOCR request cleans up cache
    // TODO Add healthcheck to see if cache is empty
    // assertEquals(true, )
  }
}
