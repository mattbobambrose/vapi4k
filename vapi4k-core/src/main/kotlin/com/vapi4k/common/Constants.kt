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

object Constants {
  const val VAPI_API_URL = "https://api.vapi.ai"
  const val HTMX_SOURCE_URL = "https://unpkg.com/htmx.org@2.0.2"

  const val UNSPECIFIED_DEFAULT = "unspecified"
  const val UNKNOWN = "unknown"
  const val STATIC_BASE = "/core_static"

  const val OUTBOUND_SERVER_PATH = "__outboundCalls"

  const val APPLICATION_ID = "applicationId"
  const val SESSION_CACHE_ID = "sessionCacheId"
  const val FUNCTION_NAME = "functionName"
}

object QueryParams {
  const val SECRET_QUERY_PARAM = "secret"
}

object Headers {
  const val VAPI_SECRET_HEADER = "x-vapi-secret"
  const val VAPI4K_VALIDATE_HEADER = "x-vapi4k-validate"
  const val VAPI4K_VALIDATE_VALUE = "true"
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
