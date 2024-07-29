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

package com.vapi4k.utils

import com.vapi4k.ServerTest.Companion.configPost
import com.vapi4k.common.Constants.DEFAULT_SERVER_PATH
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.server.Vapi4k
import com.vapi4k.utils.Utils.resourceFile
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.JsonElement

object TestUtils {
  fun withTestApplication(
    fileName: String,
    block: (JsonElement) -> AssistantRequestResponse,
  ): Pair<HttpResponse, JsonElement> {
    var response: HttpResponse? = null
    var je: JsonElement? = null
    testApplication {
      application {
        install(Vapi4k) {
          onAssistantRequest { request ->
            block(request)
          }

          toolCallEndpoints {
            // Provide a default endpoint
            endpoint {
              serverUrl = "https://test/toolCall"
              serverUrlSecret = "456"
              timeoutSeconds = 20
            }
          }
        }
      }

      response =
        client.post("/$DEFAULT_SERVER_PATH") {
          configPost()
          setBody(resourceFile(fileName))
        }

      je = response!!.bodyAsText().toJsonElement()
    }
    return response!! to je!!
  }

  fun withTestApplication(
    vararg fileNames: String,
    block: (JsonElement) -> AssistantRequestResponse,
  ): List<Pair<HttpResponse, JsonElement>> {
    val responses: MutableList<Pair<HttpResponse, JsonElement>> = mutableListOf()
    testApplication {
      application {
        install(Vapi4k) {
          onAssistantRequest { request ->
            block(request)
          }

          toolCallEndpoints {
            // Provide a default endpoint
            endpoint {
              serverUrl = "https://test/toolCall"
              serverUrlSecret = "456"
              timeoutSeconds = 20
            }
          }
        }
      }

      responses
        .addAll(
          fileNames.map { fileName ->
            val response = client.post("/$DEFAULT_SERVER_PATH") {
              configPost()
              setBody(resourceFile(fileName))
            }
            response to response.bodyAsText().toJsonElement()
          })

    }
    return responses
  }
}
