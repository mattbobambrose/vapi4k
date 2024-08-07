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

import com.vapi4k.api.assistant.Assistant
import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.api.vapi4k.AssistantRequestContext
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.vapi4k.Vapi4kConfigImpl
import com.vapi4k.dtos.assistant.AssistantDto
import com.vapi4k.dtos.assistant.AssistantOverridesDto
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.DuplicateChecker

interface AssistantProperties : CommonAssistantProperties {
  // TODO: Not in the docs
  var dialKeypadFunctionEnabled: Boolean?
  var endCallFunctionEnabled: Boolean?
  var forwardingPhoneNumber: String
}

data class AssistantImpl internal constructor(
  override val assistantRequestContext: AssistantRequestContext,
  override val sessionCacheId: SessionCacheId,
  internal val assistantCacheIdSource: AssistantCacheIdSource,
  private val assistantDto: AssistantDto,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AbstractAssistantImpl(),
  AssistantProperties by assistantDto,
  Assistant {
  override val transcriberChecker = DuplicateChecker()
  override val modelChecker = DuplicateChecker()
  override val voiceChecker = DuplicateChecker()
  override val assistantCacheId = assistantCacheIdSource.nextAssistantCacheId()

  override val modelDtoUnion get() = assistantDto
  override val voicemailDetectionDto get() = modelDtoUnion.voicemailDetectionDto
  override val analysisPlanDto get() = modelDtoUnion.analysisPlanDto
  override val artifactPlanDto get() = modelDtoUnion.artifactPlanDto

  // AssistantOverrides
  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(
      assistantRequestContext,
      sessionCacheId,
      assistantCacheIdSource,
      assistantOverridesDto,
    ).apply(block)

  companion object {
    internal lateinit var config: Vapi4kConfigImpl
  }
}
