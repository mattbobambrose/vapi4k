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

import com.vapi4k.dsl.assistant.AssistantOverridesProperties
import com.vapi4k.dsl.assistant.ModelDtoUnion
import com.vapi4k.dtos.AnalysisPlanDto
import com.vapi4k.dtos.ArtifactPlanDto
import com.vapi4k.dtos.MessagePlanDto
import com.vapi4k.dtos.TransportConfigurationDto
import com.vapi4k.dtos.VoicemailDetectionDto
import com.vapi4k.dtos.model.CommonModelDto
import com.vapi4k.dtos.transcriber.CommonTranscriberDto
import com.vapi4k.dtos.voice.CommonVoiceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantOverridesDto(
//  override var name: String = "",

//  override var firstMessage: String = "",
//  override var recordingEnabled: Boolean? = null,
//  override var hipaaEnabled: Boolean? = null,
//  override var serverUrl: String = "",
//  override var serverUrlSecret: String = "",

//  override var silenceTimeoutSeconds: Int = -1,
//  override var responseDelaySeconds: Double = -1.0,
//  override var llmRequestDelaySeconds: Double = -1.0,
//  override var llmRequestNonPunctuatedDelaySeconds: Double = -1.0,
//  override var numWordsToInterruptAssistant: Int = -1,
//  override var maxDurationSeconds: Int = -1,
//  override var backgroundSound: BackgroundSoundType = BackgroundSoundType.UNSPECIFIED,
//  override var backchannelingEnabled: Boolean? = null,
//  override var backgroundDenoisingEnabled: Boolean? = null,
//  override var modelOutputInMessagesEnabled: Boolean? = null,
//  override var voicemailMessage: String = "",
//  override var endCallMessage: String = "",

  override var firstMessageMode: String = "",

  override val clientMessages: MutableSet<String> = mutableSetOf(),
  override val serverMessages: MutableSet<String> = mutableSetOf(),

  val endCallPhrases: MutableSet<String> = mutableSetOf(),

  @SerialName("transcriber")
  override var transcriberDto: CommonTranscriberDto? = null,

  @SerialName("model")
  override var modelDto: CommonModelDto? = null,

  @SerialName("voice")
  override var voiceDto: CommonVoiceDto? = null,

  // TODO: Came from squad assistant,
  val transportConfigurations: MutableList<TransportConfigurationDto> = mutableListOf(),
  val variableValues: MutableMap<String, String> = mutableMapOf(),
  val metadata: MutableMap<String, String> = mutableMapOf(),

  @SerialName("voicemailDetection")
  val voicemailDetectionDto: VoicemailDetectionDto = VoicemailDetectionDto(),

  @SerialName("analysisPlan")
  val analysisPlanDto: AnalysisPlanDto = AnalysisPlanDto(),

  @SerialName("artifactPlan")
  val artifactPlanDto: ArtifactPlanDto = ArtifactPlanDto(),
  @SerialName("messagePlan")
  val messagePlanDto: MessagePlanDto = MessagePlanDto(),
) : AbstractAssistantDto(), AssistantOverridesProperties, ModelDtoUnion
