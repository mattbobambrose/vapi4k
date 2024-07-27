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

import com.vapi4k.common.AssistantCacheId
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.model.AnthropicModel
import com.vapi4k.dsl.model.AnthropicModelImpl
import com.vapi4k.dsl.model.AnyscaleModel
import com.vapi4k.dsl.model.AnyscaleModelImpl
import com.vapi4k.dsl.model.CustomLLMModel
import com.vapi4k.dsl.model.CustomLLMModelImpl
import com.vapi4k.dsl.model.DeepInfraModel
import com.vapi4k.dsl.model.DeepInfraModelImpl
import com.vapi4k.dsl.model.GroqModel
import com.vapi4k.dsl.model.GroqModelImpl
import com.vapi4k.dsl.model.OpenAIModel
import com.vapi4k.dsl.model.OpenAIModelImpl
import com.vapi4k.dsl.model.OpenRouterModel
import com.vapi4k.dsl.model.OpenRouterModelImpl
import com.vapi4k.dsl.model.PerplexityAIModel
import com.vapi4k.dsl.model.PerplexityAIModelImpl
import com.vapi4k.dsl.model.TogetherAIModel
import com.vapi4k.dsl.model.TogetherAIModelImpl
import com.vapi4k.dsl.model.VapiModel
import com.vapi4k.dsl.model.VapiModelImpl
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
import com.vapi4k.dtos.VoicemailDetectionDto
import com.vapi4k.dtos.model.AnthropicModelDto
import com.vapi4k.dtos.model.AnyscaleModelDto
import com.vapi4k.dtos.model.CommonModelDto
import com.vapi4k.dtos.model.CustomLLMModelDto
import com.vapi4k.dtos.model.DeepInfraModelDto
import com.vapi4k.dtos.model.GroqModelDto
import com.vapi4k.dtos.model.OpenAIModelDto
import com.vapi4k.dtos.model.OpenRouterModelDto
import com.vapi4k.dtos.model.PerplexityAIModelDto
import com.vapi4k.dtos.model.TogetherAIModelDto
import com.vapi4k.dtos.model.VapiModelDto
import com.vapi4k.dtos.transcriber.CommonTranscriberDto
import com.vapi4k.dtos.transcriber.DeepgramTranscriberDto
import com.vapi4k.dtos.transcriber.GladiaTranscriberDto
import com.vapi4k.dtos.transcriber.TalkscriberTranscriberDto
import com.vapi4k.dtos.voice.AzureVoiceDto
import com.vapi4k.dtos.voice.CartesiaVoiceDto
import com.vapi4k.dtos.voice.CommonVoiceDto
import com.vapi4k.dtos.voice.DeepgramVoiceDto
import com.vapi4k.dtos.voice.ElevenLabsVoiceDto
import com.vapi4k.dtos.voice.LMNTVoiceDto
import com.vapi4k.dtos.voice.NeetsVoiceDto
import com.vapi4k.dtos.voice.OpenAIVoiceDto
import com.vapi4k.dtos.voice.PlayHTVoiceDto
import com.vapi4k.dtos.voice.RimeAIVoiceDto
import com.vapi4k.utils.DuplicateChecker
import kotlinx.serialization.json.JsonElement

interface ModelDtoUnion {
  var transcriberDto: CommonTranscriberDto?
  var modelDto: CommonModelDto?
  var voiceDto: CommonVoiceDto?
}

interface ModelUnion {
  val request: JsonElement
  val sessionCacheId: SessionCacheId
  val assistantCacheId: AssistantCacheId
  val transcriberChecker: DuplicateChecker
  val modelChecker: DuplicateChecker
  val voiceChecker: DuplicateChecker
  val modelDtoUnion: ModelDtoUnion
  val voicemailDetectionDto: VoicemailDetectionDto
}

fun ModelUnion.voicemailDetectionBridge(
  block: VoicemailDetection.() -> Unit,
): VoicemailDetection {
  return VoicemailDetection(voicemailDetectionDto).apply(block)
}

// Transcribers
fun ModelUnion.deepgramTranscriberBridge(
  block: DeepgramTranscriber.() -> Unit,
): DeepgramTranscriber {
  transcriberChecker.check("deepGramTranscriber{} already called")
  val transcriberDto = DeepgramTranscriberDto().also { modelDtoUnion.transcriberDto = it }
  return DeepgramTranscriber(transcriberDto)
    .apply(block)
    .apply { transcriberDto.verifyValues() }
}

fun ModelUnion.gladiaTranscriberBridge(
  block: GladiaTranscriber.() -> Unit,
): GladiaTranscriber {
  transcriberChecker.check("gladiaTranscriber{} already called")
  val transcriberDto = GladiaTranscriberDto().also { modelDtoUnion.transcriberDto = it }
  return GladiaTranscriber(transcriberDto)
    .apply(block)
    .apply { transcriberDto.verifyValues() }
}

fun ModelUnion.talkscriberTranscriberBridge(
  block: TalkscriberTranscriber.() -> Unit,
): TalkscriberTranscriber {
  transcriberChecker.check("talkscriberTranscriber{} already called")
  val transcriberDto = TalkscriberTranscriberDto().also { modelDtoUnion.transcriberDto = it }
  return TalkscriberTranscriber(transcriberDto)
    .apply(block)
    .apply { transcriberDto.verifyValues() }
}

// Models
fun ModelUnion.anyscaleModelBridge(
  block: AnyscaleModel.() -> Unit,
): AnyscaleModel {
  modelChecker.check("anyscaleModel{} already called")
  val modelDto = AnyscaleModelDto().also { modelDtoUnion.modelDto = it }
  return AnyscaleModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.anthropicModelBridge(
  block: AnthropicModel.() -> Unit,
): AnthropicModel {
  val modelDto = AnthropicModelDto().also { modelDtoUnion.modelDto = it }
  return AnthropicModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.customLLMModelBridge(
  block: CustomLLMModel.() -> Unit,
): CustomLLMModel {
  modelChecker.check("customLLMModel{} already called")
  val modelDto = CustomLLMModelDto().also { modelDtoUnion.modelDto = it }
  return CustomLLMModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.deepInfraModelBridge(
  block: DeepInfraModel.() -> Unit,
): DeepInfraModel {
  modelChecker.check("deepInfraModel{} already called")
  val modelDto = DeepInfraModelDto().also { modelDtoUnion.modelDto = it }
  return DeepInfraModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.groqModelBridge(
  block: GroqModel.() -> Unit,
): GroqModel {
  modelChecker.check("groqModel{} already called")
  val modelDto = GroqModelDto().also { modelDtoUnion.modelDto = it }
  return GroqModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.openAIModelBridge(
  block: OpenAIModel.() -> Unit,
): OpenAIModel {
  modelChecker.check("openAIModel{} already called")
  val modelDto = OpenAIModelDto().also { modelDtoUnion.modelDto = it }
  return OpenAIModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.openRouterModelBridge(
  block: OpenRouterModel.() -> Unit,
): OpenRouterModel {
  modelChecker.check("openRouterModel{} already called")
  val modelDto = OpenRouterModelDto().also { modelDtoUnion.modelDto = it }
  return OpenRouterModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.perplexityAIModelBridge(
  block: PerplexityAIModel.() -> Unit,
): PerplexityAIModel {
  modelChecker.check("perplexityAIModel{} already called")
  val modelDto = PerplexityAIModelDto().also { modelDtoUnion.modelDto = it }
  return PerplexityAIModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.togetherAIModelBridge(
  block: TogetherAIModel.() -> Unit,
): TogetherAIModel {
  modelChecker.check("togetherAIModel{} already called")
  val modelDto = TogetherAIModelDto().also { modelDtoUnion.modelDto = it }
  return TogetherAIModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun ModelUnion.vapiModelBridge(
  block: VapiModel.() -> Unit,
): VapiModel {
  modelChecker.check("vapiModel{} already called")
  val modelDto = VapiModelDto().also { modelDtoUnion.modelDto = it }
  return VapiModelImpl(this, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

// Voices
fun ModelUnion.azureVoiceBridge(
  block: AzureVoice.() -> Unit,
): AzureVoice {
  voiceChecker.check("azureVoice{} already called")
  val voiceDto = AzureVoiceDto().also { modelDtoUnion.voiceDto = it }
  return AzureVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.cartesiaVoiceBridge(
  block: CartesiaVoice.() -> Unit,
): CartesiaVoice {
  voiceChecker.check("cartesiaVoice{} already called")
  val voiceDto = CartesiaVoiceDto().also { modelDtoUnion.voiceDto = it }
  return CartesiaVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.deepgramVoiceBridge(
  block: DeepgramVoice.() -> Unit,
): DeepgramVoice {
  voiceChecker.check("deepgramVoice{} already called")
  val voiceDto = DeepgramVoiceDto().also { modelDtoUnion.voiceDto = it }
  return DeepgramVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.elevenLabsVoiceBridge(
  block: ElevenLabsVoice.() -> Unit,
): ElevenLabsVoice {
  voiceChecker.check("elevenLabsVoice{} already called")
  val voiceDto = ElevenLabsVoiceDto().also { modelDtoUnion.voiceDto = it }
  return ElevenLabsVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.lmntVoiceBridge(
  block: LMNTVoice.() -> Unit,
): LMNTVoice {
  voiceChecker.check("lmntVoice{} already called")
  val voiceDto = LMNTVoiceDto().also { modelDtoUnion.voiceDto = it }
  return LMNTVoice(voiceDto)
    .apply(block)
}

fun ModelUnion.neetsVoiceBridge(
  block: NeetsVoice.() -> Unit,
): NeetsVoice {
  voiceChecker.check("neetsVoice{} already called")
  val voiceDto = NeetsVoiceDto().also { modelDtoUnion.voiceDto = it }
  return com.vapi4k.dsl.voice.NeetsVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.openAIVoiceBridge(
  block: OpenAIVoice.() -> Unit,
): OpenAIVoice {
  voiceChecker.check("openAIVoice{} already called")
  val voiceDto = OpenAIVoiceDto().also { modelDtoUnion.voiceDto = it }
  return OpenAIVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.playHTVoiceBridge(
  block: PlayHTVoice.() -> Unit,
): PlayHTVoice {
  voiceChecker.check("playHTVoice{} already called")
  val voiceDto = PlayHTVoiceDto().also { modelDtoUnion.voiceDto = it }
  return com.vapi4k.dsl.voice.PlayHTVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun ModelUnion.rimeAIVoiceBridge(
  block: RimeAIVoice.() -> Unit,
): RimeAIVoice {
  voiceChecker.check("rimeAIVoice{} already called")
  val voiceDto = RimeAIVoiceDto().also { modelDtoUnion.voiceDto = it }
  return com.vapi4k.dsl.voice.RimeAIVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}
