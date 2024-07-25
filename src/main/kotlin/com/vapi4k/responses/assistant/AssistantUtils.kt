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

import com.vapi4k.common.CacheId
import com.vapi4k.common.DuplicateChecker
import com.vapi4k.dsl.assistant.model.AnthropicModel
import com.vapi4k.dsl.assistant.model.AnthropicModelImpl
import com.vapi4k.dsl.assistant.model.AnyscaleModel
import com.vapi4k.dsl.assistant.model.AnyscaleModelImpl
import com.vapi4k.dsl.assistant.model.CustomLLMModel
import com.vapi4k.dsl.assistant.model.CustomLLMModelImpl
import com.vapi4k.dsl.assistant.model.DeepInfraModel
import com.vapi4k.dsl.assistant.model.DeepInfraModelImpl
import com.vapi4k.dsl.assistant.model.GroqModel
import com.vapi4k.dsl.assistant.model.GroqModelImpl
import com.vapi4k.dsl.assistant.model.OpenAIModel
import com.vapi4k.dsl.assistant.model.OpenAIModelImpl
import com.vapi4k.dsl.assistant.model.OpenRouterModel
import com.vapi4k.dsl.assistant.model.OpenRouterModelImpl
import com.vapi4k.dsl.assistant.model.PerplexityAIModel
import com.vapi4k.dsl.assistant.model.PerplexityAIModelImpl
import com.vapi4k.dsl.assistant.model.TogetherAIModel
import com.vapi4k.dsl.assistant.model.TogetherAIModelImpl
import com.vapi4k.dsl.assistant.model.VapiModel
import com.vapi4k.dsl.assistant.model.VapiModelImpl
import com.vapi4k.dsl.assistant.transcriber.DeepgramTranscriber
import com.vapi4k.dsl.assistant.transcriber.GladiaTranscriber
import com.vapi4k.dsl.assistant.transcriber.TalkscriberTranscriber
import com.vapi4k.dsl.assistant.voice.AzureVoice
import com.vapi4k.dsl.assistant.voice.CartesiaVoice
import com.vapi4k.dsl.assistant.voice.DeepgramVoice
import com.vapi4k.dsl.assistant.voice.ElevenLabsVoice
import com.vapi4k.dsl.assistant.voice.LMNTVoice
import com.vapi4k.dsl.assistant.voice.NeetsVoice
import com.vapi4k.dsl.assistant.voice.OpenAIVoice
import com.vapi4k.dsl.assistant.voice.PlayHTVoice
import com.vapi4k.dsl.assistant.voice.RimeAIVoice
import com.vapi4k.responses.assistant.model.AnthropicModelDto
import com.vapi4k.responses.assistant.model.AnyscaleModelDto
import com.vapi4k.responses.assistant.model.CommonModelDto
import com.vapi4k.responses.assistant.model.CustomLLMModelDto
import com.vapi4k.responses.assistant.model.DeepInfraModelDto
import com.vapi4k.responses.assistant.model.GroqModelDto
import com.vapi4k.responses.assistant.model.OpenAIModelDto
import com.vapi4k.responses.assistant.model.OpenRouterModelDto
import com.vapi4k.responses.assistant.model.PerplexityAIModelDto
import com.vapi4k.responses.assistant.model.TogetherAIModelDto
import com.vapi4k.responses.assistant.model.VapiModelDto
import com.vapi4k.responses.assistant.transcriber.CommonTranscriberDto
import com.vapi4k.responses.assistant.transcriber.DeepgramTranscriberDto
import com.vapi4k.responses.assistant.transcriber.GladiaTranscriberDto
import com.vapi4k.responses.assistant.transcriber.TalkscriberTranscriberDto
import com.vapi4k.responses.assistant.voice.AzureVoiceDto
import com.vapi4k.responses.assistant.voice.CartesiaVoiceDto
import com.vapi4k.responses.assistant.voice.CommonVoiceDto
import com.vapi4k.responses.assistant.voice.DeepgramVoiceDto
import com.vapi4k.responses.assistant.voice.ElevenLabsVoiceDto
import com.vapi4k.responses.assistant.voice.LMNTVoiceDto
import com.vapi4k.responses.assistant.voice.NeetsVoiceDto
import com.vapi4k.responses.assistant.voice.OpenAIVoiceDto
import com.vapi4k.responses.assistant.voice.PlayHTVoiceDto
import com.vapi4k.responses.assistant.voice.RimeAIVoiceDto
import kotlinx.serialization.json.JsonElement

interface AssistantBridge {
  var transcriberDto: CommonTranscriberDto?
  var modelDto: CommonModelDto?
  var voiceDto: CommonVoiceDto?
}

// Transcribers
fun deepgramTranscriber(
  dto: AssistantBridge,
  transcriberChecker: DuplicateChecker,
  block: DeepgramTranscriber.() -> Unit,
): DeepgramTranscriber {
  transcriberChecker.check("deepGramTranscriber{} already called")
  val transcriberDto = DeepgramTranscriberDto().also { dto.transcriberDto = it }
  return DeepgramTranscriber(transcriberDto)
    .apply(block)
    .apply { transcriberDto.verifyValues() }
}

fun gladiaTranscriber(
  dto: AssistantBridge,
  transcriberChecker: DuplicateChecker,
  block: GladiaTranscriber.() -> Unit,
): GladiaTranscriber {
  transcriberChecker.check("gladiaTranscriber{} already called")
  val transcriberDto = GladiaTranscriberDto().also { dto.transcriberDto = it }
  return GladiaTranscriber(transcriberDto)
    .apply(block)
    .apply { transcriberDto.verifyValues() }
}

fun talkscriberTranscriber(
  dto: AssistantBridge,
  transcriberChecker: DuplicateChecker,
  block: TalkscriberTranscriber.() -> Unit,
): TalkscriberTranscriber {
  transcriberChecker.check("talkscriberTranscriber{} already called")
  val transcriberDto = TalkscriberTranscriberDto().also { dto.transcriberDto = it }
  return TalkscriberTranscriber(transcriberDto)
    .apply(block)
    .apply { transcriberDto.verifyValues() }
}

// Models
fun anyscaleModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: AnyscaleModel.() -> Unit,
): AnyscaleModel {
  modelChecker.check("anyscaleModel{} already called")
  val modelDto = AnyscaleModelDto().also { dto.modelDto = it }
  return AnyscaleModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun anthropicModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: AnthropicModel.() -> Unit,
): AnthropicModel {
  modelChecker.check("anthropicModel{} already called")
  val modelDto = AnthropicModelDto().also { dto.modelDto = it }
  return AnthropicModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun customLLMModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: CustomLLMModel.() -> Unit,
): CustomLLMModel {
  modelChecker.check("customLLMModel{} already called")
  val modelDto = CustomLLMModelDto().also { dto.modelDto = it }
  return CustomLLMModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun deepInfraModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: DeepInfraModel.() -> Unit,
): DeepInfraModel {
  modelChecker.check("deepInfraModel{} already called")
  val modelDto = DeepInfraModelDto().also { dto.modelDto = it }
  return DeepInfraModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun groqModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: GroqModel.() -> Unit,
): GroqModel {
  modelChecker.check("groqModel{} already called")
  val modelDto = GroqModelDto().also { dto.modelDto = it }
  return GroqModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun openAIModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: OpenAIModel.() -> Unit,
): OpenAIModel {
  modelChecker.check("openAIModel{} already called")
  val modelDto = OpenAIModelDto().also { dto.modelDto = it }
  return OpenAIModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun openRouterModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: OpenRouterModel.() -> Unit,
): OpenRouterModel {
  modelChecker.check("openRouterModel{} already called")
  val modelDto = OpenRouterModelDto().also { dto.modelDto = it }
  return OpenRouterModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun perplexityAIModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: PerplexityAIModel.() -> Unit,
): PerplexityAIModel {
  modelChecker.check("perplexityAIModel{} already called")
  val modelDto = PerplexityAIModelDto().also { dto.modelDto = it }
  return PerplexityAIModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun togetherAIModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: TogetherAIModel.() -> Unit,
): TogetherAIModel {
  modelChecker.check("togetherAIModel{} already called")
  val modelDto = TogetherAIModelDto().also { dto.modelDto = it }
  return TogetherAIModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

fun vapiModel(
  request: JsonElement,
  cacheId: CacheId,
  dto: AssistantBridge,
  modelChecker: DuplicateChecker,
  block: VapiModel.() -> Unit,
): VapiModel {
  modelChecker.check("vapiModel{} already called")
  val modelDto = VapiModelDto().also { dto.modelDto = it }
  return VapiModelImpl(request, cacheId, modelDto)
    .apply(block)
    .apply { modelDto.verifyValues() }
}

// Voices
fun azureVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: AzureVoice.() -> Unit,
): AzureVoice {
  voiceChecker.check("azureVoice{} already called")
  val voiceDto = AzureVoiceDto().also { dto.voiceDto = it }
  return AzureVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun cartesiaVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: CartesiaVoice.() -> Unit,
): CartesiaVoice {
  voiceChecker.check("cartesiaVoice{} already called")
  val voiceDto = CartesiaVoiceDto().also { dto.voiceDto = it }
  return CartesiaVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun deepgramVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: DeepgramVoice.() -> Unit,
): DeepgramVoice {
  voiceChecker.check("deepgramVoice{} already called")
  val voiceDto = DeepgramVoiceDto().also { dto.voiceDto = it }
  return DeepgramVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun elevenLabsVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: ElevenLabsVoice.() -> Unit,
): ElevenLabsVoice {
  voiceChecker.check("elevenLabsVoice{} already called")
  val voiceDto = ElevenLabsVoiceDto().also { dto.voiceDto = it }
  return ElevenLabsVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun lmntVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: LMNTVoice.() -> Unit,
): LMNTVoice {
  voiceChecker.check("lmntVoice{} already called")
  val voiceDto = LMNTVoiceDto().also { dto.voiceDto = it }
  return LMNTVoice(voiceDto)
    .apply(block)
}

fun neetsVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: NeetsVoice.() -> Unit,
): NeetsVoice {
  voiceChecker.check("neetsVoice{} already called")
  val voiceDto = NeetsVoiceDto().also { dto.voiceDto = it }
  return NeetsVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun openAIVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: OpenAIVoice.() -> Unit,
): OpenAIVoice {
  voiceChecker.check("openAIVoice{} already called")
  val voiceDto = OpenAIVoiceDto().also { dto.voiceDto = it }
  return OpenAIVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun playHTVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: PlayHTVoice.() -> Unit,
): PlayHTVoice {
  voiceChecker.check("playHTVoice{} already called")
  val voiceDto = PlayHTVoiceDto().also { dto.voiceDto = it }
  return PlayHTVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}

fun rimeAIVoice(
  dto: AssistantBridge,
  voiceChecker: DuplicateChecker,
  block: RimeAIVoice.() -> Unit,
): RimeAIVoice {
  voiceChecker.check("rimeAIVoice{} already called")
  val voiceDto = RimeAIVoiceDto().also { dto.voiceDto = it }
  return RimeAIVoice(voiceDto)
    .apply(block)
    .apply { voiceDto.verifyValues() }
}
