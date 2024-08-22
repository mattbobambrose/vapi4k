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

import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.vapi4k.AssistantRequestContext
import com.vapi4k.dtos.assistant.AssistantOverridesDto
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.DuplicateChecker

interface AssistantOverridesProperties : CommonAssistantProperties {
  // Used only in AssistantOverrides
  val variableValues: MutableMap<String, String>
}

class AssistantOverridesImpl internal constructor(
  override val assistantRequestContext: AssistantRequestContext,
  override val sessionCacheId: SessionCacheId,
  internal val assistantCacheIdSource: AssistantCacheIdSource,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AbstractAssistantImpl(),
  AssistantOverridesProperties by assistantOverridesDto,
  AssistantOverrides {
  override val transcriberChecker = DuplicateChecker()
  override val modelChecker = DuplicateChecker()
  override val voiceChecker = DuplicateChecker()
  override val assistantCacheId = assistantCacheIdSource.nextAssistantCacheId()
  override val modelDtoUnion get() = assistantOverridesDto
  override val voicemailDetectionDto get() = modelDtoUnion.voicemailDetectionDto
  override val analysisPlanDto get() = modelDtoUnion.analysisPlanDto
  override val artifactPlanDto get() = modelDtoUnion.artifactPlanDto
}
