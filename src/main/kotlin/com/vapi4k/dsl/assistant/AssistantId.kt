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

package com.vapi4k.dsl.assistant

import com.vapi4k.common.SessionCacheId
import com.vapi4k.dtos.assistant.AssistantDto
import com.vapi4k.dtos.assistant.AssistantOverridesDto
import com.vapi4k.utils.AssistantCacheIdSource
import kotlinx.serialization.json.JsonElement

interface AssistantIdProperties {
  var assistantId: String
  val assistantDto: AssistantDto
  val assistantOverridesDto: AssistantOverridesDto
}

@AssistantDslMarker
interface AssistantId {
  var id: String

  fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides
}

data class AssistantIdImpl internal constructor(
  internal val request: JsonElement,
  private val sessionCacheId: SessionCacheId,
  private val assistantCacheIdSource: AssistantCacheIdSource,
  internal val assistantIdProperties: AssistantIdProperties,
) : AssistantIdProperties by assistantIdProperties, AssistantId {
  override var id
    get() = assistantIdProperties.assistantId
    set(value) {
      assistantIdProperties.assistantId = value
    }

  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides {
    return AssistantOverridesImpl(
      request,
      sessionCacheId,
      assistantCacheIdSource,
      assistantIdProperties.assistantOverridesDto
    ).apply(block)
  }
}
