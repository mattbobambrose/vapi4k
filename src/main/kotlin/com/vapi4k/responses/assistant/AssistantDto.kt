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
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import kotlinx.serialization.Serializable

@Serializable
data class AssistantDto(
  // TODO: Came from squad assistant
  val transportConfigurations: List<TransportConfiguration> = listOf(),

  override var name: String = "",
  override var firstMessage: String = "",
  override var recordingEnabled: Boolean = false,
  override var hipaaEnabled: Boolean = false,
  override var serverUrl: String = "",
  override var serverUrlSecret: String = "",
  // TODO: This needs to be added to docs
  // forwadingPhoneNumber might not have come in json
  override var forwardingPhoneNumber: String = "",
  // TODO: Not in docs or squad
  override var endCallFunctionEnabled: Boolean = false,
  // TODO: Not in docs or squad
  override var dialKeypadFunctionEnabled: Boolean = false,
  override var responseDelaySeconds: Double = 0.0,
  override var llmRequestDelaySeconds: Double = 0.0,
  override var silenceTimeoutSeconds: Int = 30,
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
  var voice: VoiceDto = VoiceDto(),
  var transcriber: TranscriberDto = TranscriberDto(),

  override var firstMessageMode: FirstMessageModeType = FirstMessageModeType.UNKNOWN,

  // TODO: Add verbs and enums
  var voicemailDetection: VoicemailDetectionDto? = null,
  var metadata: Metadata? = null,
  var analysisPlan: AnalysisPlanDto? = null,
  var artifactPlan: ArtifactPlanDto? = null,
  var messagePlan: MessagePlanDto? = null,

  override var clientMessages: MutableSet<AssistantClientMessageType> =
    mutableSetOf(
      AssistantClientMessageType.CONVERSATION_UPDATE,
      AssistantClientMessageType.FUNCTION_CALL,
      AssistantClientMessageType.HANG,
      AssistantClientMessageType.MODEL_OUTPUT,
      AssistantClientMessageType.SPEECH_UPDATE,
      AssistantClientMessageType.STATUS_UPDATE,
      AssistantClientMessageType.TRANSCRIPT,
      AssistantClientMessageType.TOOL_CALLS,
      AssistantClientMessageType.USER_INTERRUPTED,
      AssistantClientMessageType.VOICE_INPUT,
    ),

  override var serverMessages: MutableSet<AssistantServerMessageType> =
    mutableSetOf(
      AssistantServerMessageType.CONVERSATION_UPDATE,
      AssistantServerMessageType.END_OF_CALL_REPORT,
      AssistantServerMessageType.FUNCTION_CALL,
      AssistantServerMessageType.HANG,
      AssistantServerMessageType.SPEECH_UPDATE,
      AssistantServerMessageType.STATUS_UPDATE,
      AssistantServerMessageType.TOOL_CALLS,
      AssistantServerMessageType.TRANSFER_DESTINATION_REQUEST,
      AssistantServerMessageType.USER_INTERRUPTED,
    ),

  var endCallPhrases: MutableList<String> = mutableListOf(),

  ) : AssistantUnion
