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

import com.vapi4k.utils.DslUtils.getRandomSecret

enum class ApplicationType(
  val displayName: String,
  val pathPrefix: String,
  private val paramName: String,
) {
  INBOUND_CALL("inboundCallApplication", "inboundCall", "Inbound"),
  OUTBOUND_CALL("outboundCallApplication", "outboundCall", "Outbound"),
  WEB("webApplication", "web", "Web"),
  ;

  val functionName get() = "$displayName{}"

  fun defaultSessionId() = "$paramName-${getRandomSecret(8, 4, 4, 12)}"
}
