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

import kotlinx.serialization.Serializable

@Serializable
data class AssistantOverrides(
  var transcriber: Transcriber = Transcriber(),
  var model: ModelDto = ModelDto(),
  var voice: Voice = Voice(),
  var firstMessageMode: String = "",
  var recordingEnabled: Boolean = false,
  var hipaaEnabled: Boolean = false,
  var clientMessages: List<String> = listOf(),
  var serverMessages: List<String> = listOf(),
  var silenceTimeoutSeconds: Int = 0,
  var responseDelaySeconds: Double = 0.0,
  var llmRequestDelaySeconds: Double = 0.0,
  var llmRequestNonPunctuatedDelaySeconds: Double = 0.0,
  var numWordsToInterruptAssistant: Int = 0,
  var maxDurationSeconds: Int = 0,
  var backgroundSound: String = "",
  var backchannelingEnabled: Boolean = false,
  var backgroundDenoisingEnabled: Boolean = false,
  var modelOutputInMessagesEnabled: Boolean = false,
  var variableValues: VariableValues = VariableValues(),
  var name: String = "",
  var firstMessage: String = "",
  var voicemailDetection: VoicemailDetection = VoicemailDetection(),
  var voicemailMessage: String = "",
  var endCallMessage: String = "",
  var endCallPhrases: List<String> = listOf(),
  var metadata: Metadata = Metadata(),
  var serverUrl: String = "",
  var serverUrlSecret: String = "",
  var analysisPlan: AnalysisPlan = AnalysisPlan(),
  var artifactPlan: ArtifactPlan = ArtifactPlan(),
  var messagePlan: MessagePlan = MessagePlan(),
)
