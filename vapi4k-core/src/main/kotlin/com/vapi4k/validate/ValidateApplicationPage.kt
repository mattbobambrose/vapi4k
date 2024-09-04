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
import com.vapi4k.common.ApplicationName.Companion.toApplicationName
import com.vapi4k.common.Constants.APP_NAME
import com.vapi4k.common.Constants.APP_TYPE
import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.common.QueryParams.SECRET_PARAM
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dsl.vapi4k.ApplicationType.INBOUND_CALL
import com.vapi4k.dsl.vapi4k.ApplicationType.OUTBOUND_CALL
import com.vapi4k.dsl.vapi4k.ApplicationType.WEB
import com.vapi4k.dsl.vapi4k.KtorCallContext
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.utils.HttpUtils.getHeader
import com.vapi4k.utils.HttpUtils.getQueryParam
import com.vapi4k.utils.common.Utils.isNotNull
import com.vapi4k.utils.common.Utils.toErrorString
import com.vapi4k.validate.ValidateAssistantRequestPage.validateAssistantRequestPage
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import kotlinx.html.body
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import java.net.ConnectException

internal object ValidateApplicationPage {
  suspend fun KtorCallContext.validateApplicationPage(config: Vapi4kConfigImpl) =
    runCatching {
      val appType = call.parameters[APP_TYPE].orEmpty()
      val appName = call.parameters[APP_NAME].orEmpty().toApplicationName()
      val app =
        when (appType) {
          WEB.pathPrefix -> config.webApplications
          INBOUND_CALL.pathPrefix -> config.inboundCallApplications
          OUTBOUND_CALL.pathPrefix -> config.outboundCallApplications
          else -> error("Invalid application type: $appType")
        }.firstOrNull { it.serverPathNoSlash == appName.value }

      if (app.isNotNull())
        processValidateRequest(config, app, appName)
      else
        call.respondText("Application for /${appName.value} not found", status = HttpStatusCode.NotFound)
    }.getOrElse {
      if (it is ConnectException) {
        val html = serverBasePage()
        call.respondText(html, ContentType.Text.Html)
      } else {
        logger.error(it) { "Error validating application" }
        call.respondText(it.toErrorString(), status = HttpStatusCode.InternalServerError)
      }
    }

  private fun serverBasePage() =
    createHTML()
      .html {
        head {
          title { +"Assistant Request Validation" }
        }
        body {
          h2 { +"Configuration Error" }
          p {
            +"Please set the environment variable VAPI4K_BASE_URL =  "
            span {
              id = "serverBaseUrl"
            }
          }
          script { src = "$STATIC_BASE/js/server-base.js" }
        }
      }

  private suspend fun KtorCallContext.processValidateRequest(
    config: Vapi4kConfigImpl,
    application: AbstractApplicationImpl,
    appName: ApplicationName,
  ) {
    val secret = call.getQueryParam(SECRET_PARAM).orEmpty()
    val html = validateAssistantRequestPage(config, application, appName, secret)
    call.respondText(html, ContentType.Text.Html)
  }

  internal fun KtorCallContext.isValidSecret(configPropertiesSecret: String): Boolean {
    val secret = call.getHeader(VAPI_SECRET_HEADER)
    return (configPropertiesSecret.isBlank() || secret.trim() == configPropertiesSecret.trim()).also {
      if (!it) {
        logger.info { """Invalid secret. Found: "$secret" Expected: "$configPropertiesSecret"""" }
      }
    }
  }
}
