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

package com.vapi4k.responses.assistant

import com.vapi4k.dsl.assistant.AssistantOverridesUnion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantOverridesDto(
  override var firstMessageMode: String = "",
  override var recordingEnabled: Boolean = false,
  override var hipaaEnabled: Boolean = false,
  override var silenceTimeoutSeconds: Int = 0,
  override var responseDelaySeconds: Double = 0.0,
  override var llmRequestDelaySeconds: Double = 0.0,
  override var llmRequestNonPunctuatedDelaySeconds: Double = 0.0,
  override var numWordsToInterruptAssistant: Int = 0,
  override var maxDurationSeconds: Int = 0,
  override var backgroundSound: String = "",
  override var backchannelingEnabled: Boolean = false,
  override var backgroundDenoisingEnabled: Boolean = false,
  override var modelOutputInMessagesEnabled: Boolean = false,
  override var name: String = "",
  override var firstMessage: String = "",
  override var voicemailMessage: String = "",
  override var endCallMessage: String = "",
  override var serverUrl: String = "",
  override var serverUrlSecret: String = "",

  override val clientMessages: MutableSet<String> = mutableSetOf(),
  override val serverMessages: MutableSet<String> = mutableSetOf(),

  val endCallPhrases: MutableList<String> = mutableListOf(),

  @SerialName("model")
  val modelDto: ModelDto = ModelDto(),
  @SerialName("voice")
  val voiceDto: VoiceDto = VoiceDto(),

  @SerialName("transcriber")
  // DeepgramTranscriberDto is assigned as the default, but is later overwritten
  var transcriberDto: AbstractTranscriberDto = DeepgramTranscriberDto(),

  // TODO: Came from squad assistant
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
) : AssistantOverridesUnion
