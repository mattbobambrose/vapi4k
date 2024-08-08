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
import com.vapi4k.common.Constants.STYLES_CSS
import com.vapi4k.common.Endpoints.VALIDATE_PATH
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul

internal object ValidateRoot {
  suspend fun KtorCallContext.validateRoot(config: Vapi4kConfigImpl) {
    if (config.applications.size == 1) {
      val application = config.applications.first()
      call.respondRedirect("$VALIDATE_PATH/${application.serverPathAsSegment}?secret=${application.serverSecret}")
    } else {
      val html = createHTML()
        .html {
          head {
            link {
              rel = "stylesheet"
              href = STYLES_CSS
            }
            title { +"Assistant Request Validation" }
            script { src = HTMX_SOURCE_URL }
          }
          body {
            h2 {
              +"All Vapi4k Applications"
            }
            ul {
              config.allApplications.forEach { application ->
                li {
                  a {
                    href = "$VALIDATE_PATH/${application.serverPathAsSegment}?secret=${application.serverSecret}"
                    +application.serverPath
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
