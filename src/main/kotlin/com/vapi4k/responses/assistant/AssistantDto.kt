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


import com.vapi4k.dsl.assistant.AssistantUnion
import kotlinx.serialization.Serializable

@Serializable
data class AssistantDto(
  override var name: String = "",
  override var firstMessage: String = "",
  override var firstMessageMode: String = "",
  override var recordingEnabled: Boolean = false,
  override var hipaaEnabled: Boolean = false,
  override var serverUrl: String = "",
  override var serverUrlSecret: String = "",
  // TODO: This needs to be added to docs
  override var forwardingPhoneNumber: String = "",
  override var endCallFunctionEnabled: Boolean = false,
  override var dialKeypadFunctionEnabled: Boolean = false,
  override var responseDelaySeconds: Double = 0.0,
  override var llmRequestDelaySeconds: Double = 0.0,
  override var silenceTimeoutSeconds: Int = 0,
  override var maxDurationSeconds: Int = 0,
  override var backgroundSound: String = "",
  override var numWordsToInterruptAssistant: Int = 0,
  override var voicemailMessage: String = "",
  override var endCallMessage: String = "",
  override var backchannelingEnabled: Boolean = false,
  override var backgroundDenoisingEnabled: Boolean = false,
  override var modelOutputInMessagesEnabled: Boolean = false,
  override var llmRequestNonPunctuatedDelaySeconds: Double = 0.0,

  var model: ModelDto = ModelDto(),
  var transcriber: Transcriber = Transcriber(),
  var voice: Voice = Voice(),

  var voicemailDetection: VoicemailDetection? = null,
  var metadata: Metadata? = null,
  var analysisPlan: AnalysisPlan? = null,
  var artifactPlan: ArtifactPlan? = null,
  var messagePlan: MessagePlan? = null,

  var clientMessages: MutableList<String> = mutableListOf(),
  var serverMessages: MutableList<String> = mutableListOf(),
  var endCallPhrases: MutableList<String> = mutableListOf(),
) : AssistantUnion
