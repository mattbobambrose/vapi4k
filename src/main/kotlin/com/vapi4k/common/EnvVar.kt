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

import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.Utils.dropEnding
import com.vapi4k.utils.Utils.dropLeading
import com.vapi4k.utils.Utils.isNull
import com.vapi4k.utils.Utils.obfuscate

enum class EnvVar(
  private val src: () -> Any,
  private val maskFunc: ((str: String) -> String)? = null,
  private val width: Int = 10,
) {
  // Server details
  PORT({ PORT.getEnv(8080) }),
  SERVER_BASE_URL({ SERVER_BASE_URL.getEnv("http://localhost:8080") }),
  DEFAULT_SERVER_PATH({ DEFAULT_SERVER_PATH.getEnv("/vapi4k") }),
  IS_PRODUCTION({ IS_PRODUCTION.getEnv(false) }),

  // Database details
  DBMS_DRIVER_CLASSNAME({ DBMS_DRIVER_CLASSNAME.getEnv("com.impossibl.postgres.jdbc.PGDriver") }),
  DBMS_URL({ DBMS_URL.getEnv("jdbc:pgsql://localhost:5432/postgres") }),
  DBMS_USERNAME({ DBMS_USERNAME.getEnv("postgres") }),
  DBMS_PASSWORD({ DBMS_PASSWORD.getEnv("docker") }, { it.obfuscate(1) }),
  DBMS_MAX_POOL_SIZE({ DBMS_MAX_POOL_SIZE.getEnv(10) }),
  DBMS_MAX_LIFETIME_MINS({ DBMS_MAX_LIFETIME_MINS.getEnv(30) }),

  REQUEST_VALIDATION_URL({ REQUEST_VALIDATION_URL.getEnv("http://localhost:8080/vapi4k") }),
  REQUEST_VALIDATION_FILENAME({ REQUEST_VALIDATION_FILENAME.getEnv("/json/AssistantRequestValidation.json") }),

  TOOL_CACHE_CLEAN_PAUSE_MINS({ TOOL_CACHE_CLEAN_PAUSE_MINS.getEnv(30) }),
  TOOL_CACHE_MAX_AGE_MINS({ TOOL_CACHE_MAX_AGE_MINS.getEnv(60) }),

  // Resend details
  // RESEND_API_KEY({ RESEND_API_KEY.getRequired() }, { it.obfuscate(1) }),
  // RESEND_SENDER_EMAIL({ RESEND_SENDER_EMAIL.getRequired() })
  ;

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

    val isProduction: Boolean by lazy { IS_PRODUCTION.toBoolean() }
    val serverBaseUrl: String by lazy { SERVER_BASE_URL.value.dropEnding("/") }
    val defaultServerPath: String by lazy { DEFAULT_SERVER_PATH.value.dropLeading("/") }

//    val envResendApiKey: String by lazy { RESEND_API_KEY.value }
//    val envResendEmailSender: Email by lazy { RESEND_SENDER_EMAIL.value.toEmail() }

    fun logEnvVarValues() = entries.sortedBy { it.name }.forEach { logger.info { it.logReport } }
  }
}
