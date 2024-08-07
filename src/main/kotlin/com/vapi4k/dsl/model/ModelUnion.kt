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

package com.vapi4k.dsl.model

import com.vapi4k.api.vapi4k.AssistantRequestContext
import com.vapi4k.common.AssistantCacheId
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dtos.AnalysisPlanDto
import com.vapi4k.dtos.ArtifactPlanDto
import com.vapi4k.dtos.VoicemailDetectionDto
import com.vapi4k.dtos.model.CommonModelDto
import com.vapi4k.dtos.transcriber.CommonTranscriberDto
import com.vapi4k.dtos.voice.CommonVoiceDto
import com.vapi4k.utils.DuplicateChecker

interface ModelDtoUnion {
  var transcriberDto: CommonTranscriberDto?
  var modelDto: CommonModelDto?
  var voiceDto: CommonVoiceDto?
}

interface ModelUnion {
  val assistantRequestContext: AssistantRequestContext
  val analysisPlanDto: AnalysisPlanDto
  val artifactPlanDto: ArtifactPlanDto
  val assistantCacheId: AssistantCacheId
  val modelChecker: DuplicateChecker
  val modelDtoUnion: ModelDtoUnion
  val sessionCacheId: SessionCacheId
  val transcriberChecker: DuplicateChecker
  val voiceChecker: DuplicateChecker
  val voicemailDetectionDto: VoicemailDetectionDto
}
