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
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h4
import kotlinx.html.head
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title

internal object TailwindPage {
  fun HTML.tailwindPage() {
    head {
      meta { charset = "UTF-8" }
      meta {
        name = "viewport"
        content = "width=device-width, initial-scale=1.0"
      }
      link {
        rel = "stylesheet"
        href = "$STATIC_BASE/css/tailwind.css"
      }
      title { +"Assistant Application Validation" }
      // script { src = HTMX_SOURCE_URL }
      script { src = "https://cdn.tailwindcss.com" }
    }
    body {
      div("chat-notification") {
        div("chat-notification-logo-wrapper") {
          img(classes = "chat-notification-logo") {
            src = "/img/logo.svg"
            alt = "ChitChat Logo"
          }
        }
        div("chat-notification-content") {
          h4("chat-notification-title") { +"""ChitChat""" }
          p("chat-notification-message") { +"""You have a new message!""" }
        }
      }

      div("p-6 max-w-sm mx-auto bg-white rounded-xl shadow-lg flex items-center space-x-4") {
        div("shrink-0") {
          img(classes = "size-12") {
            src = "/img/logo.svg"
            alt = "ChitChat Logo"
          }
        }
        div {
          div("text-xl font-medium text-black") { +"""ChitChat""" }
          p("text-slate-500") { +"""You have a new message!""" }
        }
      }
    }
  }
}
