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

package com.vapi4k.server

import com.vapi4k.common.Constants.HTMX_SOURCE_URL
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.utils.common.Utils.ensureStartsWith
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul

internal object ValidateRoot {
  suspend fun KtorCallContext.validateRootPage(config: Vapi4kConfigImpl) {
    when {
      config.inboundCallApplications.size == 1 && config.webApplications.isEmpty() -> {
        val application = config.inboundCallApplications.first()
        call.respondRedirect("$VALIDATE_PATH/${application.serverPathWithSecret}")
      }

      config.webApplications.size == 1 && config.inboundCallApplications.isEmpty() -> {
        val application = config.webApplications.first()
        call.respondRedirect("$VALIDATE_PATH/${application.serverPathAsSegment}")
      }

      else -> {
        val html = createHTML()
          .html {
            head {
              link {
                rel = "stylesheet"
                href = "$STATIC_BASE/css/styles.css"
              }
              link {
                rel = "stylesheet"
                href = "$STATIC_BASE/css/validator.css"
              }
              title { +"Assistant Request Validation" }
              script { src = HTMX_SOURCE_URL }
            }
            body {
              h1 { +"Vapi4k Validator" }
              h2 { +"InboundCall Applications" }
              ul {
                config.inboundCallApplications
                  .forEach { application ->
                    li {
                      id = "all-li"
                      a {
                        href = "$VALIDATE_PATH/${application.serverPathWithSecret}"
                        +application.serverPath.ensureStartsWith("/")
                      }
                    }
                  }
              }
              h2 { +"Web Applications" }
              ul {
                config.webApplications
                  .forEach { application ->
                    li {
                      id = "all-li"
                      a {
                        href = "$VALIDATE_PATH/${application.serverPathAsSegment}"
                        +application.serverPath.ensureStartsWith("/")
                      }
                    }
                  }
              }
            }
          }
        call.respondText(html, ContentType.Text.Html)
      }
    }
  }
}
