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

import com.vapi4k.common.CacheId
import com.vapi4k.common.DuplicateChecker
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import com.vapi4k.dsl.assistant.model.AnthropicModel
import com.vapi4k.dsl.assistant.model.AnthropicModelImpl
import com.vapi4k.dsl.assistant.model.AnyscaleModel
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
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.responses.assistant.AssistantDto
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.AssistantUtils.anyscaleModel
import com.vapi4k.responses.assistant.AssistantUtils.deepgramTranscriber
import com.vapi4k.responses.assistant.AssistantUtils.gladiaTranscriber
import com.vapi4k.responses.assistant.AssistantUtils.talkscriberTranscriber
import com.vapi4k.responses.assistant.model.AnthropicModelDto
import com.vapi4k.responses.assistant.model.CustomLLMModelDto
import com.vapi4k.responses.assistant.model.DeepInfraModelDto
import com.vapi4k.responses.assistant.model.GroqModelDto
import com.vapi4k.responses.assistant.model.OpenAIModelDto
import com.vapi4k.responses.assistant.model.OpenRouterModelDto
import com.vapi4k.responses.assistant.model.PerplexityAIModelDto
import com.vapi4k.responses.assistant.model.TogetherAIModelDto
import com.vapi4k.responses.assistant.model.VapiModelDto
import com.vapi4k.responses.assistant.voice.AzureVoiceDto
import com.vapi4k.responses.assistant.voice.CartesiaVoiceDto
import com.vapi4k.responses.assistant.voice.DeepgramVoiceDto
import com.vapi4k.responses.assistant.voice.ElevenLabsVoiceDto
import com.vapi4k.responses.assistant.voice.LMNTVoiceDto
import com.vapi4k.responses.assistant.voice.NeetsVoiceDto
import com.vapi4k.responses.assistant.voice.OpenAIVoiceDto
import com.vapi4k.responses.assistant.voice.PlayHTVoiceDto
import com.vapi4k.responses.assistant.voice.RimeAIVoiceDto
import kotlinx.serialization.json.JsonElement

interface AssistantProperties {
  var name: String
  var firstMessage: String
  var recordingEnabled: Boolean?
  var hipaaEnabled: Boolean?
  var serverUrl: String
  var serverUrlSecret: String
  var forwardingPhoneNumber: String
  var endCallFunctionEnabled: Boolean?
  var dialKeypadFunctionEnabled: Boolean?
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var silenceTimeoutSeconds: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var numWordsToInterruptAssistant: Int
  var voicemailMessage: String
  var endCallMessage: String
  var backchannelingEnabled: Boolean?
  var backgroundDenoisingEnabled: Boolean?
  var modelOutputInMessagesEnabled: Boolean?
  var llmRequestNonPunctuatedDelaySeconds: Double
  var firstMessageMode: FirstMessageModeType
  var clientMessages: MutableSet<AssistantClientMessageType>
  var serverMessages: MutableSet<AssistantServerMessageType>
}

@AssistantDslMarker
interface Assistant : AssistantProperties {
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
}

data class AssistantImpl internal constructor(
  internal val request: JsonElement,
  private val cacheId: CacheId,
  internal val assistantDto: AssistantDto,
  internal val assistantOverridesDto: AssistantOverridesDto,
) : AssistantProperties by assistantDto, Assistant {
  private val transcriberChecker = DuplicateChecker()
  private val modelChecker = DuplicateChecker()
  private val voiceChecker = DuplicateChecker()

  // Transcribers
  override fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit): DeepgramTranscriber =
    deepgramTranscriber(assistantDto, transcriberChecker, block)

  override fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit): GladiaTranscriber =
    gladiaTranscriber(assistantDto, transcriberChecker, block)

  override fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit): TalkscriberTranscriber =
    talkscriberTranscriber(assistantDto, transcriberChecker, block)


  // Models
  override fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel =
    anyscaleModel(request, cacheId, assistantDto, modelChecker, block)


  override fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModelImpl {
    modelChecker.check("anthropicModel{} already called")
    val modelDto = AnthropicModelDto().also { assistantDto.modelDto = it }
    return AnthropicModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel {
    modelChecker.check("customLLMModel{} already called")
    val modelDto = CustomLLMModelDto().also { assistantDto.modelDto = it }
    return CustomLLMModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel {
    modelChecker.check("deepInfraModel{} already called")
    val modelDto = DeepInfraModelDto().also { assistantDto.modelDto = it }
    return DeepInfraModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun groqModel(block: GroqModel.() -> Unit): GroqModel {
    modelChecker.check("groqModel{} already called")
    val modelDto = GroqModelDto().also { assistantDto.modelDto = it }
    return GroqModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    modelChecker.check("openAIModel{} already called")
    val modelDto = OpenAIModelDto().also { assistantDto.modelDto = it }
    return OpenAIModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel {
    modelChecker.check("openRouterModel{} already called")
    val modelDto = OpenRouterModelDto().also { assistantDto.modelDto = it }
    return OpenRouterModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel {
    modelChecker.check("perplexityAIModel{} already called")
    val modelDto = PerplexityAIModelDto().also { assistantDto.modelDto = it }
    return PerplexityAIModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel {
    modelChecker.check("togetherAIModel{} already called")
    val modelDto = TogetherAIModelDto().also { assistantDto.modelDto = it }
    return TogetherAIModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  override fun vapiModel(block: VapiModel.() -> Unit): VapiModel {
    modelChecker.check("vapiModel{} already called")
    val modelDto = VapiModelDto().also { assistantDto.modelDto = it }
    return VapiModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  // Voices
  override fun azureVoice(block: AzureVoice.() -> Unit): AzureVoice {
    voiceChecker.check("azureVoice{} already called")
    val voiceDto = AzureVoiceDto().also { assistantDto.voiceDto = it }
    return AzureVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun cartesiaVoice(block: CartesiaVoice.() -> Unit): CartesiaVoice {
    voiceChecker.check("cartesiaVoice{} already called")
    val voiceDto = CartesiaVoiceDto().also { assistantDto.voiceDto = it }
    return CartesiaVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun deepgramVoice(block: DeepgramVoice.() -> Unit): DeepgramVoice {
    voiceChecker.check("deepgramVoice{} already called")
    val voiceDto = DeepgramVoiceDto().also { assistantDto.voiceDto = it }
    return DeepgramVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit): ElevenLabsVoice {
    voiceChecker.check("elevenLabsVoice{} already called")
    val voiceDto = ElevenLabsVoiceDto().also { assistantDto.voiceDto = it }
    return ElevenLabsVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun lmntVoice(block: LMNTVoice.() -> Unit): LMNTVoice {
    voiceChecker.check("lmntVoice{} already called")
    val voiceDto = LMNTVoiceDto().also { assistantDto.voiceDto = it }
    return LMNTVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun neetsVoice(block: NeetsVoice.() -> Unit): NeetsVoice {
    voiceChecker.check("neetsVoice{} already called")
    val voiceDto = NeetsVoiceDto().also { assistantDto.voiceDto = it }
    return NeetsVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun openAIVoice(block: OpenAIVoice.() -> Unit): OpenAIVoice {
    voiceChecker.check("openAIVoice{} already called")
    val voiceDto = OpenAIVoiceDto().also { assistantDto.voiceDto = it }
    return OpenAIVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun playHTVoice(block: PlayHTVoice.() -> Unit): PlayHTVoice {
    voiceChecker.check("playHTVoice{} already called")
    val voiceDto = PlayHTVoiceDto().also { assistantDto.voiceDto = it }
    return PlayHTVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  override fun rimeAIVoice(block: RimeAIVoice.() -> Unit): RimeAIVoice {
    voiceChecker.check("rimeAIVoice{} already called")
    val voiceDto = RimeAIVoiceDto().also { assistantDto.voiceDto = it }
    return RimeAIVoice(voiceDto)
      .apply(block)
      .apply { voiceDto.verifyValues() }
  }

  // AssistantOverrides
  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(request, cacheId, assistantOverridesDto).apply(block)

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
