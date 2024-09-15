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

package com.vapi4k.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.pluginRegistry
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.minimumSize
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.slf4j.event.Level
import java.time.Duration

fun Application.defaultKtorConfig(appMicrometerRegistry: PrometheusMeterRegistry) {
  val pregistry = pluginRegistry

//  if (!pregistry.contains(ContentNegotiation.key)) {
//    install(ContentNegotiation) {
//      json(Json { ignoreUnknownKeys = true })
//    }
//  }

  if (!pregistry.contains(Compression.key)) {
    install(Compression) {
      gzip {
        priority = 1.0
      }
      deflate {
        priority = 10.0
        minimumSize(1024) // condition
      }
    }
  }

  if (!pregistry.contains(CallLogging.key)) {
    install(CallLogging) {
      level = Level.INFO
      filter { call -> call.request.path().startsWith("/") }
      disableDefaultColors()
    }
  }

  if (!pregistry.contains(Routing.key)) {
    install(Routing)
  }

  if (!pregistry.contains(WebSockets.key)) {
    install(WebSockets) {
      pingPeriod = Duration.ofSeconds(15)
      timeout = Duration.ofSeconds(15)
      maxFrameSize = Long.MAX_VALUE
      masking = false
    }
  }

//  if (!pregistry.contains(MicrometerMetrics.key)) {
//    install(MicrometerMetrics) {
//      registry = appMicrometerRegistry
//      meterBinders = emptyList()
//    }
//  }
}

fun Route.installContentNegotiation(block: JsonBuilder.() -> Unit = {}) {
  install(ContentNegotiation) {
    json(
      Json {
        ignoreUnknownKeys = true
        block()
      },
    )
  }
}
