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

package com.vapi4k.dtos.assistant

import com.vapi4k.dsl.assistant.AssistantProperties
import com.vapi4k.dsl.model.ModelDtoUnion
import com.vapi4k.dtos.AnalysisPlanDto
import com.vapi4k.dtos.ArtifactPlanDto
import com.vapi4k.dtos.MessagePlanDto
import com.vapi4k.dtos.VoicemailDetectionDto
import com.vapi4k.dtos.model.CommonModelDto
import com.vapi4k.dtos.transcriber.CommonTranscriberDto
import com.vapi4k.dtos.voice.CommonVoiceDto
import com.vapi4k.utils.common.Utils.isNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AssistantDto(
  @SerialName("transcriber")
  override var transcriberDto: CommonTranscriberDto? = null,
  @SerialName("model")
  override var modelDto: CommonModelDto? = null,
  @SerialName("voice")
  override var voiceDto: CommonVoiceDto? = null,
  @SerialName("voicemailDetection")
  val voicemailDetectionDto: VoicemailDetectionDto = VoicemailDetectionDto(),
  @SerialName("analysisPlan")
  val analysisPlanDto: AnalysisPlanDto = AnalysisPlanDto(),
  @SerialName("artifactPlan")
  val artifactPlanDto: ArtifactPlanDto = ArtifactPlanDto(),
  @SerialName("messagePlan")
  val messagePlanDto: MessagePlanDto = MessagePlanDto(),
) : AbstractAssistantDto(),
  AssistantProperties,
  ModelDtoUnion {
  @Transient
  var updated = false

  internal fun verifyValues() {
    if (modelDto.isNull())
      error("An assistant{} requires a model{} decl")
  }
}

