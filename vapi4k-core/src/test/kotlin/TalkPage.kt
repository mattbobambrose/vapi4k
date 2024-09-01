import com.vapi4k.api.web.MethodType
import com.vapi4k.dsl.web.VapiWeb.vapi
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
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
          h1 { +"Talk with an Assistant" }
          vapi {
            talkButton {
              serverPath = "/talkapp?x=1&b=2"
              serverSecret = "12345"
              vapiPublicApiKey = "c1492df9-e59f-4e06-a9ab-54f44df44f66"
              method = MethodType.POST
              postArgs = """
              {"x": "1",
               "y": "2",
               "position": "BOTTOM",
               "name": "Ellen"
               }""".toJsonElement()
            }
          }
        }
      }
}
