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

package com.vapi4k.dsl.web

import com.vapi4k.api.web.TalkButton
import com.vapi4k.api.web.VapiHtml
import com.vapi4k.common.CoreEnvVars.vapi4kBaseUrl
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.MiscUtils.removeEnds
import kotlinx.html.HtmlBlockTag
import kotlinx.html.script

class VapiHtmlImpl(
  val htmlContext: HtmlBlockTag,
) : VapiHtml {
  override fun talkButton(block: TalkButton.() -> Unit) {
    val props = TalkButtonProperties()
    TalkButtonImpl(props).apply(block)
    props.verifyTalkButtonValues()
    with(htmlContext) {
      script {
        val indent = "\t\t\t"
        val args = buildString {
          appendLine()
          append("\t\taddVapiButton(\n$indent")
          appendLine(
            listOf(
              "'$vapi4kBaseUrl/${props.serverPath.removeEnds("/")}'",
              "'${props.serverSecret}'",
              "'${props.vapiPublicApiKey}'",
              "'${props.method.name}'",
              "JSON.parse('${props.postArgs}')",
            ).joinToString(",\n$indent"),
          )
          appendLine("\t\t);")
        }
        rawHtml(args)
      }
    }
  }
}
