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
import com.vapi4k.dsl.model.ModelUnion
import com.vapi4k.dsl.model.analysisPlanUnion
import com.vapi4k.dsl.model.anthropicModelUnion
import com.vapi4k.dsl.model.anyscaleModelUnion
import com.vapi4k.dsl.model.artifactPlanUnion
import com.vapi4k.dsl.model.azureVoiceUnion
import com.vapi4k.dsl.model.cartesiaVoiceUnion
import com.vapi4k.dsl.model.customLLMModelUnion
import com.vapi4k.dsl.model.deepInfraModelUnion
import com.vapi4k.dsl.model.deepgramTranscriberUnion
import com.vapi4k.dsl.model.deepgramVoiceUnion
import com.vapi4k.dsl.model.elevenLabsVoiceUnion
import com.vapi4k.dsl.model.gladiaTranscriberUnion
import com.vapi4k.dsl.model.groqModelUnion
import com.vapi4k.dsl.model.lmntVoiceUnion
import com.vapi4k.dsl.model.neetsVoiceUnion
import com.vapi4k.dsl.model.openAIModelUnion
import com.vapi4k.dsl.model.openAIVoiceUnion
import com.vapi4k.dsl.model.openRouterModelUnion
import com.vapi4k.dsl.model.perplexityAIModelUnion
import com.vapi4k.dsl.model.playHTVoiceUnion
import com.vapi4k.dsl.model.rimeAIVoiceUnion
import com.vapi4k.dsl.model.talkscriberTranscriberUnion
import com.vapi4k.dsl.model.togetherAIModelUnion
import com.vapi4k.dsl.model.vapiModelUnion

abstract class AbstractAssistantImpl : ModelUnion {
  // Transcribers
  fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit) = deepgramTranscriberUnion(block)

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) = gladiaTranscriberUnion(block)

  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit) = talkscriberTranscriberUnion(block)

  // Models
  fun anyscaleModel(block: AnyscaleModel.() -> Unit) = anyscaleModelUnion(block)

  fun anthropicModel(block: AnthropicModel.() -> Unit) = anthropicModelUnion(block)

  fun customLLMModel(block: CustomLLMModel.() -> Unit) = customLLMModelUnion(block)

  fun deepInfraModel(block: DeepInfraModel.() -> Unit) = deepInfraModelUnion(block)

  fun groqModel(block: GroqModel.() -> Unit) = groqModelUnion(block)

  fun openAIModel(block: OpenAIModel.() -> Unit) = openAIModelUnion(block)

  fun openRouterModel(block: OpenRouterModel.() -> Unit) = openRouterModelUnion(block)

  fun perplexityAIModel(block: PerplexityAIModel.() -> Unit) = perplexityAIModelUnion(block)

  fun togetherAIModel(block: TogetherAIModel.() -> Unit) = togetherAIModelUnion(block)

  fun vapiModel(block: VapiModel.() -> Unit) = vapiModelUnion(block)

  // Voices
  fun azureVoice(block: AzureVoice.() -> Unit) = azureVoiceUnion(block)

  fun cartesiaVoice(block: CartesiaVoice.() -> Unit) = cartesiaVoiceUnion(block)

  fun deepgramVoice(block: DeepgramVoice.() -> Unit) = deepgramVoiceUnion(block)

  fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit) = elevenLabsVoiceUnion(block)

  fun lmntVoice(block: LMNTVoice.() -> Unit) = lmntVoiceUnion(block)

  fun neetsVoice(block: NeetsVoice.() -> Unit) = neetsVoiceUnion(block)

  fun openAIVoice(block: OpenAIVoice.() -> Unit) = openAIVoiceUnion(block)

  fun playHTVoice(block: PlayHTVoice.() -> Unit) = playHTVoiceUnion(block)

  fun rimeAIVoice(block: RimeAIVoice.() -> Unit) = rimeAIVoiceUnion(block)

  fun analysisPlan(block: AnalysisPlan.() -> Unit): AnalysisPlan = analysisPlanUnion(block)

  fun artifactPlan(block: ArtifactPlan.() -> Unit): ArtifactPlan = artifactPlanUnion(block)
}
