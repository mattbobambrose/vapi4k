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

package com.vapi4k.dsl.vapi4k

import com.vapi4k.plugin.Vapi4kConfig

@Vapi4KDslMarker
class ToolCallEndpoints(val config: Vapi4kConfig) {
  internal fun ToolCallEndpoints.hasName(endpoint: Endpoint) =
    config.toolCallEndpoints.any { it.name == endpoint.name }

  internal fun ToolCallEndpoints.hasUrl(endpoint: Endpoint) =
    config.toolCallEndpoints.any { it.url == endpoint.url }
}
