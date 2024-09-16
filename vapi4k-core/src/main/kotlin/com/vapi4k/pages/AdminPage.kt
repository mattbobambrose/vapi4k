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

package com.vapi4k.pages

import com.vapi4k.common.Constants.BS_BASE
import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.HTMX_WS_SOURCE_URL
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CssNames.ACTIVE
import com.vapi4k.common.CssNames.HIDDEN
import com.vapi4k.common.CssNames.LOG_DIV
import com.vapi4k.common.CssNames.MAIN_DIV
import com.vapi4k.common.CssNames.SYS_INFO_DIV
import com.vapi4k.common.Endpoints.ADMIN_CONSOLE_ENDPOINT
import com.vapi4k.common.Endpoints.ADMIN_ENV_PATH
import com.vapi4k.common.Endpoints.ADMIN_VERSION_PATH
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.utils.HtmlUtils.attribs
import com.vapi4k.utils.HtmlUtils.css
import com.vapi4k.utils.HtmlUtils.js
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.common.Utils.ensureStartsWith
import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.HTMLTag
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.pre
import kotlinx.html.role
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.ul

internal object AdminPage {
  fun HTML.adminPage(config: Vapi4kConfigImpl) {
    head {
      meta {
        name = "viewport"
        content = "width=device-width, initial-scale=1"
      }

      css(
        // "https://cdn.jsdelivr.net/npm/@docsearch/css@3",
        "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css",
        "$BS_BASE/dist/css/bootstrap.min.css",
        "$STATIC_BASE/css/sidebars2.css",
        "$STATIC_BASE/css/sidebars.css",
        "$STATIC_BASE/css/styles.css",
        "$STATIC_BASE/css/prism.css",
        "$STATIC_BASE/css/validator.css",
      )

      js(
        HTMX_SOURCE_URL,
        HTMX_WS_SOURCE_URL,
        "$STATIC_BASE/js/ws-events.js",
        "$STATIC_BASE/js/prism.js",
        "$STATIC_BASE/js/color-modes.js",
        "$STATIC_BASE/js/fade-events.js",
      )

      title { +"Vapi4k Admin" }
    }

    body {
      rawHtml(
        """
          <svg xmlns="http://www.w3.org/2000/svg" class="d-none">
              <symbol id="check2" viewBox="0 0 16 16">
                  <path d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0z"/>
              </symbol>
              <symbol id="circle-half" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 0 8 1v14zm0 1A8 8 0 1 1 8 0a8 8 0 0 1 0 16z"/>
              </symbol>
              <symbol id="moon-stars-fill" viewBox="0 0 16 16">
                  <path d="M6 .278a.768.768 0 0 1 .08.858 7.208 7.208 0 0 0-.878 3.46c0 4.021 3.278 7.277 7.318 7.277.527 0 1.04-.055 1.533-.16a.787.787 0 0 1 .81.316.733.733 0 0 1-.031.893A8.349 8.349 0 0 1 8.344 16C3.734 16 0 12.286 0 7.71 0 4.266 2.114 1.312 5.124.06A.752.752 0 0 1 6 .278z"/>
                  <path d="M10.794 3.148a.217.217 0 0 1 .412 0l.387 1.162c.173.518.579.924 1.097 1.097l1.162.387a.217.217 0 0 1 0 .412l-1.162.387a1.734 1.734 0 0 0-1.097 1.097l-.387 1.162a.217.217 0 0 1-.412 0l-.387-1.162A1.734 1.734 0 0 0 9.31 6.593l-1.162-.387a.217.217 0 0 1 0-.412l1.162-.387a1.734 1.734 0 0 0 1.097-1.097l.387-1.162zM13.863.099a.145.145 0 0 1 .274 0l.258.774c.115.346.386.617.732.732l.774.258a.145.145 0 0 1 0 .274l-.774.258a1.156 1.156 0 0 0-.732.732l-.258.774a.145.145 0 0 1-.274 0l-.258-.774a1.156 1.156 0 0 0-.732-.732l-.774-.258a.145.145 0 0 1 0-.274l.774-.258c.346-.115.617-.386.732-.732L13.863.1z"/>
              </symbol>
              <symbol id="sun-fill" viewBox="0 0 16 16">
                  <path d="M8 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM8 0a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 0zm0 13a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 13zm8-5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2a.5.5 0 0 1 .5.5zM3 8a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2A.5.5 0 0 1 3 8zm10.657-5.657a.5.5 0 0 1 0 .707l-1.414 1.415a.5.5 0 1 1-.707-.708l1.414-1.414a.5.5 0 0 1 .707 0zm-9.193 9.193a.5.5 0 0 1 0 .707L3.05 13.657a.5.5 0 0 1-.707-.707l1.414-1.414a.5.5 0 0 1 .707 0zm9.193 2.121a.5.5 0 0 1-.707 0l-1.414-1.414a.5.5 0 0 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .707zM4.464 4.465a.5.5 0 0 1-.707 0L2.343 3.05a.5.5 0 1 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .708z"/>
              </symbol>
          </svg>
        """,
      )

      addLiveTailButton()
      addToggleThemeButton()

      declareSvgs()

      main {
        classes = setOf("d-flex", "flex-nowrap")
        div {
          classes = setOf("d-flex", "flex-column", "flex-shrink-0", "p-2", "bg-body-tertiary", "sidebar")
          style = "width: 280px;"
          span {
            classes = setOf(
              "d-flex",
              "align-items-center",
              "mb-3",
              "mb-md-0",
              "me-md-auto",
              "link-body-emphasis",
              "text-decoration-none",
            )
            id = "title-span"
            // svg("bi pe-none me-2") { details(40, 32, "bootstrap") }
            span {
              classes += "fs-4"
              rawHtml("&nbsp;&nbsp;")
              +"Vapi4k Admin"
              rawHtml("&nbsp;&nbsp;&nbsp;&nbsp;")
            }
            span("spinner-border text-primary p-1 htmx-indicator") {
              id = "spinner"
              role = "status"
            }
          }

          hr {
            style = "margin-top: 5px; margin-bottom: 5px;"
          }

          ul {
            classes = setOf("nav", "nav-pills", "flex-column", "mb-auto")
//            li("nav-item") {
//              a(classes = "nav-link active") {
//                href = "#"
//                attributes["aria-current"] = "page"
//                svg("bi pe-none me-2") {
//                  details(16, 16, "home")
//                }
//                +"Home"
//              }
//            }

//            button(classes = "btn  d-inline-flex align-items-center rounded border-0 collapsed") {
//              attribs(
//                "data-bs-toggle" to "collapse",
//                "data-bs-target" to "#validator-collapse",
//                "aria-expanded" to "true",
//              )
//              svg("bi pe-none me-2") { details(16, 16, "table") }
//              +"Environment Vars"
//            }

            button {
              classes = setOf(
                "btn",
                "d-inline-flex",
                "align-items-center",
                "rounded",
                "border-0",
                "sidebar-menu-item",
                "fs-5",
                ACTIVE,
              )
              attribs("onclick" to "displayLogging()")
              rawHtml(
                """
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="me-2 bi bi-terminal" viewBox="0 0 16 16">
                    <path d="M6 9a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1h-3A.5.5 0 0 1 6 9M3.854 4.146a.5.5 0 1 0-.708.708L4.793 6.5 3.146 8.146a.5.5 0 1 0 .708.708l2-2a.5.5 0 0 0 0-.708z"/>
                    <path d="M2 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2zm12 1a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1z"/>
                  </svg>
                  """
              )
              +"Console Log"
            }

            button {
              classes = setOf(
                "btn",
                "d-inline-flex",
                "align-items-center",
                "rounded",
                "border-0",
                "fs-5",
              )
              style = "cursor: default;"
              rawHtml(
                """
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="me-2 bi bi-window" viewBox="0 0 16 16">
                    <path d="M2.5 4a.5.5 0 1 0 0-1 .5.5 0 0 0 0 1m2-.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0m1 .5a.5.5 0 1 0 0-1 .5.5 0 0 0 0 1"/>
                    <path d="M2 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2zm13 2v2H1V3a1 1 0 0 1 1-1h12a1 1 0 0 1 1 1M2 14a1 1 0 0 1-1-1V6h14v7a1 1 0 0 1-1 1z"/>
                  </svg>
                  """
              )
              +"Applications"
            }

            displayApplications("InboundCall Applications", config.inboundCallApplications, "inbound-collapse")
            displayApplications("OutboundCall Applications", config.outboundCallApplications, "outbound-collapse")
            displayApplications("Web Applications", config.webApplications, "web-collapse")

            button {
              classes =
                setOf("btn", "d-inline-flex", "align-items-center", "rounded", "border-0", "sidebar-menu-item", "fs-5")
              clickAction(ADMIN_ENV_PATH, SYS_INFO_DIV)
              rawHtml(
                """
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="me-2 bi bi-clipboard-check" viewBox="0 0 16 16">
                  <path fill-rule="evenodd" d="M10.854 7.146a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708 0l-1.5-1.5a.5.5 0 1 1 .708-.708L7.5 9.793l2.646-2.647a.5.5 0 0 1 .708 0"/>
                  <path d="M4 1.5H3a2 2 0 0 0-2 2V14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V3.5a2 2 0 0 0-2-2h-1v1h1a1 1 0 0 1 1 1V14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V3.5a1 1 0 0 1 1-1h1z"/>
                  <path d="M9.5 1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5zm-3-1A1.5 1.5 0 0 0 5 1.5v1A1.5 1.5 0 0 0 6.5 4h3A1.5 1.5 0 0 0 11 2.5v-1A1.5 1.5 0 0 0 9.5 0z"/>
                </svg>
              """
              )
              +"Environment Vars"
            }

            button {
              classes =
                setOf("btn", "d-inline-flex", "align-items-center", "rounded", "border-0", "sidebar-menu-item", "fs-5")
              clickAction(ADMIN_VERSION_PATH, SYS_INFO_DIV)
              rawHtml(
                """
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="me-2 bi bi-calendar2-date" viewBox="0 0 16 16">
                  <path d="M6.445 12.688V7.354h-.633A13 13 0 0 0 4.5 8.16v.695c.375-.257.969-.62 1.258-.777h.012v4.61zm1.188-1.305c.047.64.594 1.406 1.703 1.406 1.258 0 2-1.066 2-2.871 0-1.934-.781-2.668-1.953-2.668-.926 0-1.797.672-1.797 1.809 0 1.16.824 1.77 1.676 1.77.746 0 1.23-.376 1.383-.79h.027c-.004 1.316-.461 2.164-1.305 2.164-.664 0-1.008-.45-1.05-.82zm2.953-2.317c0 .696-.559 1.18-1.184 1.18-.601 0-1.144-.383-1.144-1.2 0-.823.582-1.21 1.168-1.21.633 0 1.16.398 1.16 1.23"/>
                  <path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5M2 2a1 1 0 0 0-1 1v11a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1z"/>
                  <path d="M2.5 4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5H3a.5.5 0 0 1-.5-.5z"/>
                </svg>
              """
              )
//              img {
//                classes = setOf("bi", "bi-calendar2-date")
//                src = "$BS_BASE/dist/icons/calendar2-date.svg"
//              }

              +"System Version"
            }

            // addBottomOptions()
          }
        }

        div {
          classes = setOf("container-fluid", "overflow-auto")
          id = "console-div"

          pre {
            id = LOG_DIV
            attribs(
              "hx-ext" to "ws",
              "ws-connect" to ADMIN_CONSOLE_ENDPOINT,
            )
          }
        }

        div {
          classes = setOf("container-fluid", "overflow-auto", HIDDEN)
          id = MAIN_DIV
        }

        div {
          classes = setOf("container-fluid", "overflow-auto", HIDDEN)
          id = SYS_INFO_DIV
        }
      }

      js(
        "$BS_BASE/dist/js/bootstrap.bundle.min.js",
        "$STATIC_BASE/js/sidebars.js",
        "$STATIC_BASE/js/admin-support.js",
        "$STATIC_BASE/js/console-support.js",
      )
    }
  }

  private fun HTMLTag.clickAction(
    path: String,
    target: String,
  ) {
    attribs(
      "hx-get" to path,
      "hx-trigger" to "click",
      "hx-target" to "#$target",
      "hx-indicator" to "#spinner",
    )
  }

  private fun UL.displayApplications(
    header: String,
    applications: List<AbstractApplicationImpl>,
    target: String,
  ) {
    li {
      classes += "mb-1"
      button {
        classes =
          setOf("btn", "btn-toggle", "d-inline-flex", "align-items-center", "rounded", "border-0", "ms-3", "collapsed")
        attribs(
          "data-bs-toggle" to "collapse",
          "data-bs-target" to "#$target",
          "aria-expanded" to "true",
        )
//      svg("bi pe-none me-2") { details(16, 16, "table") }
        +header
      }

      div {
        classes = setOf("collapse", "show")
        id = target
        ul {
          classes = setOf("btn-toggle-nav", "list-unstyled", "fw-normal", "pb-1", "small")
          applications.forEach { displayApplicationItems(it) }
        }
      }
    }
  }

  private fun UL.displayApplicationItems(app: AbstractApplicationImpl) {
    li {
      classes = setOf("ms-4")
      a {
        classes =
          setOf("link-body-emphasis", "d-inline-flex", "text-decoration-none", "rounded", "pb-1", "sidebar-menu-item")
        clickAction("$VALIDATE_PATH/${app.fullServerPathWithSecretAsQueryParam}", MAIN_DIV)
        +app.serverPath.ensureStartsWith("/")
      }
    }
  }

  private fun DIV.addBottomOptions() {
    hr {}
    div("dropdown") {
      a(classes = "d-flex align-items-center link-body-emphasis text-decoration-none dropdown-toggle") {
        href = "#"
        attributes["data-bs-toggle"] = "dropdown"
        attributes["aria-expanded"] = "false"
        img(classes = "rounded-circle me-2") {
          src = "https://github.com/mdo.png"
          alt = ""
          width = "32"
          height = "32"
        }
        strong { +"mdo" }
      }
      ul("dropdown-menu text-small shadow") {
        li {
          a(classes = "dropdown-item") {
            href = "#"
            +"New project..."
          }
        }
        li {
          a(classes = "dropdown-item") {
            href = "#"
            +"Settings"
          }
        }
        li {
          a(classes = "dropdown-item") {
            href = "#"
            +"""Profile"""
          }
        }
        li {
          hr("dropdown-divider") {
          }
        }
        li {
          a(classes = "dropdown-item") {
            href = "#"
            +"""Sign out"""
          }
        }
      }
    }
  }

  private fun BODY.addLiveTailButton() {
    div {
      classes = setOf("position-fixed", "top-0", "end-0", "mb-3", "mt-2", "me-4", "bd-mode-toggle")
      id = "live-tail-div"
      button {
        classes = setOf("rounded", "btn", "btn-bd-primary", "m-0", "p-0", "d-flex", "align-items-center")
        id = "live-tail-button"
        type = ButtonType.button
        attribs(
          "data-bs-toggle" to "tooltip",
          "data-bs-placement" to "left",
          "title" to "Live tail",
          "onclick" to "toggleScrolling()"
        )
        rawHtml(
          """
            <i class="bi bi-pause-fill fs-2 m-0 px-2" id="live-tail-icon"></i>
          """
        )
      }
    }
  }

  private fun BODY.addToggleThemeButton() {
    div("dropdown position-fixed bottom-0 end-0 mb-3 me-4 bd-mode-toggle") {
      button(classes = "btn btn-bd-primary py-2 dropdown-toggle d-flex align-items-center") {
        id = "bd-theme"
        type = ButtonType.button
        attribs(
          "aria-expanded" to "false",
          "data-bs-toggle" to "dropdown",
          "aria-label" to "Toggle theme (auto)",
        )
        rawHtml(
          """
                  <svg class="bi my-1 theme-icon-active" width="1em" height="1em">
                      <use href="#circle-half"></use>
                  </svg>
                """,
        )
        span("visually-hidden") {
          id = "bd-theme-text"
          +"Toggle theme"
        }
      }

      val check = """
                  <svg class="bi ms-auto d-none" width="1em" height="1em">
                      <use href="#check2"></use>
                  </svg>
                """
      ul("dropdown-menu dropdown-menu-end shadow") {
        attributes["aria-labelledby"] = "bd-theme-text"
        li {
          button {
            classes = setOf("dropdown-item", "d-flex", "align-items-center")
            type = ButtonType.button
            attribs(
              "data-bs-theme-value" to "light",
              "aria-pressed" to "false",
            )
            rawHtml(
              """
                  <svg class="bi me-2 opacity-50" width="1em" height="1em">
                      <use href="#sun-fill"></use>
                  </svg>
                """,
            )
            +"Light"
            rawHtml(check)
          }
        }

        li {
          button {
            classes = setOf("dropdown-item", "d-flex", "align-items-center")
            type = ButtonType.button
            attribs(
              "data-bs-theme-value" to "dark",
              "aria-pressed" to "false",
            )
            rawHtml(
              """
                  <svg class="bi me-2 opacity-50" width="1em" height="1em">
                      <use href="#moon-stars-fill"></use>
                  </svg>
                """,
            )
            +"Dark"
            rawHtml(check)
          }
        }
        li {
          button {
            classes = setOf("dropdown-item", "d-flex", "align-items-center", ACTIVE)
            type = ButtonType.button
            attribs(
              "data-bs-theme-value" to "auto",
              "aria-pressed" to "true",
            )
            rawHtml(
              """
                  <svg class="bi me-2 opacity-50" width="1em" height="1em">
                      <use href="#circle-half"></use>
                  </svg>
                """,
            )
            +"Auto"
            rawHtml(check)
          }
        }
      }
    }
  }

  private fun BODY.declareSvgs() {
    rawHtml(
      """
          <svg xmlns="http://www.w3.org/2000/svg" class="d-none">
              <symbol id="bootstrap" viewBox="0 0 118 94">
                  <title>Bootstrap</title>
                  <path fill-rule="evenodd" clip-rule="evenodd"
                        d="M24.509 0c-6.733 0-11.715 5.893-11.492 12.284.214 6.14-.064 14.092-2.066 20.577C8.943 39.365 5.547 43.485 0 44.014v5.972c5.547.529 8.943 4.649 10.951 11.153 2.002 6.485 2.28 14.437 2.066 20.577C12.794 88.106 17.776 94 24.51 94H93.5c6.733 0 11.714-5.893 11.491-12.284-.214-6.14.064-14.092 2.066-20.577 2.009-6.504 5.396-10.624 10.943-11.153v-5.972c-5.547-.529-8.934-4.649-10.943-11.153-2.002-6.484-2.28-14.437-2.066-20.577C105.214 5.894 100.233 0 93.5 0H24.508zM80 57.863C80 66.663 73.436 72 62.543 72H44a2 2 0 01-2-2V24a2 2 0 012-2h18.437c9.083 0 15.044 4.92 15.044 12.474 0 5.302-4.01 10.049-9.119 10.88v.277C75.317 46.394 80 51.21 80 57.863zM60.521 28.34H49.948v14.934h8.905c6.884 0 10.68-2.772 10.68-7.727 0-4.643-3.264-7.207-9.012-7.207zM49.948 49.2v16.458H60.91c7.167 0 10.964-2.876 10.964-8.281 0-5.406-3.903-8.178-11.425-8.178H49.948z"></path>
              </symbol>
              <symbol id="home" viewBox="0 0 16 16">
                  <path d="M8.354 1.146a.5.5 0 0 0-.708 0l-6 6A.5.5 0 0 0 1.5 7.5v7a.5.5 0 0 0 .5.5h4.5a.5.5 0 0 0 .5-.5v-4h2v4a.5.5 0 0 0 .5.5H14a.5.5 0 0 0 .5-.5v-7a.5.5 0 0 0-.146-.354L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293L8.354 1.146zM2.5 14V7.707l5.5-5.5 5.5 5.5V14H10v-4a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5v4H2.5z"/>
              </symbol>
              <symbol id="speedometer2" viewBox="0 0 16 16">
                  <path d="M8 4a.5.5 0 0 1 .5.5V6a.5.5 0 0 1-1 0V4.5A.5.5 0 0 1 8 4zM3.732 5.732a.5.5 0 0 1 .707 0l.915.914a.5.5 0 1 1-.708.708l-.914-.915a.5.5 0 0 1 0-.707zM2 10a.5.5 0 0 1 .5-.5h1.586a.5.5 0 0 1 0 1H2.5A.5.5 0 0 1 2 10zm9.5 0a.5.5 0 0 1 .5-.5h1.5a.5.5 0 0 1 0 1H12a.5.5 0 0 1-.5-.5zm.754-4.246a.389.389 0 0 0-.527-.02L7.547 9.31a.91.91 0 1 0 1.302 1.258l3.434-4.297a.389.389 0 0 0-.029-.518z"/>
                  <path fill-rule="evenodd"
                        d="M0 10a8 8 0 1 1 15.547 2.661c-.442 1.253-1.845 1.602-2.932 1.25C11.309 13.488 9.475 13 8 13c-1.474 0-3.31.488-4.615.911-1.087.352-2.49.003-2.932-1.25A7.988 7.988 0 0 1 0 10zm8-7a7 7 0 0 0-6.603 9.329c.203.575.923.876 1.68.63C4.397 12.533 6.358 12 8 12s3.604.532 4.923.96c.757.245 1.477-.056 1.68-.631A7 7 0 0 0 8 3z"/>
              </symbol>
              <symbol id="table" viewBox="0 0 16 16">
                  <path d="M0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm15 2h-4v3h4V4zm0 4h-4v3h4V8zm0 4h-4v3h3a1 1 0 0 0 1-1v-2zm-5 3v-3H6v3h4zm-5 0v-3H1v2a1 1 0 0 0 1 1h3zm-4-4h4V8H1v3zm0-4h4V4H1v3zm5-3v3h4V4H6zm4 4H6v3h4V8z"/>
              </symbol>
              <symbol id="people-circle" viewBox="0 0 16 16">
                  <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
                  <path fill-rule="evenodd"
                        d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>
              </symbol>
              <symbol id="grid" viewBox="0 0 16 16">
                  <path d="M1 2.5A1.5 1.5 0 0 1 2.5 1h3A1.5 1.5 0 0 1 7 2.5v3A1.5 1.5 0 0 1 5.5 7h-3A1.5 1.5 0 0 1 1 5.5v-3zM2.5 2a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3zm6.5.5A1.5 1.5 0 0 1 10.5 1h3A1.5 1.5 0 0 1 15 2.5v3A1.5 1.5 0 0 1 13.5 7h-3A1.5 1.5 0 0 1 9 5.5v-3zm1.5-.5a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3zM1 10.5A1.5 1.5 0 0 1 2.5 9h3A1.5 1.5 0 0 1 7 10.5v3A1.5 1.5 0 0 1 5.5 15h-3A1.5 1.5 0 0 1 1 13.5v-3zm1.5-.5a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3zm6.5.5A1.5 1.5 0 0 1 10.5 9h3a1.5 1.5 0 0 1 1.5 1.5v3a1.5 1.5 0 0 1-1.5 1.5h-3A1.5 1.5 0 0 1 9 13.5v-3zm1.5-.5a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3z"/>
              </symbol>
          </svg>
        """,
    )
  }
}
