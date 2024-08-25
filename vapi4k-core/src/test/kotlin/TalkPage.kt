import com.vapi4k.common.Constants.STATIC_BASE
import com.vapi4k.common.CoreEnvVars.serverBaseUrl
import com.vapi4k.utils.HtmlUtils.rawHtml
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title

object TalkPage {
  fun talkPage() =
    createHTML()
      .html {
        head {
          title { +"Talk with an Assistant" }
        }
        body {
          script {
            rawHtml(
              """
//                const vapi4kUrl = 'https://eocare-app-fiqm5.ondigitalocean.app/talkapp?a=1&b=2';
                const vapi4kUrl = '$serverBaseUrl/talkapp?a=1&b=2';
                const serverSecret = '12345';
                const userArgs = {x: "1", y: "2", position: "BOTTOM", name: "Ellen"};
                const publicApiKey = "c1492df9-e59f-4e06-a9ab-54f44df44f66";
                const method = "POST";
              """.trimIndent()
            )
          }
          script { src = "$STATIC_BASE/js/vapi-call.js" }
          h1 { +"Talk with an Assistant" }
        }
      }
}
