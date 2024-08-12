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

import com.vapi4k.api.assistant.AnalysisPlan
import com.vapi4k.api.assistant.ArtifactPlan
import com.vapi4k.api.assistant.VoicemailDetection
import com.vapi4k.api.model.AnthropicModel
import com.vapi4k.api.model.AnyscaleModel
import com.vapi4k.api.model.CustomLLMModel
import com.vapi4k.api.model.DeepInfraModel
import com.vapi4k.api.model.GroqModel
import com.vapi4k.api.model.OpenAIModel
import com.vapi4k.api.model.OpenRouterModel
import com.vapi4k.api.model.PerplexityAIModel
import com.vapi4k.api.model.TogetherAIModel
import com.vapi4k.api.model.VapiModel
import com.vapi4k.api.transcriber.DeepgramTranscriber
import com.vapi4k.api.transcriber.GladiaTranscriber
import com.vapi4k.api.transcriber.TalkscriberTranscriber
import com.vapi4k.api.voice.AzureVoice
import com.vapi4k.api.voice.CartesiaVoice
import com.vapi4k.api.voice.DeepgramVoice
import com.vapi4k.api.voice.ElevenLabsVoice
import com.vapi4k.api.voice.LMNTVoice
import com.vapi4k.api.voice.NeetsVoice
import com.vapi4k.api.voice.OpenAIVoice
import com.vapi4k.api.voice.PlayHTVoice
import com.vapi4k.api.voice.RimeAIVoice

interface CommonAssistantFunctions {
  /**
  This determines whether the video is recorded during the call. Default is false. Only relevant for `webCall` type.
   */
  var videoRecordingEnabled: Boolean

  /**
  These are the settings to configure or disable voicemail detection. Alternatively, voicemail detection can be
  configured using the model.tools=[VoicemailTool]. This uses Twilio's built-in detection while the VoicemailTool
  relies on the model to detect if a voicemail was reached. You can use neither of them, one of them, or both of them.
  By default, Twilio built-in detection is enabled while VoicemailTool is not.
   */
  fun voicemailDetection(block: VoicemailDetection.() -> Unit): VoicemailDetection

  // Transcribers

  /**
  Builder for the Deepgram transcriber.
   */
  fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit): DeepgramTranscriber

  /**
  Builder for the Gladia transcriber.
   */
  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit): GladiaTranscriber

  /**
  Builder for the Talkscriber transcriber.
   */
  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit): TalkscriberTranscriber

  // Models

  /**
  Builder for the Anyscale model.
   */
  fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel

  /**
  Builder for the Anthropic model.
   */
  fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel

  /**
  Builder for the CustomLLM model.
   */
  fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel

  /**
  Builder for the DeepInfra model.
   */
  fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel

  /**
  Builder for the Groq model.
   */
  fun groqModel(block: GroqModel.() -> Unit): GroqModel

  /**
  Builder for the OpenAI model.
   */
  fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel

  /**
  Builder for the OpenRouter model.
   */
  fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel

  /**
  Builder for the PerplexityAI model.
   */
  fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel

  /**
  Builder for the TogetherAI model.
   */
  fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel

  /**
  Builder for the Vapi model.
   */
  fun vapiModel(block: VapiModel.() -> Unit): VapiModel

  // Voices

  /**
  Builder for the Azure voice.
   */
  fun azureVoice(block: AzureVoice.() -> Unit): AzureVoice

  /**
  Builder for the Cartesia voice.
   */
  fun cartesiaVoice(block: CartesiaVoice.() -> Unit): CartesiaVoice

  /**
  Builder for the Deepgram voice.
   */
  fun deepgramVoice(block: DeepgramVoice.() -> Unit): DeepgramVoice

  /**
  Builder for the ElevenLabs voice.
   */
  fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit): ElevenLabsVoice

  /**
  Builder for the LMNT voice.
   */
  fun lmntVoice(block: LMNTVoice.() -> Unit): LMNTVoice

  /**
  Builder for the Neets voice.
   */
  fun neetsVoice(block: NeetsVoice.() -> Unit): NeetsVoice

  /**
  Builder for the OpenAI voice.
   */
  fun openAIVoice(block: OpenAIVoice.() -> Unit): OpenAIVoice

  /**
  Builder for the PlayHT voice.
   */
  fun playHTVoice(block: PlayHTVoice.() -> Unit): PlayHTVoice

  /**
  Builder for the RimeAI voice.
   */
  fun rimeAIVoice(block: RimeAIVoice.() -> Unit): RimeAIVoice

  /**
  This is the plan for analysis of assistant's calls. Stored in `call.analysis`.
   */
  fun analysisPlan(block: AnalysisPlan.() -> Unit): AnalysisPlanImpl

  /**
  <p>This is the plan for artifacts generated during assistant's calls. Stored in <code>call.artifact</code>.
  <br>Note: <code>recordingEnabled</code> is currently at the root level. It will be moved to <code>artifactPlan</code> in the future, but will remain backwards compatible.
  </p>
   */
  fun artifactPlan(block: ArtifactPlan.() -> Unit): ArtifactPlanImpl
}
