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

import com.vapi4k.common.AssistantId
import com.vapi4k.common.AssistantId.Companion.EMPTY_ASSISTANT_ID
import com.vapi4k.common.AssistantId.Companion.getAssistantIdFromSuffix
import com.vapi4k.common.AssistantId.Companion.toAssistantId
import com.vapi4k.common.Constants.FUNCTION_NAME
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CssNames.FUNCTIONS
import com.vapi4k.common.CssNames.MANUAL_TOOLS
import com.vapi4k.common.CssNames.MESSAGE_RESPONSE
import com.vapi4k.common.CssNames.SERVICE_TOOLS
import com.vapi4k.common.CssNames.TOOLS_DIV
import com.vapi4k.common.Endpoints.VALIDATE_INVOKE_TOOL_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.common.FunctionName
import com.vapi4k.common.FunctionName.Companion.toFunctionName
import com.vapi4k.common.QueryParams.APPLICATION_ID
import com.vapi4k.common.QueryParams.ASSISTANT_ID
import com.vapi4k.common.QueryParams.SESSION_ID
import com.vapi4k.common.QueryParams.TOOL_TYPE
import com.vapi4k.dsl.functions.ToolCallInfo.Companion.ID_SEPARATOR
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.server.RequestContextImpl
import com.vapi4k.utils.DslUtils.getRandomString
import com.vapi4k.utils.HtmlUtils.css
import com.vapi4k.utils.HtmlUtils.html
import com.vapi4k.utils.HtmlUtils.js
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.JsonUtils.getFunctionNames
import com.vapi4k.utils.JsonUtils.getToolNames
import com.vapi4k.utils.MiscUtils.appendQueryParams
import com.vapi4k.utils.ReflectionUtils.asKClass
import com.vapi4k.utils.ReflectionUtils.isNotRequestContextClass
import com.vapi4k.utils.ReflectionUtils.paramAnnotationWithDefault
import com.vapi4k.utils.json.JsonElementUtils.containsKey
import com.vapi4k.utils.json.JsonElementUtils.jsonElementList
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import com.vapi4k.utils.json.get
import com.vapi4k.validate.AdminPage.attribs
import io.ktor.http.HttpStatusCode
import kotlinx.html.BODY
import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.hiddenInput
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.pre
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.ul
import kotlinx.serialization.json.JsonElement

object ValidateAssistantRequestPage {
  fun HTML.validateAssistantRequestPage(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    requestContext: RequestContextImpl,
    status: HttpStatusCode,
    responseBody: String,
  ) {
    head {
      css(
        "$STATIC_BASE/css/styles.css",
        "$STATIC_BASE/css/prism.css",
        "$STATIC_BASE/css/validator-body.css",
        "$STATIC_BASE/css/validator.css",
      )

      js(
        HTMX_SOURCE_URL,
        "$STATIC_BASE/js/update-prism-content.js",
        "$STATIC_BASE/js/prism.js",
      )

      title { +"Assistant Request Validation" }
    }
    body {
      if (config.allWebAndInboundApplications.size > 1)
        displayBackButton()

      h2 { +"Assistant Request Response" }

//      if (status == HttpStatusCode.OK) {
//        displayAssistantResponse(application, status, responseBody)
//        displayTools(responseBody, requestContext)
//      } else {
//        displayError(application, status, responseBody)
//      }
    }
  }

  fun validateAssistantRequestBody(
    application: AbstractApplicationImpl,
    requestContext: RequestContextImpl,
    status: HttpStatusCode,
    responseBody: String,
  ) =
    html {
      // h2 { +"Assistant Request Response" }

      nav("navbar navbar-expand-lg bg-body-tertiary") {
        div("container-fluid") {
          a(classes = "navbar-brand") {
            href = "#"
            +"Application"
          }

          button(classes = "navbar-toggler") {
            type = kotlinx.html.ButtonType.button
            attributes["data-bs-toggle"] = "collapse"
            attributes["data-bs-target"] = "#navbarNav"
            attributes["aria-controls"] = "navbarNav"
            attributes["aria-expanded"] = "false"
            attributes["aria-label"] = "Toggle navigation"
            span("navbar-toggler-icon") {
            }
          }

          div("collapse navbar-collapse") {
            id = "navbarNav"
            ul {
              classes += "navbar-nav"
              li {
                classes += "nav-item"
                a {
                  classes = setOf("nav-link", "active")
                  id = "$MESSAGE_RESPONSE-tab"
                  attribs("hx-on:click" to "selectTab('$MESSAGE_RESPONSE')")

                  attributes["aria-current"] = "page"
                  +"Message Response"
                }
              }

              li("nav-item") {
                a {
                  classes += "nav-link"
                  id = "$SERVICE_TOOLS-tab"
                  attribs("hx-on:click" to "selectTab('$SERVICE_TOOLS')")
                  href = "#"
                  +"Service Tools"
                }
              }

              li("nav-item") {
                a {
                  classes += "nav-link"
                  id = "$MANUAL_TOOLS-tab"
                  attribs("hx-on:click" to "selectTab('$MANUAL_TOOLS')")
                  +"Manual Tools"
                }
              }

              li("nav-item") {
                a {
                  classes += "nav-link"
                  id = "$FUNCTIONS-tab"
                  attribs("hx-on:click" to "selectTab('$FUNCTIONS')")
                  +"Functions"
                }
              }
            }
          }
        }
      }
      if (status == HttpStatusCode.OK) {
        displayAssistantResponse(application, responseBody)
        displayTools(responseBody, requestContext)
      } else {
        displayError(application, status, responseBody)
      }
    }

  private fun TagConsumer<*>.displayError(
    application: AbstractApplicationImpl,
    status: HttpStatusCode,
    responseBody: String,
  ) {
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

  private fun BODY.displayBackButton() {
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

  fun TagConsumer<*>.displayAssistantResponse(
    application: AbstractApplicationImpl,
    responseBody: String,
  ) {
    div {
      classes = setOf("validation-data")
      id = "$MESSAGE_RESPONSE-data"
      div {
        id = "response-header"
        +"Vapi Server URL: "
        span {
          style = "padding-left: 4px;"
          +application.serverUrl
        }
      }
      pre {
        code {
          classes = setOf("language-json", "line-numbers", "match-braces")
          id = "result-main"
          +responseBody.toJsonString()
        }
      }
    }
  }

  private fun TagConsumer<*>.displayTools(
    responseBody: String,
    requestContext: RequestContextImpl,
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

  private fun TagConsumer<*>.assistantRequestToolsBody(
    requestContext: RequestContextImpl,
    jsonElement: JsonElement,
    key: String,
  ) {
    val toolNames = jsonElement.getToolNames(key)
    val funcNames = jsonElement.getFunctionNames(key)
    div {
      classes = setOf("validation-data", "hidden")
      id = "$SERVICE_TOOLS-data"
      displayServiceTools(requestContext, toolNames)
    }
    div {
      classes = setOf("validation-data", "hidden")
      id = "$MANUAL_TOOLS-data"
      displayManualTools(requestContext, toolNames)
    }
    div {
      classes = setOf("validation-data", "hidden")
      id = "$FUNCTIONS-data"
      displayFunctions(requestContext, funcNames)
    }
  }

  private fun TagConsumer<*>.displayServiceTools(
    requestContext: RequestContextImpl,
    toolNames: List<String>,
  ) {
    if (requestContext.application.hasServiceTools()) {
      h3 { +"Service Tools" }
      val serviceCache = requestContext.application.serviceToolCache
      toolNames
        .mapIndexed { i, name ->
          // Grab assistantId from suffix of function name
          val assistantId = name.getAssistantIdFromSuffix()
          val newRequestContext = requestContext.copyWithNewAssistantId(assistantId)
          name.toFunctionName() to newRequestContext
        }
        .forEach { (toolName, newRequestContext) ->
          val functionInfo = serviceCache.getFromCache(newRequestContext)
          if (functionInfo.containsFunction(toolName)) {
            val functionDetails = functionInfo.getFunction(toolName)
            val divId = getRandomString()

            div {
              id = TOOLS_DIV
              h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCallInfo.llmDescription}]" }
              form {
                setHtmxTags(ToolType.SERVICE_TOOL, newRequestContext, divId)
                addHiddenFields(newRequestContext, toolName, false)

                table {
                  tbody {
                    functionDetails.params
                      .filter { it.second.isNotRequestContextClass() }
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
              displayToolResponse(divId)
            }
          }
        }
    } else {
      h3 { +"No Service Tools" }
    }
  }

  private fun TagConsumer<*>.displayManualTools(
    requestContext: RequestContextImpl,
    toolNames: List<String>,
  ) {
    if (requestContext.application.hasManualTools()) {
      h3 { +"Manual Tools" }
      toolNames
        .mapIndexed { i, name ->
          name.toFunctionName() to name.split(ID_SEPARATOR).last().toAssistantId()
        }
        .filter { (toolName, _) -> requestContext.application.containsManualTool(toolName) }
        .forEach { (funcName, assistantId) ->
          div {
            id = TOOLS_DIV
            val manualToolImpl = requestContext.application.getManualTool(funcName)
            val divId = getRandomString()
            h3 { +"$funcName (${manualToolImpl.signature})" }
            form {
              setHtmxTags(ToolType.MANUAL_TOOL, requestContext, divId)
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
            displayToolResponse(divId)
          }
        }
    } else {
      h3 { +"No Manual Tools" }
    }
  }

  private fun TagConsumer<*>.displayFunctions(
    requestContext: RequestContextImpl,
    funcNames: List<String>,
  ) {
    if (requestContext.application.hasFunctions()) {
      h3 { +"Functions" }
      val functionCache = requestContext.application.functionCache
      funcNames
        .mapIndexed { i, name ->
          // Grab assistantId from suffix of function name
          val assistantId = name.getAssistantIdFromSuffix()
          val newRequestContext = requestContext.copyWithNewAssistantId(assistantId)
          name.toFunctionName() to newRequestContext
        }
        .forEach { (funcName, newRequestContext) ->
          val functionInfo = functionCache.getFromCache(newRequestContext)
          if (functionInfo.containsFunction(funcName)) {
            val functionDetails = functionInfo.getFunction(funcName)
            val divId = getRandomString()

            div {
              id = TOOLS_DIV
              h3 { +"${functionDetails.fqNameWithParams}  [${functionDetails.toolCallInfo.llmDescription}]" }
              form {
                setHtmxTags(ToolType.FUNCTION, newRequestContext, divId)
                addHiddenFields(newRequestContext, funcName, false)

                table {
                  tbody {
                    functionDetails.params
                      .filter { it.second.isNotRequestContextClass() }
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
              displayToolResponse(divId)
            }
          }
        }
    } else {
      h3 { +"No Functions" }
    }
  }

  private fun FORM.setHtmxTags(
    toolType: ToolType,
    requestContext: RequestContextImpl,
    divId: String,
  ) {
    attribs(
      "hx-get" to
        VALIDATE_INVOKE_TOOL_PATH
          .appendQueryParams(
            TOOL_TYPE to toolType.name,
            SESSION_ID to requestContext.sessionId.value,
            ASSISTANT_ID to requestContext.assistantId.value,
          ),
      "hx-trigger" to "submit",
      "hx-target" to "#result-$divId",
    )
  }

  private fun FORM.addHiddenFields(
    requestContext: RequestContextImpl,
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

  private fun DIV.displayToolResponse(divId: String) {
    script { rawHtml("updateToolPrismContent(`$divId`);\n") }

    pre {
      classes += "tools-pre"
      id = "display-$divId"
      code {
        classes = setOf("language-json", "line-numbers", "match-braces")
        id = "result-$divId"
      }
    }
  }

  private fun RequestContextImpl.copyWithNewAssistantId(newAssistantId: AssistantId) =
    RequestContextImpl(application, request, sessionId, newAssistantId)
}
