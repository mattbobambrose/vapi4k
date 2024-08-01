import DemoApplication.doubleToolAssistant2
import com.vapi4k.dsl.assistant.AssistantDsl.squad
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.dsl.model.enums.GroqModelType
import com.vapi4k.dsl.toolservice.ToolCallService
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.server.Vapi4k
import com.vapi4k.server.Vapi4kServer.logger
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.serialization.json.JsonElement


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
      doubleToolAssistant2(assistantRequest)
    }
  }
}

object DemoApplication {
  fun doubleToolAssistant2(request: JsonElement): AssistantRequestResponse =
    squad(request) {
      members {
        member {
          assistant {
            name = "assistant1"
            firstMessage = "Hi there! I'm assistant1"

            groqModel {
              modelType = GroqModelType.LLAMA3_70B
              tools {
                tool(TestWeatherLookupService("windy"))
              }
            }
          }
          destinations {
            destination {
              assistantName = "assistant2"
              message = "Transfer to assistant2"
              description = "assistant 2"
            }
          }
        }

        member {
          assistant {
            name = "assistant2"
            firstMessage = "Hi there! I'm assistant2"

            groqModel {
              modelType = GroqModelType.LLAMA3_70B
              tools {
                tool(TestWeatherLookupService("rainy"))
              }
            }
          }
          destinations {
            destination {
              assistantName = "assistant1"
              message = "Transfer to assistant1"
              description = "assistant 1"
            }
          }
        }
      }
    }

  class TestWeatherLookupService(
    val weather: String,
  ) : ToolCallService() {
    @ToolCall("Look up the weather for a city")
    fun getWeatherByCity(
      city: String,
      state: String,
    ) = "The weather in $city, $state is $weather"
  }

}
