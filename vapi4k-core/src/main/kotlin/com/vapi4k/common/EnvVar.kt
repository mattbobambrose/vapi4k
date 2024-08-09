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

import com.vapi4k.utils.Utils.isNull
import com.vapi4k.utils.Utils.obfuscate


class EnvVar(
  val name: String,
  private val src: EnvVar.() -> Any,
  private val maskFunc: ((str: String) -> String)? = null,
  private val width: Int = 10,
  val reportOnBoot: Boolean = true,
) {
  init {
    evars[name] = this
  }

  val value: String by lazy { src().toString() }

  private val logReport: String by lazy { "$name: $logValue" }

  private val logValue
    get() = if (maskFunc.isNull())
      value
    else
      maskFunc.invoke(value).let { if (it.length > width) it.substring(0, width) else it.padEnd(width, '*') }

  fun toBoolean(): Boolean = value.toBoolean()

  fun toInt(): Int = value.toInt()

  fun getEnvOrNull(): String? = System.getenv(name)

  fun getEnv(default: String): String = getEnvOrNull() ?: default

  fun getEnv(default: Boolean) = getEnvOrNull()?.toBoolean() ?: default

  fun getEnv(default: Int) = getEnvOrNull()?.toInt() ?: default

  fun getRequired() = getEnvOrNull() ?: error("Missing $name value")

  override fun toString(): String = value

  companion object {
    init {
      // Without this, the logger will not throw a missing value exception when reporting the values at startup
      System.setProperty("kotlin-logging.throwOnMessageError", "true")
    }

    val evars = mutableMapOf<String, EnvVar>()

    fun logEnvVarValues(block: (String) -> Unit) =
      evars.values.filter { it.reportOnBoot }.sortedBy { it.name }.map { it.logReport }.forEach(block)
  }
}

object CoreEnvVars {
  val PORT = EnvVar("PORT", { System.getenv(name) ?: "8080" })
  val SERVER_BASE_URL = EnvVar("SERVER_BASE_URL", { System.getenv(name) ?: "http://localhost:8080" })
  val DEFAULT_SERVER_PATH = EnvVar("DEFAULT_SERVER_PATH", { System.getenv(name) ?: "/vapi4k" })
  val IS_PRODUCTION = EnvVar("IS_PRODUCTION", { System.getenv(name) ?: "false" })

  val DBMS_DRIVER_CLASSNAME =
    EnvVar("DBMS_DRIVER_CLASSNAME", { System.getenv(name) ?: "com.impossibl.postgres.jdbc.PGDriver" })
  val DBMS_URL = EnvVar("DBMS_URL", { System.getenv(name) ?: "jdbc:pgsql://localhost:5432/postgres" })
  val DBMS_USERNAME = EnvVar("DBMS_USERNAME", { System.getenv(name) ?: "postgres" })
  val DBMS_PASSWORD = EnvVar("DBMS_PASSWORD", { System.getenv(name) ?: "docker" }, { it.obfuscate(1) })
  val DBMS_MAX_POOL_SIZE = EnvVar("DBMS_MAX_POOL_SIZE", { System.getenv(name) ?: "10" })
  val DBMS_MAX_LIFETIME_MINS = EnvVar("DBMS_MAX_LIFETIME_MINS", { System.getenv(name) ?: "30" })

  val REQUEST_VALIDATION_FILENAME =
    EnvVar(
      name = "REQUEST_VALIDATION_FILENAME",
      src = { System.getenv(name) ?: "/json/AssistantRequestValidation.json" },
      reportOnBoot = false
    )

  val TOOL_CACHE_CLEAN_PAUSE_MINS = EnvVar("TOOL_CACHE_CLEAN_PAUSE_MINS", { System.getenv(name) ?: "30" })
  val TOOL_CACHE_MAX_AGE_MINS = EnvVar("TOOL_CACHE_MAX_AGE_MINS", { System.getenv(name) ?: "60" })

  val isProduction: Boolean by lazy { IS_PRODUCTION.toBoolean() }
  val serverBaseUrl: String by lazy { SERVER_BASE_URL.value.removeSuffix("/") }
  val defaultServerPath: String by lazy { DEFAULT_SERVER_PATH.value.removePrefix("/") }
}
