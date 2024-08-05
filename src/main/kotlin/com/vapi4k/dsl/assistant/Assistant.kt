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

import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.BackgroundSoundType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import com.vapi4k.dsl.model.AnthropicModel
import com.vapi4k.dsl.model.AnyscaleModel
import com.vapi4k.dsl.model.CustomLLMModel
import com.vapi4k.dsl.model.DeepInfraModel
import com.vapi4k.dsl.model.GroqModel
import com.vapi4k.dsl.model.OpenAIModel
import com.vapi4k.dsl.model.OpenRouterModel
import com.vapi4k.dsl.model.PerplexityAIModel
import com.vapi4k.dsl.model.TogetherAIModel
import com.vapi4k.dsl.model.VapiModel
import com.vapi4k.dsl.transcriber.DeepgramTranscriber
import com.vapi4k.dsl.transcriber.GladiaTranscriber
import com.vapi4k.dsl.transcriber.TalkscriberTranscriber
import com.vapi4k.dsl.voice.AzureVoice
import com.vapi4k.dsl.voice.CartesiaVoice
import com.vapi4k.dsl.voice.DeepgramVoice
import com.vapi4k.dsl.voice.ElevenLabsVoice
import com.vapi4k.dsl.voice.LMNTVoice
import com.vapi4k.dsl.voice.NeetsVoice
import com.vapi4k.dsl.voice.OpenAIVoice
import com.vapi4k.dsl.voice.PlayHTVoice
import com.vapi4k.dsl.voice.RimeAIVoice

interface AssistantProperties {
  var backchannelingEnabled: Boolean?
  var backgroundDenoisingEnabled: Boolean?
  var backgroundSound: BackgroundSoundType
  var endCallMessage: String
  var firstMessage: String
  var firstMessageMode: FirstMessageModeType

  /**
  When this is enabled, no logs, recordings, or transcriptions will be stored.
  At the end of the call, you will still receive an end-of-call-report message
  to store on your server. Defaults to false.
   */
  var hipaaEnabled: Boolean?
  var llmRequestDelaySeconds: Double
  var llmRequestNonPunctuatedDelaySeconds: Double
  var maxDurationSeconds: Int
  var modelOutputInMessagesEnabled: Boolean?
  var name: String
  var numWordsToInterruptAssistant: Int

  /**
  This sets whether the assistant's calls are recorded. Defaults to true.
   */
  var recordingEnabled: Boolean?
  var responseDelaySeconds: Double
  var serverUrl: String
  var serverUrlSecret: String

  /**
  How many seconds of silence to wait before ending the call. Defaults to 30.
   */
  var silenceTimeoutSeconds: Int

  /**
  This is the message that the assistant will say if the call is forwarded to voicemail.

  If unspecified, it will hang up.
   */
  var voicemailMessage: String

  var dialKeypadFunctionEnabled: Boolean?
  var endCallFunctionEnabled: Boolean?
  var forwardingPhoneNumber: String

  /**
  These are the messages that will be sent to your Client SDKs.
  Default is CONVERSATION_UPDATE, FUNCTION_CALL, HANG, MODEL_OUTPUT, SPEECH_UPDATE,
  STATUS_UPDATE, TRANSCRIPT, TOOL_CALLS, USER_INTERRUPTED, and VOICE_INPUT.
   */
  val clientMessages: MutableSet<AssistantClientMessageType>
  val serverMessages: MutableSet<AssistantServerMessageType>
}

@AssistantDslMarker
interface Assistant : AssistantProperties {
  var videoRecordingEnabled: Boolean?

  fun voicemailDetection(block: VoicemailDetection.() -> Unit): VoicemailDetection

  // Transcribers
  fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit): DeepgramTranscriber

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit): GladiaTranscriber

  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit): TalkscriberTranscriber

  // Models
  fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel

  fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel

  fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel

  fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel

  fun groqModel(block: GroqModel.() -> Unit): GroqModel

  fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel

  fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel

  fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel

  fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel

  fun vapiModel(block: VapiModel.() -> Unit): VapiModel

  // Voices
  fun azureVoice(block: AzureVoice.() -> Unit): AzureVoice

  fun cartesiaVoice(block: CartesiaVoice.() -> Unit): CartesiaVoice

  fun deepgramVoice(block: DeepgramVoice.() -> Unit): DeepgramVoice

  fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit): ElevenLabsVoice

  fun lmntVoice(block: LMNTVoice.() -> Unit): LMNTVoice

  fun neetsVoice(block: NeetsVoice.() -> Unit): NeetsVoice

  fun openAIVoice(block: OpenAIVoice.() -> Unit): OpenAIVoice

  fun playHTVoice(block: PlayHTVoice.() -> Unit): PlayHTVoice

  fun rimeAIVoice(block: RimeAIVoice.() -> Unit): RimeAIVoice

  // AssistantOverrides
  fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides

  fun analysisPlan(block: AnalysisPlan.() -> Unit): AnalysisPlan

  fun artifactPlan(block: ArtifactPlan.() -> Unit): ArtifactPlan
}
