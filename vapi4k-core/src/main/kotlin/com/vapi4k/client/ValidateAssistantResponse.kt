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

import com.vapi4k.api.vapi4k.AssistantRequestUtils.isAssistantIdResponse
import com.vapi4k.api.vapi4k.AssistantRequestUtils.isAssistantResponse
import com.vapi4k.api.vapi4k.AssistantRequestUtils.isSquadIdResponse
import com.vapi4k.api.vapi4k.AssistantRequestUtils.isSquadResponse
import com.vapi4k.common.Constants.APPLICATION_ID
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.SESSION_CACHE_ID
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CoreEnvVars.REQUEST_VALIDATION_FILENAME
import com.vapi4k.common.Endpoints.VALIDATE_INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.DslUtils.getRandomString
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.JsonElementUtils.sessionCacheId
import com.vapi4k.utils.JsonUtils.modifyObjectWith
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.paramAnnotationWithDefault
import com.vapi4k.utils.common.Utils.resourceFile
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import com.vapi4k.utils.json.get
import kotlinx.html.BODY
import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.InputType
import kotlinx.html.TBODY
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
  internal fun getNewRequest(): JsonElement {
    val request = runCatching {
      resourceFile(REQUEST_VALIDATION_FILENAME.value)
    }.getOrElse { ASSISTANT_REQUEST_JSON }
    return copyWithNewCallId(request.toJsonElement())
  }

  fun validateAssistantRequestPage(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    appName: String,
    secret: String,
  ): String {
    val request = getNewRequest()
    val (status, responseBody) = application.fetchContent(request, appName, secret)
    val sessionCacheId = request.sessionCacheId

    return createHTML()
      .html {
        head {
          link {
            rel = "stylesheet"
            href = "$STATIC_BASE/css/styles.css"
          }
          link {
            rel = "stylesheet"
            href = "$STATIC_BASE/css/prism.css"
          }
          link {
            rel = "stylesheet"
            href = "$STATIC_BASE/css/validator.css"
          }
          title { +"Assistant Request Validation" }
          script { src = HTMX_SOURCE_URL }
        }
        body {
          script { src = "$STATIC_BASE/js/prism.js" }

          if (config.inboundCallApplications.size > 1) {
            div {
              id = "back-div"
              a {
                href = VALIDATE_PATH
                +"⬅️ Back"
              }
            }
          }
          h2 { +"Assistant Request Response" }
          if (status.value == 200) {
            div {
              id = "status-div"
              h3 { +"Vapi Server URL: ${application.serverUrl}" }
              h3 { +"Status: $status" }
              pre {
                code(classes = "language-json line-numbers match-braces") {
                  +responseBody.toJsonString()
                }
              }
            }

            with(responseBody.toJsonElement()) {
              when {
                isAssistantResponse ->
                  assistantRequestToolsBody(application, this, sessionCacheId, "messageResponse.assistant.model")

                containsKey("assistant") ->
                  assistantRequestToolsBody(application, this, sessionCacheId, "assistant.model")

                isSquadResponse || containsKey("squad") -> {
                  val assistants = jsonElementList("messageResponse.squad.members")
                  assistants.forEachIndexed { i, assistant ->
                    h2 { +"""Assistant "${getAssistantName(assistant, i)}"""" }
                    assistantRequestToolsBody(application, assistant, sessionCacheId, "assistant.model")
                  }
                }
                // TODO - Add support for assistantId and squadId responses
                isAssistantIdResponse || containsKey("assistantId") -> {}
                isSquadIdResponse || containsKey("squadId") -> {}
                else -> {
                  error("Unknown response type: ${responseBody.toJsonElement().keys}")
                }
              }
            }
          } else {
            h3 { +"Vapi Server URL: ${application.serverUrl}" }
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
    application: AbstractApplicationImpl,
    jsonElement: JsonElement,
    sessionCacheId: SessionCacheId,
    key: String,
  ) {
    logger.debug { jsonElement.toJsonString() }
    val toolNames =
      if (jsonElement[key].containsKey("tools"))
        jsonElement
          .jsonElementList(key, "tools")
          .mapNotNull { if (!it.containsKey("function.name")) null else it.stringValue("function.name") }
      else
        emptyList()
    val funcNames =
      if (jsonElement[key].containsKey("functions"))
        jsonElement
          .jsonElementList(key, "functions")
          .mapNotNull { if (!it.containsKey("name")) null else it.stringValue("name") }
      else
        emptyList()

    displayServiceTools(application, sessionCacheId, toolNames)

    displayManualTools(application, sessionCacheId, toolNames)

    displayFunctions(application, sessionCacheId, funcNames)
  }

  private fun BODY.displayServiceTools(
    application: AbstractApplicationImpl,
    sessionCacheId: SessionCacheId,
    toolNames: List<String>,
  ) {
    if (application.serviceToolCache.containsSessionCacheId(sessionCacheId)) {
      h2 { +"Service Tools" }
      val functionInfo = application.serviceToolCache.getFromCache(sessionCacheId)
      toolNames
        .filter { functionInfo.containsFunction(it) }
        .forEach { toolName ->
          div {
            id = "tools-div"
            val functionDetails = functionInfo.getFunction(toolName)
            val divId = getRandomString()
            h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCallInfo.llmDescription}]" }
            form {
              setupHtmxTags(divId)
              addHiddenFields(application, sessionCacheId, toolName)

              table {
                tbody {
                  functionDetails.params
                    .filter { it.second.asKClass() != JsonElement::class }
                    .forEach { functionDetail ->
                      tr {
                        td { +"${functionDetail.first}:" }
                        td {
                          input {
                            id = "tools-input"
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
                  addInvokeToolOption("Tool")
                }
              }
            }

            displayResponse(divId)
          }
        }
    }
  }

  private fun BODY.displayManualTools(
    application: AbstractApplicationImpl,
    sessionCacheId: SessionCacheId,
    toolNames: List<String>,
  ) {
    if (application.manualToolCache.functions.isNotEmpty()) {
      h2 { +"Manual Tools" }
      toolNames
        .filter { application.manualToolCache.containsTool(it) }
        .forEach { funcName ->
          div {
            id = "tools-div"
            val manualToolImpl = application.manualToolCache.getTool(funcName)
            val divId = getRandomString()
            h3 { +"$funcName (${manualToolImpl.signature})" }
            form {
              setupHtmxTags(divId)
              addHiddenFields(application, sessionCacheId, funcName)

              table {
                tbody {
                  manualToolImpl.properties.forEach { propertyName, propertyDesc ->
                    tr {
                      td { +"$propertyName:" }
                      td {
                        input {
                          id = "tools-input"
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
                  addInvokeToolOption("Tool")
                }
              }
            }

            displayResponse(divId)
          }
        }
    }
  }

  private fun BODY.displayFunctions(
    application: AbstractApplicationImpl,
    sessionCacheId: SessionCacheId,
    funcNames: List<String>,
  ) {
    if (application.functionCache.containsSessionCacheId(sessionCacheId)) {
      h2 { +"Functions" }
      val functionInfo = application.functionCache.getFromCache(sessionCacheId)
      funcNames
        .filter { functionInfo.containsFunction(it) }
        .forEach { funcName ->
          div {
            id = "tools-div"
            val functionDetails = functionInfo.getFunction(funcName)
            val divId = getRandomString()
            h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCallInfo.llmDescription}]" }
            form {
              setupHtmxTags(divId)
              addHiddenFields(application, sessionCacheId, funcName)

              table {
                tbody {
                  functionDetails.params
                    .filter { it.second.asKClass() != JsonElement::class }
                    .forEach { functionDetail ->
                      tr {
                        td { +"${functionDetail.first}:" }
                        td {
                          input {
                            id = "tools-input"
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
                  addInvokeToolOption("Function")
                }
              }
            }

            displayResponse(divId)
          }
        }
    }
  }

  private fun FORM.setupHtmxTags(divId: String) {
    attributes["hx-get"] = VALIDATE_INVOKE_TOOL_PATH
    attributes["hx-trigger"] = "submit"
    attributes["hx-target"] = "#result-$divId"
  }

  private fun FORM.addHiddenFields(
    application: AbstractApplicationImpl,
    sessionCacheId: SessionCacheId,
    funcName: String,
  ) {
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
  }

  private fun TBODY.addInvokeToolOption(name: String) {
    tr {
      td {
        input {
          id = "invoke-input"
          type = InputType.submit
          value = "Invoke $name"
        }
      }
      td {}
      td {}
    }
  }

  private fun DIV.displayResponse(divId: String) {
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

    pre(classes = "code-pre") {
      id = "display-$divId"
      code(classes = "language-json line-numbers match-braces") {
        id = "result-$divId"
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
