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
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.KtorCallContext
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.utils.common.Utils.ensureStartsWith
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul

internal object ValidateRootPage {
  suspend fun KtorCallContext.validateRootPage(config: Vapi4kConfigImpl) {
    if (config.allWebAndInboundApplications.size == 1) {
      val app = config.allWebAndInboundApplications.first()
      call.respondRedirect("$VALIDATE_PATH/${app.fullServerPathWithSecretAsQueryParam}")
    } else {
      val html = createHTML()
        .html {
          head {
            meta { charset = "UTF-8" }
            meta {
              name = "viewport"
              content = "width=device-width, initial-scale=1.0"
            }
            link {
              rel = "stylesheet"
              href = "$STATIC_BASE/css/styles.css"
            }
            link {
              rel = "stylesheet"
              href = "$STATIC_BASE/css/validator.css"
            }
            title { +"Assistant Application Validation" }
            // script { src = HTMX_SOURCE_URL }
            //  script { src = "https://cdn.tailwindcss.com" }
          }
          body {
            h1 { +"Vapi4k Application Validator" }

            config.inboundCallApplications.also { apps ->
              h2 { +"${if (apps.isEmpty()) "No " else ""}InboundCall Applications" }
              ul { apps.forEach { applicationDetails(it) } }
            }

            config.outboundCallApplications.also { apps ->
              h2 { +"${if (apps.isEmpty()) "No " else ""}OutboundCall Applications" }
              ul { apps.forEach { applicationDetails(it) } }
            }

            config.webApplications.also { apps ->
              h2 { +"${if (apps.isEmpty()) "No " else ""}Web Applications" }
              ul { apps.forEach { applicationDetails(it) } }
            }
          }
        }
      call.respondText(html, ContentType.Text.Html)
    }
  }

  private fun UL.applicationDetails(application: AbstractApplicationImpl) {
    li {
      id = "all-li"
      a {
        href = "$VALIDATE_PATH/${application.fullServerPathWithSecretAsQueryParam}"
        +application.fullServerPath.ensureStartsWith("/")
      }
    }
  }
}
