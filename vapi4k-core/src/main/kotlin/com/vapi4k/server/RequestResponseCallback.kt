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

import com.vapi4k.common.ApplicationId
import com.vapi4k.dsl.vapi4k.RequestResponseType
import com.vapi4k.utils.JsonUtils
import com.vapi4k.utils.enums.ServerRequestType
import com.vapi4k.utils.enums.ServerRequestType.Companion.requestType
import kotlinx.serialization.json.JsonElement
import kotlin.time.Duration

data class RequestResponseCallback(
  val applicationId: ApplicationId,
  val type: RequestResponseType,
  val requestType: ServerRequestType,
  val request: JsonElement = JsonUtils.emptyJsonElement(),
  val response: (() -> JsonElement) = { JsonUtils.emptyJsonElement() },
  val elapsed: Duration = Duration.ZERO,
) {
  companion object {
    fun requestCallback(requestContext: RequestContext): RequestResponseCallback =
      with(requestContext) {
        RequestResponseCallback(
          applicationId = application.applicationId,
          type = RequestResponseType.REQUEST,
          requestType = request.requestType,
          request = request,
        )
      }

    fun responseCallback(
      requestContext: RequestContext,
      response: () -> JsonElement,
      elapsed: Duration,
    ) = RequestResponseCallback(
      applicationId = requestContext.application.applicationId,
      type = RequestResponseType.RESPONSE,
      requestType = requestContext.request.requestType,
      response = response,
      elapsed = elapsed,
    )
  }
}
