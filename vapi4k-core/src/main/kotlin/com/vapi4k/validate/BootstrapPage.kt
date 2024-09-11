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

import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.utils.HtmlUtils.rawHtml
import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.SVG
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.option
import kotlinx.html.role
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.title
import kotlinx.html.ul

fun SVG.use(href: String) = rawHtml(""" <use xlink:href="$href"></use> """)

internal object BootstrapPage {
  fun HTML.bootstrapPage() {
    head {
      meta { charset = "UTF-8" }
      meta {
        name = "viewport"
        content = "width=device-width, initial-scale=1.0"
      }
      link {
        href = "$STATIC_BASE/css/bootstrap.min.css"
        rel = "stylesheet"
      }
      title { +"Assistant Application Validation" }
    }
    body {

      div("container-fluid") {

        div("d-flex flex-row mb-3") {
          ul("nav flex-column") {
            li("nav-item") {
              a(classes = "nav-link active") {
                attributes["aria-current"] = "page"
                href = "#"
                +"Active"
              }
            }
            li("nav-item dropdown") {
              a(classes = "nav-link dropdown-toggle") {
                attributes["data-bs-toggle"] = "dropdown"
                href = "#"
                role = "button"
                attributes["aria-expanded"] = "false"
                +"Dropdown"
              }
              ul("dropdown-menu") {
                li {
                  a(classes = "dropdown-item") {
                    href = "#"
                    +"Action"
                  }
                }
                li {
                  a(classes = "dropdown-item") {
                    href = "#"
                    +"Another action"
                  }
                }
                li {
                  a(classes = "dropdown-item") {
                    href = "#"
                    +"Something else here"
                  }
                }
                li {
                  hr("dropdown-divider") {
                  }
                }
                li {
                  a(classes = "dropdown-item") {
                    href = "#"
                    +"Separated link"
                  }
                }
              }
            }
            li("nav-item") {
              a(classes = "nav-link") {
                href = "#"
                +"Link"
              }
            }
            li("nav-item") {
              a(classes = "nav-link disabled") {
                attributes["aria-disabled"] = "true"
                +"Disabled"
              }
            }
          }

          div("container") {
            +"Hello, world!"
          }
        }
      }

      // addForm()

      script {
        attributes["crossorigin"] = "anonymous"
        src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity = "sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
      }
    }
  }

  private fun BODY.addForm() {
    form(classes = "row g-3 needs-validation") {
      novalidate = true
      div("col-md-4") {
        label("form-label") {
          htmlFor = "validationCustom01"
          +"First name"
        }
        input(classes = "form-control") {
          type = InputType.text
          id = "validationCustom01"
          value = "Mark"
          required = true
        }
        div("valid-feedback") { +"Looks good!" }
      }
      div("col-md-4") {
        label("form-label") {
          htmlFor = "validationCustom02"
          +"Last name"
        }
        input(classes = "form-control") {
          type = InputType.text
          id = "validationCustom02"
          value = "Otto"
          required = true
        }
        div("valid-feedback") { +"Looks good!" }
      }
      div("col-md-4") {
        label("form-label") {
          htmlFor = "validationCustomUsername"
          +"Username"
        }
        div("input-group has-validation") {
          span("input-group-text") {
            id = "inputGroupPrepend"
            +"@"
          }
          input(classes = "form-control") {
            type = InputType.text
            id = "validationCustomUsername"
            attributes["aria-describedby"] = "inputGroupPrepend"
            required = true
          }
          div("invalid-feedback") { +"Please choose a username." }
        }
      }
      div("col-md-6") {
        label("form-label") {
          htmlFor = "validationCustom03"
          +"City"
        }
        input(classes = "form-control") {
          type = InputType.text
          id = "validationCustom03"
          required = true
        }
        div("invalid-feedback") { +"Please provide a valid city." }
      }
      div("col-md-3") {
        label("form-label") {
          htmlFor = "validationCustom04"
          +"State"
        }
        select("form-select") {
          id = "validationCustom04"
          required = true
          option {
            selected = true
            disabled = true
            value = ""
            +"Choose..."
          }
          option { +"..." }
        }
        div("invalid-feedback") { +"Please select a valid state." }
      }
      div("col-md-3") {
        label("form-label") {
          htmlFor = "validationCustom05"
          +"Zip"
        }
        input(classes = "form-control") {
          type = InputType.text
          id = "validationCustom05"
          required = true
        }
        div("invalid-feedback") { +"Please provide a valid zip." }
      }
      div("col-12") {
        div("form-check") {
          input(classes = "form-check-input") {
            type = InputType.checkBox
            value = ""
            id = "invalidCheck"
            required = true
          }
          label("form-check-label") {
            htmlFor = "invalidCheck"
            +"Agree to terms and conditions"
          }
          div("invalid-feedback") { +"You must agree before submitting." }
        }
      }
      div("col-12") {
        button(classes = "btn btn-primary") {
          type = ButtonType.submit
          +"Submit form"
        }
      }
    }
  }
}
