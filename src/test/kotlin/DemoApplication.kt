import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.server.Vapi4k
import com.vapi4k.server.Vapi4kServer.logger
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer


fun main() {
  embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module2)
    .start(wait = true)
}

fun Application.module2() {
  install(Vapi4k) {
    val BASE_URL = "https://eocare-app-fiqm5.ondigitalocean.app"

    configure {
      serverUrl = "$BASE_URL/vapi4k"
      serverUrlSecret = "12345"
    }

    onAssistantRequest { assistantRequest ->
      logger.info { "Assistant request has been made: $assistantRequest" }
      assistant(assistantRequest) {

      }
    }
  }
}
