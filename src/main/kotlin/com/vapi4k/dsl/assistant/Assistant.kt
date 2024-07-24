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
import com.vapi4k.dsl.assistant.model.AnyscaleModel
import com.vapi4k.dsl.assistant.model.CustomLLMModel
import com.vapi4k.dsl.assistant.model.DeepInfraModel
import com.vapi4k.dsl.assistant.model.GroqModel
import com.vapi4k.dsl.assistant.model.OpenAIModel
import com.vapi4k.dsl.assistant.model.OpenRouterModel
import com.vapi4k.dsl.assistant.model.PerplexityAIModel
import com.vapi4k.dsl.assistant.model.TogetherAIModel
import com.vapi4k.dsl.assistant.model.VapiModel
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
import com.vapi4k.responses.assistant.model.AnthropicModelDto
import com.vapi4k.responses.assistant.model.AnyscaleModelDto
import com.vapi4k.responses.assistant.model.CustomLLMModelDto
import com.vapi4k.responses.assistant.model.DeepInfraModelDto
import com.vapi4k.responses.assistant.model.GroqModelDto
import com.vapi4k.responses.assistant.model.OpenAIModelDto
import com.vapi4k.responses.assistant.model.OpenRouterModelDto
import com.vapi4k.responses.assistant.model.PerplexityAIModelDto
import com.vapi4k.responses.assistant.model.TogetherAIModelDto
import com.vapi4k.responses.assistant.model.VapiModelDto
import com.vapi4k.responses.assistant.transcriber.DeepgramTranscriberDto
import com.vapi4k.responses.assistant.transcriber.GladiaTranscriberDto
import com.vapi4k.responses.assistant.transcriber.TalkscriberTranscriberDto
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

interface AssistantUnion {
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
data class Assistant internal constructor(
  val request: JsonElement,
  private val cacheId: CacheId,
  internal val assistantDto: AssistantDto,
  internal val assistantOverridesDto: AssistantOverridesDto,
) : AssistantUnion by assistantDto {
  private val transcriberChecker = DuplicateChecker()
  private val modelChecker = DuplicateChecker()
  private val voiceChecker = DuplicateChecker()

  // Transcribers
  fun deepGramTranscriber(block: DeepgramTranscriber.() -> Unit): DeepgramTranscriber {
    transcriberChecker.check("deepGramTranscriber{} already called")
    val transcriberDto = DeepgramTranscriberDto().also { assistantDto.transcriberDto = it }
    return DeepgramTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit): GladiaTranscriber {
    transcriberChecker.check("gladiaTranscriber{} already called")
    val transcriberDto = GladiaTranscriberDto().also { assistantDto.transcriberDto = it }
    return GladiaTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit): TalkscriberTranscriber {
    transcriberChecker.check("talkscriberTranscriber{} already called")
    val transcriberDto = TalkscriberTranscriberDto().also { assistantDto.transcriberDto = it }
    return TalkscriberTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  // Models
  fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel {
    modelChecker.check("anyscaleModel{} already called")
    val modelDto = AnyscaleModelDto().also { assistantDto.modelDto = it }
    return AnyscaleModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel {
    modelChecker.check("anthropicModel{} already called")
    val modelDto = AnthropicModelDto().also { assistantDto.modelDto = it }
    return AnthropicModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel {
    modelChecker.check("customLLMModel{} already called")
    val modelDto = CustomLLMModelDto().also { assistantDto.modelDto = it }
    return CustomLLMModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel {
    modelChecker.check("deepInfraModel{} already called")
    val modelDto = DeepInfraModelDto().also { assistantDto.modelDto = it }
    return DeepInfraModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun groqModel(block: GroqModel.() -> Unit): GroqModel {
    modelChecker.check("groqModel{} already called")
    val modelDto = GroqModelDto().also { assistantDto.modelDto = it }
    return GroqModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    modelChecker.check("openAIModel{} already called")
    val modelDto = OpenAIModelDto().also { assistantDto.modelDto = it }
    return OpenAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel {
    modelChecker.check("openRouterModel{} already called")
    val modelDto = OpenRouterModelDto().also { assistantDto.modelDto = it }
    return OpenRouterModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel {
    modelChecker.check("perplexityAIModel{} already called")
    val modelDto = PerplexityAIModelDto().also { assistantDto.modelDto = it }
    return PerplexityAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel {
    modelChecker.check("togetherAIModel{} already called")
    val modelDto = TogetherAIModelDto().also { assistantDto.modelDto = it }
    return TogetherAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun vapiModel(block: VapiModel.() -> Unit): VapiModel {
    modelChecker.check("vapiModel{} already called")
    val modelDto = VapiModelDto().also { assistantDto.modelDto = it }
    return VapiModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }


  // Voices
//  fun voice(block: Voice.() -> Unit): Voice {
//    val voiceDto = VoiceDto()
//    assistantDto.voiceDto = voiceDto
//    return Voice(voiceDto).apply(block)
//  }

  fun azureVoice(block: AzureVoice.() -> Unit): AzureVoice {
    voiceChecker.check("azureVoice{} already called")
    val voiceDto = AzureVoiceDto()
    assistantDto.voiceDto = voiceDto
    return AzureVoice(voiceDto).apply(block)
  }

  fun cartesiaVoice(block: CartesiaVoice.() -> Unit): CartesiaVoice {
    voiceChecker.check("cartesiaVoice{} already called")
    val voiceDto = CartesiaVoiceDto()
    assistantDto.voiceDto = voiceDto
    return CartesiaVoice(voiceDto).apply(block)
  }

  fun deepgramVoice(block: DeepgramVoice.() -> Unit): DeepgramVoice {
    voiceChecker.check("deepgramVoice{} already called")
    val voiceDto = DeepgramVoiceDto()
    assistantDto.voiceDto = voiceDto
    return DeepgramVoice(voiceDto).apply(block)
  }

  fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit): ElevenLabsVoice {
    voiceChecker.check("elevenLabsVoice{} already called")
    val voiceDto = ElevenLabsVoiceDto()
    assistantDto.voiceDto = voiceDto
    return ElevenLabsVoice(voiceDto).apply(block)
  }

  fun lmntVoice(block: LMNTVoice.() -> Unit): LMNTVoice {
    voiceChecker.check("lmntVoice{} already called")
    val voiceDto = LMNTVoiceDto()
    assistantDto.voiceDto = voiceDto
    return LMNTVoice(voiceDto).apply(block)
  }

  fun neetsVoice(block: NeetsVoice.() -> Unit): NeetsVoice {
    voiceChecker.check("neetsVoice{} already called")
    val voiceDto = NeetsVoiceDto()
    assistantDto.voiceDto = voiceDto
    return NeetsVoice(voiceDto).apply(block)
  }

  fun openAIVoice(block: OpenAIVoice.() -> Unit): OpenAIVoice {
    voiceChecker.check("openAIVoice{} already called")
    val voiceDto = OpenAIVoiceDto()
    assistantDto.voiceDto = voiceDto
    return OpenAIVoice(voiceDto).apply(block)
  }

  fun playHTVoice(block: PlayHTVoice.() -> Unit): PlayHTVoice {
    voiceChecker.check("playHTVoice{} already called")
    val voiceDto = PlayHTVoiceDto()
    assistantDto.voiceDto = voiceDto
    return PlayHTVoice(voiceDto).apply(block)
  }

  fun rimeAIVoice(block: RimeAIVoice.() -> Unit): RimeAIVoice {
    voiceChecker.check("rimeAIVoice{} already called")
    val voiceDto = RimeAIVoiceDto()
    assistantDto.voiceDto = voiceDto
    return RimeAIVoice(voiceDto).apply(block)
  }

  // AssistantOverrides
  fun assistantOverrides(block: AssistantOverrides.() -> Unit) {
    AssistantOverrides(request, cacheId, assistantOverridesDto).apply(block)
  }

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
