import SimpleSquad.doubleToolAssistant2
import com.vapi4k.server.Vapi4k
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun main() {
  embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module2)
    .start(wait = true)
}

const val BASE_URL = "https://app.ondigitalocean.app"

fun Application.module2() {
  install(Vapi4k) {
    configure {
      serverUrl = "$BASE_URL/vapi4k"
      serverUrlSecret = "12345"
    }

    onAssistantRequest { assistantRequest ->
//      AssistantId with tools
//      SquadId with tools
      doubleToolAssistant2(assistantRequest)
      //simpleAssistantRequest(assistantRequest)
    }
  }
}
