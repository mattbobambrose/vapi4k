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

package com.vapi4k.validate

import com.vapi4k.common.ApplicationName
import com.vapi4k.common.AssistantId.Companion.EMPTY_ASSISTANT_ID
import com.vapi4k.common.AssistantId.Companion.toAssistantId
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CoreEnvVars.REQUEST_VALIDATION_FILENAME
import com.vapi4k.common.CoreEnvVars.vapi4kBaseUrl
import com.vapi4k.common.CssNames.TOOLS_DIV
import com.vapi4k.common.Endpoints.VALIDATE_INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.FunctionName
import com.vapi4k.common.FunctionName.Companion.toFunctionName
import com.vapi4k.common.Headers.VALIDATE_HEADER
import com.vapi4k.common.Headers.VALIDATE_VALUE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.common.QueryParams.APPLICATION_ID
import com.vapi4k.common.QueryParams.ASSISTANT_ID
import com.vapi4k.common.QueryParams.SESSION_ID
import com.vapi4k.common.QueryParams.TOOL_TYPE
import com.vapi4k.dsl.functions.ToolCallInfo.Companion.ID_SEPARATOR
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.ApplicationType
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.server.RequestContext
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.DslUtils.getRandomString
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.JsonUtils.EMPTY_JSON_ELEMENT
import com.vapi4k.utils.JsonUtils.modifyObjectWith
import com.vapi4k.utils.MiscUtils.appendQueryParams
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.paramAnnotationWithDefault
import com.vapi4k.utils.common.Utils.resourceFile
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.JsonElementUtils.stringValueOrNull
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import com.vapi4k.utils.json.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
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

object ValidateAssistant {
  private fun getNewRequest(): JsonElement {
    val request = runCatching {
      resourceFile(REQUEST_VALIDATION_FILENAME.value)
    }.getOrElse { ASSISTANT_REQUEST_JSON }
    return copyWithNewCallId(request.toJsonElement())
  }

  suspend fun validateAssistantRequestPage(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    appName: ApplicationName,
    secret: String,
  ): String {
    val request = getNewRequest()
    val typePrefix = application.applicationType.pathPrefix
    val sessionId = application.applicationType.randomSessionId
    val requestContext =
      RequestContext(
        application = application,
        request = request,
        sessionId = sessionId,
        assistantId = EMPTY_ASSISTANT_ID,
      )

    val baseUrl = "$vapi4kBaseUrl/$typePrefix/${appName.value}"
    val url = baseUrl.appendQueryParams(SESSION_ID to sessionId.value)
    val (status, responseBody) = fetchContent(application, request, secret, url)

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
          script { src = "$STATIC_BASE/js/prism-update.js" }
        }
        body {
          script { src = "$STATIC_BASE/js/prism.js" }

          if (config.allWebAndInboundApplications.size > 1) {
            div {
              id = "back-div"

              a {
                style = "text-decoration: none;"
                href = VALIDATE_PATH
                +"⬅️ "
              }
              a {
                href = VALIDATE_PATH
                +"Back"
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
            displayTools(responseBody, requestContext)
          } else {
            h3 {
              +"Vapi Server URL: "
              a {
                href = application.serverUrl
                target = "_blank"
                +application.serverUrl
              }
            }
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

  private fun BODY.displayTools(
    responseBody: String,
    requestContext: RequestContext,
  ) {
    val topLevel = responseBody.toJsonElement()
    // Strip messageResponse if it exists
    val child = if (topLevel.containsKey("messageResponse")) topLevel["messageResponse"] else topLevel
    when {
      child.containsKey("assistant") -> {
        assistantRequestToolsBody(requestContext, child["assistant"], "model")
      }

      child.containsKey("squad") -> {
        if (child.containsKey("squad.members")) {
          child.jsonElementList("squad.members")
            .forEachIndexed { i, member ->
              h2 { +"""Assistant "${getAssistantName(member, i + 1)}"""" }
              assistantRequestToolsBody(requestContext, member["assistant"], "model")
            }
        }

        if (child.containsKey("squad.membersOverrides")) {
          h2 { +"""Member Overrides""" }
          assistantRequestToolsBody(requestContext, child["squad.membersOverrides"], "model")
        }
      }

      child.containsKey("assistantId") -> {
        if (child.containsKey("assistantOverrides")) {
          assistantRequestToolsBody(requestContext, child["assistantOverrides"], "model")
        }
      }

      child.containsKey("squadId") -> {
        // Nothing to do here
      }

      else -> {
        logger.error { "Unknown response type: ${responseBody.toJsonElement().keys}" }
      }
    }
  }

  private fun getAssistantName(
    assistantElement: JsonElement,
    index: Int,
  ): String =
    runCatching {
      assistantElement.stringValue("assistant.name")
    }.getOrElse { "Unnamed-$index" }

  private fun BODY.assistantRequestToolsBody(
    requestContext: RequestContext,
    jsonElement: JsonElement,
    key: String,
  ) {
    val toolNames =
      if (jsonElement.containsKey("$key.tools"))
        jsonElement
          .jsonElementList(key, "tools")
          .mapNotNull { it.stringValueOrNull("function.name") }
      else
        emptyList()

    val funcNames =
      if (jsonElement.containsKey("$key.functions"))
        jsonElement
          .jsonElementList(key, "functions")
          .mapNotNull { it.stringValueOrNull("name") }
      else
        emptyList()

    displayServiceTools(requestContext, toolNames)
    displayManualTools(requestContext, toolNames)
    displayFunctions(requestContext, funcNames)
  }

  private fun BODY.displayServiceTools(
    requestContext: RequestContext,
    toolNames: List<String>,
  ) {
    if (requestContext.application.serviceCache.isNotEmpty()) {
      h3 { +"Service Tools" }
      val serviceCache = requestContext.application.serviceCache
      toolNames
        .mapIndexed { i, name ->
          name.toFunctionName() to name.split(ID_SEPARATOR).last().toAssistantId()
        }
        .filter { (toolName, assistantId) ->
          val functionInfo = serviceCache.getFromCache(requestContext.newAssistantId(assistantId))
          functionInfo.containsFunction(toolName)
        }
        .forEach { (toolName, assistantId) ->
          val newRequestContext = requestContext.newAssistantId(assistantId)
          div {
            id = TOOLS_DIV
            val functionInfo = serviceCache.getFromCache(newRequestContext)
            val functionDetails = functionInfo.getFunction(toolName)
            val divId = getRandomString()
            h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCallInfo.llmDescription}]" }
            form {
              setupHtmxTags(ToolType.SERVICE_TOOL, newRequestContext, divId)
              addHiddenFields(newRequestContext, toolName, false)

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
                        td { +"[${functionDetail.second.paramAnnotationWithDefault}]" }
                      }
                    }
                  addInvokeToolOption("Tool")
                }
              }
            }
            displayResponse(divId)
          }
        }
    } else {
      h3 { +"No Service Tools" }
    }
  }

  private fun BODY.displayManualTools(
    requestContext: RequestContext,
    toolNames: List<String>,
  ) {
    if (requestContext.application.manualToolCache.functions.isNotEmpty()) {
      h3 { +"Manual Tools" }
      toolNames
        .mapIndexed { i, name ->
          name.toFunctionName() to name.split(ID_SEPARATOR).last().toAssistantId()
        }
        .filter { (toolName, _) -> requestContext.application.containsManualToolInCache(toolName) }
        .forEach { (funcName, assistantId) ->
          div {
            id = TOOLS_DIV
            val manualToolImpl = requestContext.application.manualToolCache.getTool(funcName)
            val divId = getRandomString()
            h3 { +"$funcName (${manualToolImpl.signature})" }
            form {
              setupHtmxTags(ToolType.MANUAL_TOOL, requestContext, divId)
              addHiddenFields(requestContext, funcName, true)

              table {
                tbody {
                  manualToolImpl.properties.forEach { (propertyName, propertyDesc) ->
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
                      td { +"[${propertyDesc.description}]" }
                    }
                  }
                  addInvokeToolOption("Tool")
                }
              }
            }
            displayResponse(divId)
          }
        }
    } else {
      h3 { +"No Manual Tools" }
    }
  }

  private fun BODY.displayFunctions(
    requestContext: RequestContext,
    funcNames: List<String>,
  ) {
    if (requestContext.application.functionCache.isNotEmpty()) {
      h3 { +"Functions" }
      val functionCache = requestContext.application.functionCache
      funcNames
        .mapIndexed { i, name ->
          name.toFunctionName() to name.split(ID_SEPARATOR).last().toAssistantId()
        }
        .filter { (funcName, assistantId) ->
          val functionInfo = functionCache.getFromCache(requestContext.newAssistantId(assistantId))
          functionInfo.containsFunction(funcName)
        }
        .forEach { (funcName, assistantId) ->
          val newRequestContext = requestContext.newAssistantId(assistantId)
          div {
            id = TOOLS_DIV
            val functionInfo = functionCache.getFromCache(newRequestContext)
            val functionDetails = functionInfo.getFunction(funcName)
            val divId = getRandomString()
            h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCallInfo.llmDescription}]" }
            form {
              setupHtmxTags(ToolType.FUNCTION, newRequestContext, divId)
              addHiddenFields(newRequestContext, funcName, false)

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
                        td { +"[${functionDetail.second.paramAnnotationWithDefault}]" }
                      }
                    }
                  addInvokeToolOption("Function")
                }
              }
            }
            displayResponse(divId)
          }
        }
    } else {
      h3 { +"No Functions" }
    }
  }

  private fun FORM.setupHtmxTags(
    toolType: ToolType,
    requestContext: RequestContext,
    divId: String,
  ) {
    attributes["hx-get"] =
      VALIDATE_INVOKE_TOOL_PATH
        .appendQueryParams(
          TOOL_TYPE to toolType.name,
          SESSION_ID to requestContext.sessionId.value,
          ASSISTANT_ID to requestContext.assistantId.value,
        )
    attributes["hx-trigger"] = "submit"
    attributes["hx-target"] = "#result-$divId"
  }

  private fun FORM.addHiddenFields(
    requestContext: RequestContext,
    funcName: FunctionName,
    emptyAssistantId: Boolean,
  ) {
    hiddenInput {
      name = APPLICATION_ID
      value = requestContext.application.applicationId.value
    }
    hiddenInput {
      name = SESSION_ID
      value = requestContext.sessionId.value
    }
    hiddenInput {
      name = ASSISTANT_ID
      value = (if (emptyAssistantId) EMPTY_ASSISTANT_ID else requestContext.assistantId).value
    }
    hiddenInput {
      name = FUNCTION_NAME
      value = funcName.value
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
    script { rawHtml("setupPrismUpdate(`$divId`);\n") }

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

  private suspend fun fetchContent(
    application: AbstractApplicationImpl,
    request: JsonElement,
    secret: String,
    url: String,
  ): Pair<HttpStatusCode, String> {
    val response = httpClient.post(url) {
      contentType(Application.Json)
      headers.append(VALIDATE_HEADER, VALIDATE_VALUE)
      if (secret.isNotEmpty())
        headers.append(VAPI_SECRET_HEADER, secret)
      val jsonBody =
        if (application.applicationType == ApplicationType.INBOUND_CALL)
          request
        else
          EMPTY_JSON_ELEMENT
      setBody(jsonBody)
    }
    return response.status to response.bodyAsText()
  }

  private const val ASSISTANT_REQUEST_JSON = """
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
