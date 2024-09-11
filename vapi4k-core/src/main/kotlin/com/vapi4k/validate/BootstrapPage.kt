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

import com.vapi4k.common.Constants.BS_BASE
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.utils.HtmlUtils.rawHtml
import kotlinx.html.ButtonType
import kotlinx.html.HTML
import kotlinx.html.HTMLTag
import kotlinx.html.MAIN
import kotlinx.html.SVG
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.role
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.style
import kotlinx.html.svg
import kotlinx.html.title
import kotlinx.html.ul

internal object BootstrapPage {
  fun HTMLTag.attribs(vararg pairs: Pair<String, String>) {
    pairs.forEach { (k, v) -> attributes[k] = v }
  }

  fun SVG.details(
    width: Int,
    height: Int,
    href: String,
  ) {
    attributes["width"] = width.toString()
    attributes["height"] = height.toString()
    rawHtml(
      """
                <use xlink:href="#$href"/>
        """
    )
  }

  fun HTML.bootstrapPage() {
    head {
      meta {
        name = "viewport"
        content = "width=device-width, initial-scale=1"
      }
      link {
        rel = "stylesheet"
        href = "https://cdn.jsdelivr.net/npm/@docsearch/css@3"
      }
      link {
        rel = "stylesheet"
        href = "$BS_BASE/dist/css/bootstrap.min.css"
      }

      link {
        rel = "stylesheet"
        href = "$STATIC_BASE/css/sidebars2.css"
      }

      link {
        rel = "stylesheet"
        href = "$STATIC_BASE/css/sidebars.css"
      }
      script {
        src = "$STATIC_BASE/js/color-modes.js"
      }
      title {
      }
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
        """
      )


      div("dropdown position-fixed bottom-0 end-0 mb-3 me-3 bd-mode-toggle") {
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
              """
          )
          span("visually-hidden") {
            id = "bd-theme-text"
            +"Toggle theme"
          }
        }

        ul("dropdown-menu dropdown-menu-end shadow") {
          attributes["aria-labelledby"] = "bd-theme-text"
          li {
            button(classes = "dropdown-item d-flex align-items-center") {
              type = ButtonType.button
              attributes["data-bs-theme-value"] = "light"
              attributes["aria-pressed"] = "false"
              rawHtml(
                """
                <svg class="bi me-2 opacity-50" width="1em" height="1em">
                    <use href="#sun-fill"></use>
                </svg>
              """
              )
              +"Light"
              rawHtml(
                """
                <svg class="bi ms-auto d-none" width="1em" height="1em">
                    <use href="#check2"></use>
                </svg>
              """
              )
            }
          }

          li {
            button(classes = "dropdown-item d-flex align-items-center") {
              type = ButtonType.button
              attributes["data-bs-theme-value"] = "dark"
              attributes["aria-pressed"] = "false"
              rawHtml(
                """
                <svg class="bi me-2 opacity-50" width="1em" height="1em">
                    <use href="#moon-stars-fill"></use>
                </svg>
              """
              )
              +"Dark"
              rawHtml(
                """
                <svg class="bi ms-auto d-none" width="1em" height="1em">
                    <use href="#check2"></use>
                </svg>
              """
              )
            }
          }
          li {
            button(classes = "dropdown-item d-flex align-items-center active") {
              type = ButtonType.button
              attributes["data-bs-theme-value"] = "auto"
              attributes["aria-pressed"] = "true"
              rawHtml(
                """
                <svg class="bi me-2 opacity-50" width="1em" height="1em">
                    <use href="#circle-half"></use>
                </svg>
              """
              )
              +"Auto"
              rawHtml(
                """
                <svg class="bi ms-auto d-none" width="1em" height="1em">
                    <use href="#check2"></use>
                </svg>
              """
              )
            }
          }
        }
      }

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
      """
      )

      main("d-flex flex-nowrap") {
        h1("visually-hidden") { +"Sidebars examples" }

        //column1()
        // spacer()

        div("d-flex flex-column flex-shrink-0 p-3 bg-body-tertiary") {
          style = "width: 280px;"
          a(classes = "d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none") {
            href = "/"
            svg("bi pe-none me-2") {
              details(40, 32, "bootstrap")
            }
            span("fs-4") { +"Vapi4k Admin" }
          }

          hr {}

          ul("nav nav-pills flex-column mb-auto") {

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


            button(classes = "btn  d-inline-flex align-items-center rounded border-0 collapsed") {
              attribs(
                "data-bs-toggle" to "collapse",
                "data-bs-target" to "#validator-collapse",
                "aria-expanded" to "true",
              )
              svg("bi pe-none me-2") { details(16, 16, "table") }
              +"Application Validator"
            }

            li("mb-1") {
              button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
                attribs(
                  "data-bs-toggle" to "collapse",
                  "data-bs-target" to "#inbound-collapse",
                  "aria-expanded" to "true",
                )
//                svg("bi pe-none me-2") { details(16, 16, "table") }
                +"InboundCall Applications"
              }

              div("collapse show") {
                id = "inbound-collapse"

                ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"OutboundCall Applications"
                    }
                  }

                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"InboundCall Applications"
                    }
                  }

                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"Web Applications"
                    }
                  }
                }
              }
            }

            li("mb-1") {
              button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
                attribs(
                  "data-bs-toggle" to "collapse",
                  "data-bs-target" to "#outbound-collapse",
                  "aria-expanded" to "true",
                )
//                svg("bi pe-none me-2") { details(16, 16, "table") }
                +"OutboundCall Applications"
              }

              div("collapse show") {
                id = "outbound-collapse"

                ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"OutboundCall Applications"
                    }
                  }

                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"InboundCall Applications"
                    }
                  }

                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"Web Applications"
                    }
                  }
                }
              }
            }

            li("mb-1") {
              button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
                attribs(
                  "data-bs-toggle" to "collapse",
                  "data-bs-target" to "#web-collapse",
                  "aria-expanded" to "true",
                )
//                svg("bi pe-none me-2") { details(16, 16, "table") }
                +"Web Applications"
              }

              div("collapse show") {
                id = "web-collapse"

                ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"OutboundCall Applications"
                    }
                  }

                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"InboundCall Applications"
                    }
                  }

                  li {
                    a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                      href = "#"
                      +"Web Applications"
                    }
                  }
                }
              }
            }

//            li {
//              a(classes = "nav-link link-body-emphasis") {
//                href = "#"
//                svg("bi pe-none me-2") {
//                  details(16, 16, "speedometer2")
//                }
//                +"Dashboard"
//              }
//            }
//
//            li {
//              a(classes = "nav-link link-body-emphasis") {
//                href = "#"
//                svg("bi pe-none me-2") {
//                  details(16, 16, "table")
//                }
//                +"Orders"
//              }
//            }
//
//            li {
//              a(classes = "nav-link link-body-emphasis") {
//                href = "#"
//                svg("bi pe-none me-2") {
//                  details(16, 16, "grid")
//                }
//                +"Products"
//              }
//            }
//
//            li {
//              a(classes = "nav-link link-body-emphasis") {
//                href = "#"
//                svg("bi pe-none me-2") {
//                  details(16, 16, "people-circle")
//                }
//                +"Customers"
//              }
//            }
          }

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
              strong { +"""mdoyyy""" }
            }
            ul("dropdown-menu text-small shadow") {
              li {
                a(classes = "dropdown-item") {
                  href = "#"
                  +"""New project..."""
                }
              }
              li {
                a(classes = "dropdown-item") {
                  href = "#"
                  +"""Settings"""
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

        spacer()


//        iconOnly()
//        spacer()

//        collapsible()
//        spacer()
//        spacer()
      }


      script { src = "$BS_BASE/dist/js/bootstrap.bundle.min.js" }
      script { src = "$STATIC_BASE/js/sidebars.js" }
    }
  }

  private fun MAIN.collapsible() {
    div("flex-shrink-0 p-3") {
      style = "width: 280px;"
      a(classes = "d-flex align-items-center pb-3 mb-3 link-body-emphasis text-decoration-none border-bottom") {
        href = "/"
        svg("bi pe-none me-2") {
          details(30, 24, "bootstrap")
        }
        span("fs-5 fw-semibold") { +"""Collapsible""" }
      }
      ul("list-unstyled ps-0") {
        li("mb-1") {
          button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
            attributes["data-bs-toggle"] = "collapse"
            attributes["data-bs-target"] = "#home-collapse"
            attributes["aria-expanded"] = "true"
            +"""Home"""
          }
          div("collapse show") {
            id = "home-collapse"
            ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Overview"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Updates"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Reports"""
                }
              }
            }
          }
        }
        li("mb-1") {
          button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
            attributes["data-bs-toggle"] = "collapse"
            attributes["data-bs-target"] = "#dashboard-collapse"
            attributes["aria-expanded"] = "false"
            +"""Dashboard"""
          }
          div("collapse") {
            id = "dashboard-collapse"
            ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Overview"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Weekly"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Monthly"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Annually"""
                }
              }
            }
          }
        }
        li("mb-1") {
          button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
            attributes["data-bs-toggle"] = "collapse"
            attributes["data-bs-target"] = "#orders-collapse"
            attributes["aria-expanded"] = "false"
            +"""Orders"""
          }
          div("collapse") {
            id = "orders-collapse"
            ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""New"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Processed"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Shipped"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Returned"""
                }
              }
            }
          }
        }
        li("border-top my-3") {
        }
        li("mb-1") {
          button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed") {
            attributes["data-bs-toggle"] = "collapse"
            attributes["data-bs-target"] = "#account-collapse"
            attributes["aria-expanded"] = "false"
            +"""Account"""
          }
          div("collapse") {
            id = "account-collapse"
            ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""New..."""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Profile"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Settings"""
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded") {
                  href = "#"
                  +"""Sign
                              out"""
                }
              }
            }
          }
        }
      }
    }
  }

  private fun MAIN.iconOnly() {
    div("d-flex flex-column flex-shrink-0 bg-body-tertiary") {
      style = "width: 4.5rem;"
      a(classes = "d-block p-3 link-body-emphasis text-decoration-none") {
        href = "/"
        title = "Icon-only"
        attributes["data-bs-toggle"] = "tooltip"
        attributes["data-bs-placement"] = "right"
        svg("bi pe-none") {
          details(40, 32, "bootstrap")
        }
        span("visually-hidden") { +"""Icon-only""" }
      }
      ul("nav nav-pills nav-flush flex-column mb-auto text-center") {
        li("nav-item") {
          a(classes = "nav-link active py-3 border-bottom rounded-0") {
            href = "#"
            attributes["aria-current"] = "page"
            title = "Home"
            attributes["data-bs-toggle"] = "tooltip"
            attributes["data-bs-placement"] = "right"
            svg("bi pe-none") {
              attributes["aria-label"] = "Home"
              role = "img"
              details(24, 24, "home")
            }
          }
        }
        li {
          a(classes = "nav-link py-3 border-bottom rounded-0") {
            href = "#"
            title = "Dashboard"
            attributes["data-bs-toggle"] = "tooltip"
            attributes["data-bs-placement"] = "right"
            svg("bi pe-none") {
              attributes["aria-label"] = "Dashboard"
              role = "img"
              details(24, 24, "speedometer2")
            }
          }
        }
        li {
          a(classes = "nav-link py-3 border-bottom rounded-0") {
            href = "#"
            title = "Orders"
            attributes["data-bs-toggle"] = "tooltip"
            attributes["data-bs-placement"] = "right"
            svg("bi pe-none") {
              attributes["aria-label"] = "Orders"
              role = "img"
              details(24, 24, "table")
            }
          }
        }
        li {
          a(classes = "nav-link py-3 border-bottom rounded-0") {
            href = "#"
            title = "Products"
            attributes["data-bs-toggle"] = "tooltip"
            attributes["data-bs-placement"] = "right"
            svg("bi pe-none") {
              attributes["aria-label"] = "Products"
              role = "img"
              details(24, 24, "grid")
            }
          }
        }
        li {
          a(classes = "nav-link py-3 border-bottom rounded-0") {
            href = "#"
            title = "Customers"
            attributes["data-bs-toggle"] = "tooltip"
            attributes["data-bs-placement"] = "right"
            svg("bi pe-none") {
              attributes["aria-label"] = "Customers"
              role = "img"
              details(24, 24, "people-circle")
            }
          }
        }
      }
      div("dropdown border-top") {
        a(classes = "d-flex align-items-center justify-content-center p-3 link-body-emphasis text-decoration-none dropdown-toggle") {
          href = "#"
          attributes["data-bs-toggle"] = "dropdown"
          attributes["aria-expanded"] = "false"
          img(classes = "rounded-circle") {
            src = "https://github.com/mdo.png"
            alt = "mdo"
            width = "24"
            height = "24"
          }
        }
        ul("dropdown-menu text-small shadow") {
          li {
            a(classes = "dropdown-item") {
              href = "#"
              +"""New project..."""
            }
          }
          li {
            a(classes = "dropdown-item") {
              href = "#"
              +"""Settings"""
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
  }

  private fun MAIN.spacer() {
    div("b-example-divider b-example-vr") {}
  }

  private fun MAIN.column1() {
    div("d-flex flex-column flex-shrink-0 p-3 text-bg-dark") {
      style = "width: 280px;"
      a(classes = "d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none") {
        href = "/"
        svg("bi pe-none me-2") {
          details(40, 32, "bootstrap")
        }
        span("fs-4") { +"""Sidebar""" }
      }
      hr {
      }
      ul("nav nav-pills flex-column mb-auto") {
        li("nav-item") {
          a(classes = "nav-link active") {
            href = "#"
            attributes["aria-current"] = "page"
            svg("bi pe-none me-2") {
              details(16, 16, "home")
            }
            +"Home"
          }
        }
        li {
          a(classes = "nav-link text-white") {
            href = "#"
            svg("bi pe-none me-2") {
              details(16, 16, "speedometer2")
            }
            +"Dashboard"
          }
        }
        li {
          a(classes = "nav-link text-white") {
            href = "#"
            svg("bi pe-none me-2") {
              details(16, 16, "table")
            }
            +"Orders"
          }
        }
        li("mb-1") {
          button(classes = "btn btn-toggle d-inline-flex align-items-center rounded border-0 collapsed text-white") {
            attributes["data-bs-toggle"] = "collapse"
            attributes["data-bs-target"] = "#home-collapse"
            attributes["aria-expanded"] = "true"
            svg("bi pe-none me-2") {
              details(16, 16, "table")
            }
            +"Home"
          }
          div("collapse show") {
            id = "home-collapse"
            ul("btn-toggle-nav list-unstyled fw-normal pb-1 small") {
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded text-white") {
                  href = "#"
                  +"Overview"
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded text-white") {
                  href = "#"
                  +"Updates"
                }
              }
              li {
                a(classes = "link-body-emphasis d-inline-flex text-decoration-none rounded text-white") {
                  href = "#"
                  +"""Reports"""
                }
              }
            }
          }
        }
        li {
          a(classes = "nav-link text-white") {
            href = "#"
            svg("bi pe-none me-2") {
              details(16, 16, "grid")
            }
            +"Products"
          }
        }
        li {
          a(classes = "nav-link text-white") {
            href = "#"
            svg("bi pe-none me-2") {
              details(16, 16, "people-circle")
            }
            +"Customers"
          }
        }
      }
      hr {}
      div("dropdown") {
        a(classes = "d-flex align-items-center text-white text-decoration-none dropdown-toggle") {
          href = "#"
          attributes["data-bs-toggle"] = "dropdown"
          attributes["aria-expanded"] = "false"
          img(classes = "rounded-circle me-2") {
            src = "https://github.com/mdo.png"
            alt = ""
            width = "32"
            height = "32"
          }
          strong { +"""mdozzz""" }
        }
        ul("dropdown-menu dropdown-menu-dark text-small shadow") {
          li {
            a(classes = "dropdown-item") {
              href = "#"
              +"""New project..."""
            }
          }
          li {
            a(classes = "dropdown-item") {
              href = "#"
              +"""Settings"""
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
  }
}
