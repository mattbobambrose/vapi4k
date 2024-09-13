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

package com.vapi4k.dashboard

import com.vapi4k.common.CssNames.ERROR_MSG
import com.vapi4k.common.CssNames.FUNCTIONS
import com.vapi4k.common.CssNames.MANUAL_TOOLS
import com.vapi4k.common.CssNames.MESSAGE_RESPONSE
import com.vapi4k.common.CssNames.SERVICE_TOOLS
import com.vapi4k.common.CssNames.VALIDATION_DATA
import com.vapi4k.dashboard.ValidateTools.displayTools
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.server.RequestContextImpl
import com.vapi4k.utils.HtmlUtils.attribs
import com.vapi4k.utils.HtmlUtils.html
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import io.ktor.http.HttpStatusCode
import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.h6
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.pre
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.ul
import kotlin.collections.set

object ValidateAssistantRequestPage {
  fun validateAssistantRequestBody(
    application: AbstractApplicationImpl,
    requestContext: RequestContextImpl,
    status: HttpStatusCode,
    responseBody: String,
  ): String =
    html {
      nav {
        classes = setOf("navbar", "navbar-expand-lg", "bg-body-tertiary")
        style = "padding-top: 10px; padding-bottom: 0px;"
        div {
          classes += "container-fluid"
//          a(classes = "navbar-brand") {
//            href = "#"
//            +"Application"
//          }

//          button(classes = "navbar-toggler") {
//            type = kotlinx.html.ButtonType.button
//            attributes["data-bs-toggle"] = "collapse"
//            attributes["data-bs-target"] = "#navbarNav"
//            attributes["aria-controls"] = "navbarNav"
//            attributes["aria-expanded"] = "false"
//            attributes["aria-label"] = "Toggle navigation"
//            span("navbar-toggler-icon") {
//            }
//          }

          if (status == HttpStatusCode.OK) {
            div {
              classes = setOf("collapse", "navbar-collapse")
              id = "navbarNav"
              ul {
                //classes += "navbar-nav"
                classes = setOf("nav", "nav-tabs")
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

                li {
                  classes += "nav-item"
                  a {
                    classes = setOf("nav-link", if (requestContext.application.hasServiceTools()) "" else "disabled")
                    id = "$SERVICE_TOOLS-tab"
                    attribs("hx-on:click" to "selectTab('$SERVICE_TOOLS')")
                    +"Service Tools"
                  }
                }

                li {
                  classes += "nav-item"
                  a {
                    classes = setOf("nav-link", if (requestContext.application.hasManualTools()) "" else "disabled")
                    id = "$MANUAL_TOOLS-tab"
                    attribs("hx-on:click" to "selectTab('$MANUAL_TOOLS')")
                    +"Manual Tools"
                  }
                }

                li {
                  classes += "nav-item"
                  a {
                    classes = setOf("nav-link", if (requestContext.application.hasFunctions()) "" else "disabled")
                    id = "$FUNCTIONS-tab"
                    attribs("hx-on:click" to "selectTab('$FUNCTIONS')")
                    +"Functions"
                  }
                }
              }
            }
          } else {
            div {
              classes = setOf("collapse", "navbar-collapse")
              id = "navbarNav"
              ul {
                // classes += "navbar-nav"
                classes = setOf("nav", "nav-tabs")
                li {
                  classes += "nav-item"
                  a {
                    classes = setOf("nav-link", "active")
                    id = "$MESSAGE_RESPONSE-tab"
                    // attribs("hx-on:click" to "selectTab('$MESSAGE_RESPONSE')")
                    attributes["aria-current"] = "page"
                    +"Error Message"
                  }
                }
              }
            }
          }
        }
      }

      if (status == HttpStatusCode.OK) {
        displayResponse(application, responseBody)
        displayTools(responseBody, requestContext)
      } else {
        displayError(application, status, responseBody)
      }
    }

  private fun TagConsumer<*>.displayResponse(
    application: AbstractApplicationImpl,
    responseBody: String,
  ) {
    div {
      classes = setOf(VALIDATION_DATA)
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
          // "line-numbers" is added in the JS code. It is a work-around for it getting dropped on the 2nd selection
          classes = setOf("language-json", "match-braces")
          id = "response-main"
          +responseBody.toJsonString()
        }
      }
    }
  }


  private fun TagConsumer<*>.displayError(
    application: AbstractApplicationImpl,
    status: HttpStatusCode,
    responseBody: String,
  ) {
    div {
      classes += VALIDATION_DATA
      id = "$MESSAGE_RESPONSE-data"

      div {
        classes += ERROR_MSG
        h6 {
          style = "padding-top: 10px;"
          +"Vapi Server URL: "
          a {
            href = application.serverUrl
            target = "_blank"
            +application.serverUrl
          }
        }
        h6 { +"Status: $status" }
      }

      if (responseBody.isNotEmpty()) {
        if (responseBody.length < 80) {
          h6 {
            classes += ERROR_MSG
            +"Error: $responseBody"
          }
        } else {
          h6 {
            classes += ERROR_MSG
            +"Error:"
          }
          pre { +"  $responseBody" }
        }
      } else {
        h6 {
          classes += ERROR_MSG
          +"Check the ktor log for error information."
        }
      }
    }
  }


}
