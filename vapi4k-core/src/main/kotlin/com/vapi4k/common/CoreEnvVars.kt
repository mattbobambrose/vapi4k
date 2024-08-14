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

package com.vapi4k.common

import com.vapi4k.utils.envvar.EnvVar

object CoreEnvVars {
  val PORT = EnvVar("PORT", { System.getenv(name) ?: "8080" })
  val HOST = EnvVar("HOST", { System.getenv(name) ?: "unknown" })
  private val IS_PRODUCTION = EnvVar("IS_PRODUCTION", { System.getenv(name) ?: "false" })
  private val SERVER_BASE_URL = EnvVar("SERVER_BASE_URL", { System.getenv(name) ?: "http://localhost:$8080" })
  private val DEFAULT_SERVER_PATH = EnvVar("DEFAULT_SERVER_PATH", { System.getenv(name) ?: "/vapi4k" })

  val REQUEST_VALIDATION_FILENAME =
    EnvVar(
      name = "REQUEST_VALIDATION_FILENAME",
      src = { System.getenv(name) ?: "/json/AssistantRequestValidation.json" },
      reportOnBoot = false,
    )

  val TOOL_CACHE_CLEAN_PAUSE_MINS =
    EnvVar("TOOL_CACHE_CLEAN_PAUSE_MINS", { System.getenv(name) ?: "30" }, reportOnBoot = false)
  val TOOL_CACHE_MAX_AGE_MINS =
    EnvVar("TOOL_CACHE_MAX_AGE_MINS", { System.getenv(name) ?: "60" }, reportOnBoot = false)

  val isProduction: Boolean by lazy { IS_PRODUCTION.toBoolean() }
  val defaultServerPath: String by lazy { DEFAULT_SERVER_PATH.value.removePrefix("/").removeSuffix("/") }
  val serverBaseUrl: String by lazy { SERVER_BASE_URL.value.removeSuffix("/") }

  fun loadCoreEnvVars() = Unit
}
