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

package com.vapi4k.client

import com.vapi4k.common.Constants.APPLICATION_ID
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.SESSION_CACHE_ID
import com.vapi4k.common.Constants.STYLES_CSS
import com.vapi4k.common.Endpoints.INVOKE_TOOL_PATH
import com.vapi4k.common.EnvVar.REQUEST_VALIDATION_FILENAME
import com.vapi4k.common.EnvVar.REQUEST_VALIDATION_URL
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.tools.ToolCache.Companion.toolCallCache
import com.vapi4k.dsl.vapi4k.Vapi4kApplication
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonElementUtils.isAssistantIdResponse
import com.vapi4k.utils.JsonElementUtils.isAssistantResponse
import com.vapi4k.utils.JsonElementUtils.isSquadResponse
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.paramAnnotationWithDefault
import com.vapi4k.utils.Utils.resourceFile
import com.vapi4k.utils.get
import com.vapi4k.utils.modifyObjectWith
import com.vapi4k.utils.stringValue
import com.vapi4k.utils.toJsonElement
import com.vapi4k.utils.toJsonElementList
import com.vapi4k.utils.toJsonString
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.html.BODY
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.hiddenInput
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.link
import kotlinx.html.pre
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.collections.set

object ValidateAssistantResponse {
  private fun getNewRequest(): JsonElement {
    val request = runCatching {
      resourceFile(REQUEST_VALIDATION_FILENAME.value)
    }.getOrElse { ASSISTANT_REQUEST_JSON }
    return copyWithNewCallId(request.toJsonElement())
  }

  fun validateAssistantRequestResponse(
    application: Vapi4kApplication,
    secret: String,
  ): String {
    val request = getNewRequest()
    val (status, body) = runBlocking {
      val response = httpClient.post(REQUEST_VALIDATION_URL.value) {
        contentType(Application.Json)
        if (secret.isNotEmpty())
          headers.append("x-vapi-secret", secret)
        setBody(request)
      }
      response.status to response.bodyAsText()
    }

    val sessionCacheId = request.sessionCacheId

    return createHTML()
      .html {
        head {
          link {
            rel = "stylesheet"
            href = STYLES_CSS
          }
          link {
            rel = "stylesheet"
            href = "/assets/prism.css"
          }
          title { +"Assistant Request Validation" }
          script { src = HTMX_SOURCE_URL }
        }
        body {
          script { src = "/assets/prism.js" }
          h2 { +"Vapi4k Assistant Request Response" }
          if (status.value == 200) {
            div {
              style = "border: 1px solid black; padding: 10px; margin: 10px;"
              h3 { +"Path: ${application.serverPath}" }
              h3 { +"Status: $status" }
              pre {
                code(classes = "language-json line-numbers match-braces") {
                  +body.toJsonString()
                }
              }
            }
            val jsonElement = body.toJsonElement()

            with(jsonElement) {
              when {
                isAssistantResponse -> processAssistantRequest(application, jsonElement, sessionCacheId)
                isSquadResponse -> {
                  val assistants = jsonElement["squad.members"].toJsonElementList()
                  assistants.forEachIndexed { i, assistant ->
                    h2 { +"Assistant \"${getAssistantName(assistant, i)}\"" }
                    processAssistantRequest(application, assistant, sessionCacheId)
                  }
                }
                // TODO - Add support for assistantId responses
                isAssistantIdResponse -> {}
              }
            }
          } else {
            h3 { +"Path: ${application.serverPath}" }
            h3 { +"Status: $status" }
            if (body.isNotEmpty()) {
              if (body.length < 80) {
                h3 { +"Error: $body" }
              } else {
                h3 { +"Error:" }
                pre { +body }
              }
            } else {
              h3 { +"Check the ktor log for error information." }
            }
          }
        }
      }
  }

  private fun getAssistantName(
    assistantElement: JsonElement,
    index: Int,
  ): String =
    runCatching {
      assistantElement["assistant"].stringValue("name")
    }.getOrElse { index.toString() }

  private fun BODY.processAssistantRequest(
    application: Vapi4kApplication,
    assistantElement: JsonElement,
    sessionCacheId: SessionCacheId,
  ) {
    val functions =
      assistantElement["assistant.model.tools"].toJsonElementList()
        .map { it.stringValue("function.name") }

    h2 { +"Tools" }

    val functionInfo = toolCallCache.getFromCache(sessionCacheId)

    functions.forEach { function ->
      div {
        style = "border: 1px solid black; padding: 10px; margin: 10px;"
        val functionDetails = functionInfo.getFunction(function)
        val divid = getRandomSecret()
        h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCall?.description.orEmpty()}]" }
        form {
          attributes["hx-get"] = INVOKE_TOOL_PATH
          attributes["hx-trigger"] = "submit"
          attributes["hx-target"] = "#result-$divid"

          hiddenInput {
            name = APPLICATION_ID
            value = application.applicationId.value
          }
          hiddenInput {
            name = SESSION_CACHE_ID
            value = sessionCacheId.value
          }
          hiddenInput {
            name = FUNCTION_NAME
            value = function
          }
          table {
            tbody {
              functionDetails.params.forEach { functionDetail ->
                tr {
                  td { +"${functionDetail.first}:" }
                  td {
                    style = "width: 325px;"
                    input {
                      style = "width: 325px;"
                      type =
                        when (functionDetail.second.asKClass()) {
                          String::class -> InputType.text
                          Int::class -> InputType.number
                          Boolean::class -> InputType.checkBox
                          else -> InputType.text
                        }
                      name = functionDetail.first
                    }
                  }
                  td {
                    +"[${functionDetail.second.paramAnnotationWithDefault}]"
                  }
                }
              }
              tr {
                td {
                  input {
                    style = "margin-top: 10px;"
                    type = InputType.submit
                    value = "Invoke Tool"
                  }
                }
                td {}
                td {}
              }
            }
          }
        }

        script {
          rawHtml(
            """
              document.body.addEventListener('htmx:afterOnLoad', function(event) {
                if (event.detail.target.id === 'result-$divid') {
                  // Highlight the json result
                  let responseData = event.detail.target.innerHTML;
                  const codeElement = document.querySelector('#result-$divid');
                  codeElement.textContent = responseData;
                  Prism.highlightElement(codeElement);

                  // Make the jsosn result visible
                  const preElement = document.querySelector('#display-$divid');
                  preElement.style.display = 'block';
                }
              });
            """,
          )
        }

        pre {
          style = "display: none;"
          id = "display-$divid"
          code(classes = "language-json line-numbers match-braces") {
            id = "result-$divid"
          }
        }
      }
    }
  }

  private fun copyWithNewCallId(je: JsonElement): JsonElement =
    buildJsonObject {
      put(
        "message",
        je.modifyObjectWith("message") { messageMap ->
          messageMap["call"] =
            je.modifyObjectWith("message.call") { callMap ->
              callMap["id"] = JsonPrimitive(getRandomSecret(8, 4, 4, 12))
            }
        },
      )
    }

  const val ASSISTANT_REQUEST_JSON = """
    {
      "message": {
        "type": "assistant-request",
        "call": {
          "id": "305b7217-6d48-433b-bda9-0f00a1731234",
          "orgId": "679a13ec-f40d-4055-8959-797c4ee11234",
          "createdAt": "2024-07-25T06:07:29.604Z",
          "updatedAt": "2024-07-25T06:07:29.604Z",
          "type": "inboundPhoneCall",
          "status": "ringing",
          "phoneCallProvider": "twilio",
          "phoneCallProviderId": "CAef753577823739784a4a250331e4ab5a",
          "phoneCallTransport": "pstn",
          "phoneNumberId": "5a5a04dc-dcbe-45b1-8f64-fd32a253d135",
          "assistantId": null,
          "squadId": null,
          "customer": {
            "number": "+1234567890"
          }
        },
        "phoneNumber": {
          "id": "5a5a04dc-dcbe-45b1-8f64-fd32a253d135",
          "orgId": "679a13ec-f40d-4055-8959-797c4ee1694b",
          "assistantId": null,
          "number": "+1234567890",
          "createdAt": "2024-06-29T03:03:00.576Z",
          "updatedAt": "2024-07-20T04:24:05.533Z",
          "stripeSubscriptionId": "sub_1PWrYyCRkod4mKy33cFxM9B7",
          "twilioAccountSid": null,
          "twilioAuthToken": null,
          "stripeSubscriptionStatus": "active",
          "stripeSubscriptionCurrentPeriodStart": "2024-06-29T03:02:56.000Z",
          "name": null,
          "credentialId": null,
          "serverUrl": null,
          "serverUrlSecret": null,
          "twilioOutgoingCallerId": null,
          "sipUri": null,
          "provider": "twilio",
          "fallbackForwardingPhoneNumber": null,
          "fallbackDestination": null,
          "squadId": null
        },
        "customer": {
          "number": "+19256831234"
        },
        "timestamp": "2024-07-25T06:07:29.733Z"
      }
    }
  """
}
