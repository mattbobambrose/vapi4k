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

import com.vapi4k.common.Endpoints.CACHES_PATH
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import kotlinx.serialization.json.buildJsonObject

internal object CacheResponses {
  suspend fun KtorCallContext.clearCaches(config: Vapi4kConfigImpl) {
    config.allCallApplications.forEach { application ->
      with(application) {
        serviceToolCache.clearToolCache()
        functionCache.clearToolCache()
      }
    }
    config.webApplications.forEach { application ->
      with(application) {
        serviceToolCache.clearToolCache()
        functionCache.clearToolCache()
      }
    }
    call.respondRedirect(CACHES_PATH)
  }

  suspend fun KtorCallContext.cachesRequest(config: Vapi4kConfigImpl) {
    call.respond(
      buildJsonObject {
        config.allCallApplications.forEach { application ->
          put(
            application.serverPathAsSegment,
            buildJsonObject {
              put(
                "toolServices",
                application.serviceToolCache.cacheAsJson().toJsonElement(),
              )
              put(
                "functions",
                application.functionCache.cacheAsJson().toJsonElement(),
              )
            },
          )
        }

        config.webApplications.forEach { application ->
          put(
            application.serverPathAsSegment,
            buildJsonObject {
              put(
                "toolServices",
                application.serviceToolCache.cacheAsJson().toJsonElement(),
              )
              put(
                "functions",
                application.functionCache.cacheAsJson().toJsonElement(),
              )
            },
          )
        }
      },
    )
  }
}
