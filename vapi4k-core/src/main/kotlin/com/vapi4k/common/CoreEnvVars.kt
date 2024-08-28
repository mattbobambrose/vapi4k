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

import com.vapi4k.utils.MiscUtils.removeEnds
import com.vapi4k.utils.common.Utils.obfuscate
import com.vapi4k.utils.envvar.EnvVar

object CoreEnvVars {
  private val IS_PRODUCTION = EnvVar("IS_PRODUCTION", { System.getenv(name) ?: "false" })
  private val DEFAULT_SERVER_PATH = EnvVar("DEFAULT_SERVER_PATH", { System.getenv(name) ?: "/vapi4k" })

  private val VAPI4K_BASE_URL = EnvVar("VAPI4K_BASE_URL", { System.getenv(name) ?: "http://localhost:8080" })
  private val VAPI_BASE_URL = EnvVar("VAPI_BASE_URL", { System.getenv(name) ?: "https://api.vapi.ai" })

  private val VAPI_PRIVATE_KEY = EnvVar(
    name = "VAPI_PRIVATE_KEY",
    src = { System.getenv(name) ?: "" },
    maskFunc = { it.obfuscate(1) },
  )

  private val VAPI_PHONE_NUMBER_ID = EnvVar(
    name = "VAPI_PHONE_NUMBER_ID",
    src = { System.getenv(name) ?: "" },
    maskFunc = { it.obfuscate(3) },
  )

  internal val REQUEST_VALIDATION_FILENAME =
    EnvVar(
      name = "REQUEST_VALIDATION_FILENAME",
      src = { System.getenv(name) ?: "/json/AssistantRequestValidation.json" },
      reportOnBoot = false,
    )

  internal val TOOL_CACHE_CLEAN_PAUSE_MINS =
    EnvVar("TOOL_CACHE_CLEAN_PAUSE_MINS", { System.getenv(name) ?: "30" }, reportOnBoot = false)
  internal val TOOL_CACHE_MAX_AGE_MINS =
    EnvVar("TOOL_CACHE_MAX_AGE_MINS", { System.getenv(name) ?: "60" }, reportOnBoot = false)

  internal val defaultServerPath: String by lazy { DEFAULT_SERVER_PATH.value.removeEnds("/") }

  val PORT = EnvVar("PORT", { System.getenv(name) ?: "8080" })
  val HOST = EnvVar("HOST", { System.getenv(name) ?: "unknown" })

  val isProduction: Boolean = IS_PRODUCTION.toBoolean()
  val vapi4kBaseUrl: String = VAPI4K_BASE_URL.value.removeSuffix("/")
  val vapiBaseUrl: String = VAPI_BASE_URL.value.removeSuffix("/")
  val vapiPrivateKey: String = VAPI_PRIVATE_KEY.value
  val vapiPhoneNumberId: String = VAPI_PHONE_NUMBER_ID.value

  fun loadCoreEnvVars() = Unit
}
