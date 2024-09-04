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

package com.vapi4k.server

import com.vapi4k.api.tools.RequestContext
import com.vapi4k.api.tools.ResponseContext
import com.vapi4k.utils.enums.ServerRequestType
import com.vapi4k.utils.enums.ServerRequestType.Companion.serverRequestType
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

class ResponseContextImpl(
  override val requestContext: RequestContext,
  override val response: JsonElement,
  override val elapsed: Duration,
) : ResponseContext {
  override val serverRequestType: ServerRequestType get() = requestContext.request.serverRequestType

  override fun toString(): String =
    "RequestContext:\nRequestType: ${serverRequestType}\nSession ID: ${requestContext.sessionId}\nElapsed: $elapsed\nResponse:\n${response.toJsonString()}"
}
