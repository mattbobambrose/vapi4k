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
import com.vapi4k.api.assistant.AssistantResponse
import com.vapi4k.api.tools.enums.ToolMessageType
import com.vapi4k.api.vapi4k.RequestContext
import com.vapi4k.common.EnvVar.Companion.defaultServerPath
import com.vapi4k.dtos.tools.ToolMessageCondition
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.server.Vapi4k
import com.vapi4k.utils.JsonElementUtils.emptyJsonElement
import com.vapi4k.utils.Utils.resourceFile
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.JsonElement

fun assistantResponse(
  requestContext: RequestContext,
  block: AssistantResponse.() -> Unit,
): AssistantRequestResponse {
  val assistantResponse = AssistantResponse(requestContext).apply(block)
  return if (assistantResponse.isAssigned)
    assistantResponse.assistantRequestResponse
  else
    error("assistantResponse{} is missing an assistant{}, assistantId{}, squad{}, or squadId{} declaration")
}

fun JsonElement.tools() = get("assistant.model.tools").toJsonElementList()

fun JsonElement.firstTool() = tools().first()

fun JsonElement.firstToolMessages() = firstTool()["messages"].toJsonElementList()

fun JsonElement.firstMessageOfType(
  type: ToolMessageType,
  vararg conditions: ToolMessageCondition,
) = if (conditions.isEmpty())
  firstToolMessages()
    .filter { !it.containsKey("conditions") }
    .first { it.stringValue("type") == type.desc }
else
  firstToolMessages()
    .filter { it.containsKey("conditions") }
    .filter {
      conditions.all { c -> it["conditions"].toJsonElementList().contains(c.toJsonElement()) }
    }
    .first { it.stringValue("type") == type.desc }

fun withTestApplication(
  fileName: String,
  block: AssistantResponse.() -> Unit,
): Pair<HttpResponse, JsonElement> {
  var response: HttpResponse? = null
  var je: JsonElement? = null
  testApplication {
    application {
      install(Vapi4k) {
        vapi4kApplication {
          onAssistantRequest {
            block()
          }

          toolCallEndpoints {
            // Provide a default endpoint
            endpoint {
              serverUrl = "https://test/toolCall"
              serverSecret = "456"
              timeoutSeconds = 20
            }
          }
        }
      }
    }

    response =
      client.post("/$defaultServerPath") {
        configPost()
        setBody(resourceFile(fileName))
      }

    je =
      if (response!!.status == HttpStatusCode.OK)
        response!!.bodyAsText().toJsonElement()
      else
        emptyJsonElement()
  }
  return response!! to je!!
}

fun withTestApplication(
  fileNames: List<String>,
  getArg: String = "",
  cacheRemovalEnabled: Boolean = true,
  block: AssistantResponse.() -> Unit,
): List<Pair<HttpResponse, JsonElement>> {
  val responses: MutableList<Pair<HttpResponse, JsonElement>> = mutableListOf()
  testApplication {
    application {
      install(Vapi4k) {
        vapi4kApplication {
          eocrCacheRemovalEnabled = cacheRemovalEnabled

          onAssistantRequest {
            block()
          }

          toolCallEndpoints {
            // Provide a default endpoint
            endpoint {
              serverUrl = "https://test/toolCall"
              serverSecret = "456"
              timeoutSeconds = 20
            }
          }
        }
      }
    }

    responses
      .addAll(
        fileNames.map { fileName ->
          val response = client.post("/$defaultServerPath") {
            configPost()
            setBody(resourceFile(fileName))
          }
          response to response.bodyAsText().toJsonElement()
        },
      )

    if (getArg.isNotEmpty()) {
      responses.add(
        client.get(getArg) { configPost() }.let { it to it.bodyAsText().toJsonElement() },
      )
    }
  }
  return responses
}
