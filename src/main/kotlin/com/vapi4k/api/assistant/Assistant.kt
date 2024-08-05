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

package com.vapi4k.api.assistant

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
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantProperties

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
