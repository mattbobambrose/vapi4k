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

import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.isAssistantIdResponse
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.isAssistantResponse
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.isSquadIdResponse
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.isSquadResponse
import com.vapi4k.common.Constants.APPLICATION_ID
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.SESSION_CACHE_ID
import com.vapi4k.common.Constants.STYLES_CSS
import com.vapi4k.common.Endpoints.VALIDATE_INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.JsonUtils.modifyObjectWith
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.paramAnnotationWithDefault
import com.vapi4k.utils.common.Utils.ensureStartsWith
import com.vapi4k.utils.common.Utils.resourceFile
import com.vapi4k.utils.envvar.CoreEnvVars.REQUEST_VALIDATION_FILENAME
import com.vapi4k.utils.envvar.CoreEnvVars.serverBaseUrl
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import com.vapi4k.utils.json.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.html.BODY
import kotlinx.html.InputType
import kotlinx.html.a
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

  fun validateAssistantRequestPage(
    application: Vapi4kApplicationImpl,
    appName: String,
    secret: String,
  ): String {
    val request = getNewRequest()
    val (status, responseBody) = runBlocking {
      val url = "$serverBaseUrl/$appName"
      val response = httpClient.post(url) {
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
          div {
            style = "text-align: right; margin-top: 20px; margin-right: 15;"
            a {
              href = VALIDATE_PATH
              +"Home"
            }
          }
          h2 { +"Vapi4k Assistant Request Response" }
          if (status.value == 200) {
            div {
              style = "border: 1px solid black; padding: 10px; margin: 10px;"
              h3 { +"Path: ${application.serverPath.ensureStartsWith("/")}" }
              h3 { +"Status: $status" }
              pre {
                code(classes = "language-json line-numbers match-braces") {
                  +responseBody.toJsonString()
                }
              }
            }

            with(responseBody.toJsonElement()) {
              when {
                isAssistantResponse -> assistantRequestToolsBody(application, this, sessionCacheId)
                isSquadResponse -> {
                  val assistants = jsonElementList("squad.members")
                  assistants.forEachIndexed { i, assistant ->
                    h2 { +"Assistant \"${getAssistantName(assistant, i)}\"" }
                    assistantRequestToolsBody(application, assistant, sessionCacheId)
                  }
                }
                // TODO - Add support for assistantId and squadId responses
                isAssistantIdResponse -> {}
                isSquadIdResponse -> {}
                else -> {
                  error("Unknown response type: ${responseBody.toJsonElement().keys}")
                }
              }
            }
          } else {
            h3 { +"Path: ${application.serverPath.ensureStartsWith("/")}" }
            h3 { +"Status: $status" }
            if (responseBody.isNotEmpty()) {
              if (responseBody.length < 80) {
                h3 { +"Error: $responseBody" }
              } else {
                h3 { +"Error:" }
                pre { +responseBody }
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
      assistantElement.stringValue("assistant.name")
    }.getOrElse { index.toString() }

  private fun BODY.assistantRequestToolsBody(
    application: Vapi4kApplicationImpl,
    jsonElement: JsonElement,
    sessionCacheId: SessionCacheId,
  ) {
    logger.info { jsonElement.toJsonString() }
    val funcNames =
      if (jsonElement["messageResponse.assistant.model"].containsKey("tools"))
        jsonElement
          .jsonElementList("messageResponse.assistant.model.tools")
          .mapNotNull { if (!it.containsKey("function.name")) null else it.stringValue("function.name") }
      else
        emptyList()

    logger.debug { "Checking toolCache: ${application.serviceToolCache.name} [$sessionCacheId]" }
    if (!application.serviceToolCache.containsSessionCacheId(sessionCacheId)) {
      h2 { +"No Service Tools Declared" }
    } else {
      h2 { +"Service Tools" }
      val functionInfo = application.serviceToolCache.getFromCache(sessionCacheId)
      funcNames
        .filter { functionInfo.containsFunction(it) }
        .forEach { funcName ->
          div {
            style = "border: 1px solid black; padding: 10px; margin: 10px;"
            val functionDetails = functionInfo.getFunction(funcName)
            val divId = getRandomSecret()
            h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCall?.description.orEmpty()}]" }
            form {
              attributes["hx-get"] = VALIDATE_INVOKE_TOOL_PATH
              attributes["hx-trigger"] = "submit"
              attributes["hx-target"] = "#result-$divId"

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
                value = funcName
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
                              Double::class -> InputType.number
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
                  if (event.detail.target.id === 'result-$divId') {
                    // Highlight the json result
                    let responseData = event.detail.target.innerHTML;
                    const codeElement = document.querySelector('#result-$divId');
                    codeElement.textContent = responseData;
                    Prism.highlightElement(codeElement);

                    // Make the jsosn result visible
                    const preElement = document.querySelector('#display-$divId');
                    preElement.style.display = 'block';
                  }
                });
              """,
              )
            }

            pre {
              style = "display: none;"
              id = "display-$divId"
              code(classes = "language-json line-numbers match-braces") {
                id = "result-$divId"
              }
            }
          }
        }
    }

    if (application.manualToolCache.functions.isEmpty()) {
      h2 { +"No Manual Tools Declared" }
    } else {
      h2 { +"Manual Tools" }
      val manualFunctions = application.manualToolCache.functions
      funcNames
        .filter { application.manualToolCache.containsTool(it) }
        .forEach { funcName ->
          div {
            style = "border: 1px solid black; padding: 10px; margin: 10px;"
            val divId = getRandomSecret()
            h3 { +funcName }
            form {
              attributes["hx-get"] = VALIDATE_INVOKE_TOOL_PATH
              attributes["hx-trigger"] = "submit"
              attributes["hx-target"] = "#result-$divId"

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
                value = funcName
              }
              table {
                tbody {
                  val manualToolImpl = application.manualToolCache.getTool(funcName)
                  manualToolImpl.toolDto.functionDto.parametersDto.properties.forEach { propertyName, propertyDesc ->
                    tr {
                      td { +"$propertyName:" }
                      td {
                        style = "width: 325px;"
                        input {
                          style = "width: 325px;"
                          type =
                            when (propertyDesc.type) {
                              "string" -> InputType.text
                              "int" -> InputType.number
                              "double" -> InputType.number
                              "boolean" -> InputType.checkBox
                              else -> InputType.text
                            }
                          name = propertyName
                        }
                      }
                      td {
                        +"[${propertyDesc.description}]"
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
                  if (event.detail.target.id === 'result-$divId') {
                    // Highlight the json result
                    let responseData = event.detail.target.innerHTML;
                    const codeElement = document.querySelector('#result-$divId');
                    codeElement.textContent = responseData;
                    Prism.highlightElement(codeElement);

                    // Make the jsosn result visible
                    const preElement = document.querySelector('#display-$divId');
                    preElement.style.display = 'block';
                  }
                });
              """,
              )
            }

            pre {
              style = "display: none;"
              id = "display-$divId"
              code(classes = "language-json line-numbers match-braces") {
                id = "result-$divId"
              }
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
