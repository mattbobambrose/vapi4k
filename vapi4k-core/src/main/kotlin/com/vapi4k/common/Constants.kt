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

import com.vapi4k.common.Constants.FUNCTION_NAME

object Constants {
  const val HTMX_SOURCE_URL = "https://unpkg.com/htmx.org@2.0.2"

  const val UNSPECIFIED_DEFAULT = "unspecified"
  const val UNKNOWN = "unknown"
  const val STATIC_BASE = "/core_static"

  const val FUNCTION_NAME = "functionName"

  const val APP_TYPE = "appType"
  const val APP_NAME = "appName"

  const val PRIVATE_KEY_PROPERTY = "vapi.api.privateKey"
  const val PHONE_NUMBER_ID_PROPERTY = "vapi.phoneNumberId"

  const val QUERY_ARGS = "queryArgs"
  const val POST_ARGS = "postArgs"

  const val ASSISTANT_ID_WIDTH = 3
}

object QueryParams {
  const val SECRET_PARAM = "secret"

  private const val ID_PREFIX = "__"
  const val APPLICATION_ID = "${ID_PREFIX}applicationId"
  const val SESSION_ID = "${ID_PREFIX}sessionId"
  const val ASSISTANT_ID = "${ID_PREFIX}assistantId"
  const val TOOL_TYPE = "${ID_PREFIX}toolType"

  val SYSTEM_IDS = setOf(APPLICATION_ID, SESSION_ID, ASSISTANT_ID, TOOL_TYPE, FUNCTION_NAME)
}

object Headers {
  const val VAPI_SECRET_HEADER = "x-vapi-secret"
  const val VALIDATE_HEADER = "x-vapi4k-validate"
  const val VALIDATE_VALUE = "true"
}

object Endpoints {
  const val PING_PATH = "/ping"
  const val VERSION_PATH = "/version"
  const val ENV_PATH = "/env"
  const val METRICS_PATH = "/metrics"
  const val CACHES_PATH = "/caches"
  const val CLEAR_CACHES_PATH = "/clear-caches"
  const val VALIDATE_PATH = "/validate"
  const val VALIDATE_INVOKE_TOOL_PATH = "/validateInvokeTool"
}

object CssNames {
  const val TOOLS_DIV = "tools-div"
}

object ErrorMessages {
  const val INVALID_BASE_URL = "Invalid VAPI4K_BASE_URL env value"
}
