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
import com.vapi4k.server.Vapi4k
import com.vapi4k.server.Vapi4kServer.logger
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

fun main() {
  embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  install(Vapi4k) {

    vapi4kApplication {
      onAssistantRequest {
        logger.info { "Assistant request has been made: ${assistantRequestContext.assistantRequest}" }

        assistant {
          firstMessage = "Hello, I am a simple Vapi assistant"

          openAIModel {
            modelType = OpenAIModelType.GPT_4O_MINI

            tools {
              //tool()
            }
          }
        }
      }
    }
  }
}


