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

package com.vapi4k.com.vapi4k.examples

import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.api.vapi4k.AssistantRequestUtils.phoneNumber
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.server.Vapi4k
import com.vapi4k.server.Vapi4kServer.logger
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun main() {
  // Start a ktor server on port 8080
  embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::helloWorld)
    .start(wait = true)
}

fun Application.helloWorld() {
  // Install the Vapi4k ktor plugin
  install(Vapi4k) {
    // Define the Vapi4k application
    vapi4kApplication {
      onAssistantRequest { request ->
        logger.info { "Assistant request has been made: ${request.phoneNumber}" }

        assistant {
          firstMessage = "Hello, I am a simple Vapi assistant that looks up the weather for a city and state."

          openAIModel {
            modelType = OpenAIModelType.GPT_4O_MINI
            systemMessage = """
              You're a weather lookup service. Take in state and country names as the two letter
              abbreviations (e.g. Illinois is IL, California is CA, Vermont is VT)
              """

            tools {
              serviceTool(WeatherLookupService())
            }
          }
        }
      }
    }
  }
}

class WeatherLookupService {
  @ToolCall("Look up the weather for a city and state")
  fun getWeatherByCityAndState(
    city: String,
    state: String,
  ): String {
    return "The real weather in $city, $state is ${listOf("sunny", "cloudy", "humid", "rainy").random()}"
  }
}
