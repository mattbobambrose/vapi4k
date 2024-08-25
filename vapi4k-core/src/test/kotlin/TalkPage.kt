import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CoreEnvVars.serverBaseUrl
import com.vapi4k.utils.HtmlUtils.rawHtml
import com.vapi4k.utils.JsonElementUtils.EMPTY_JSON_ELEMENT
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.html.BODY
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.serialization.json.JsonElement

object TalkPage {
  fun talkPage() =
    createHTML()
      .html {
        head {
          title { +"Talk with an Assistant" }
        }
        body {

          addVapiTalkButton(
            vapi4kUrl = "$serverBaseUrl/talkapp?a=1&b=2",
            serverSecret = "12345",
            vapiApiKey = "c1492df9-e59f-4e06-a9ab-54f44df44f66",
            postArgs = """{"x": "1", "y": "2", "position": "BOTTOM", "name": "Ellen"}""".toJsonElement(),
          )
          h1 { +"Talk with an Assistant" }
        }
      }
}

fun BODY.addVapiTalkButton(
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
