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
  const val HTMX_SOURCE_URL = "https://unpkg.com/htmx.org@2.0.1"

  const val UNSPECIFIED_DEFAULT = "unspecified"
  const val UNKNOWN = "unknown"
  const val STYLES_CSS = "/assets/css/styles.css"
}

object Endpoints {
  const val DEFAULT_SERVER_PATH = "vapi4k"
  const val PING_PATH = "/ping"
  const val VERSION_PATH = "/version"
  const val METRICS_PATH = "/metrics"
  const val CACHES_PATH = "/caches"
  const val CLEAR_CACHES_PATH = "/clear-caches"
  const val VALIDATE_PATH = "/validate"
  const val INVOKE_TOOL_PATH = "/invokeTool"
}
