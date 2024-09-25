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
import com.vapi4k.utils.envvar.EnvVar
import com.vapi4k.utils.envvar.EnvVar.Companion.getWithDefault
import com.vapi4k.utils.envvar.EnvVar.Companion.obfuscate

object CoreEnvVars {
  private val IS_PRODUCTION = EnvVar("IS_PRODUCTION", getWithDefault(false))
  private val DEFAULT_SERVER_PATH = EnvVar("DEFAULT_SERVER_PATH", getWithDefault("/vapi4k"))

  private val VAPI4K_BASE_URL = EnvVar("VAPI4K_BASE_URL", getWithDefault("http://localhost:8080"))
  private val VAPI_BASE_URL = EnvVar("VAPI_BASE_URL", getWithDefault("https://api.vapi.ai"))
  internal val PING_LOGGING_ENABLED = EnvVar("PING_LOGGING_ENABLED", getWithDefault(false))

  private val VAPI_PRIVATE_KEY = EnvVar(
    name = "VAPI_PRIVATE_KEY",
    src = getWithDefault(""),
    maskFunc = obfuscate(1),
  )

  private val VAPI_PUBLIC_KEY = EnvVar(
    name = "VAPI_PUBLIC_KEY",
    src = getWithDefault(""),
    maskFunc = obfuscate(1),
  )

  private val VAPI_PHONE_NUMBER_ID = EnvVar(
    name = "VAPI_PHONE_NUMBER_ID",
    src = getWithDefault(""),
    maskFunc = obfuscate(3),
  )

  internal val REQUEST_VALIDATION_FILENAME =
    EnvVar(
      name = "REQUEST_VALIDATION_FILENAME",
      src = getWithDefault("/json/AssistantRequestValidation.json"),
      reportOnBoot = false,
    )

  private val DEEPGRAM_PRIVATE_KEY = EnvVar(
    name = "DEEPGRAM_PRIVATE_KEY",
    src = getWithDefault(""),
    maskFunc = obfuscate(1),
  )

  internal val TOOL_CACHE_CLEAN_PAUSE_MINS =
    EnvVar("TOOL_CACHE_CLEAN_PAUSE_MINS", getWithDefault(30), reportOnBoot = false)
  internal val TOOL_CACHE_MAX_AGE_MINS =
    EnvVar("TOOL_CACHE_MAX_AGE_MINS", getWithDefault(60), reportOnBoot = false)

  internal val defaultServerPath: String by lazy { DEFAULT_SERVER_PATH.value.removeEnds("/") }

  val PORT = EnvVar("PORT", getWithDefault(8080))
  val HOST = EnvVar("HOST", getWithDefault("unknown"), reportOnBoot = false)

  val isProduction: Boolean = IS_PRODUCTION.toBoolean()
  val vapi4kBaseUrl: String = VAPI4K_BASE_URL.value.removeSuffix("/")
  val vapiBaseUrl: String = VAPI_BASE_URL.value.removeSuffix("/")
  val vapiPrivateKey: String = VAPI_PRIVATE_KEY.value
  val vapiPublicKey: String = VAPI_PUBLIC_KEY.value
  val vapiPhoneNumberId: String = VAPI_PHONE_NUMBER_ID.value
  val deepGramVoiceIdType: String = DEEPGRAM_PRIVATE_KEY.value

  fun loadCoreEnvVars() = Unit
}
