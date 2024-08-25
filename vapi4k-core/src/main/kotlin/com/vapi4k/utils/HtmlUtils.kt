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

package com.vapi4k.utils

import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.utils.JsonElementUtils.EMPTY_JSON_ELEMENT
import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.html.BODY
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import kotlinx.html.unsafe
import kotlinx.serialization.json.JsonElement

object HtmlUtils {
  fun HTMLTag.rawHtml(html: String) = unsafe { raw(html) }

  // Creates snippets of HTML for use with HTMX
  fun html(block: TagConsumer<StringBuilder>.() -> Unit): String =
    buildString {
      appendHTML().apply(block)
    }

  fun BODY.vapiTalkButton(
    vapi4kUrl: String,
    vapiApiKey: String,
    serverSecret: String = "",
    method: String = "POST",
    postArgs: JsonElement = EMPTY_JSON_ELEMENT,
  ) {
    script {
      val args = buildString {
        appendLine("const vapi4kUrl = '$vapi4kUrl';")
        appendLine("const serverSecret = '$serverSecret';")
        appendLine("const vapiApiKey = '$vapiApiKey';")
        appendLine("const method = '${method.toUpperCasePreservingASCIIRules()}';")
        appendLine("const postArgs = '$postArgs';")
      }
      rawHtml(args)
    }
    script { src = "$STATIC_BASE/js/vapi-call.js" }

  }

}
