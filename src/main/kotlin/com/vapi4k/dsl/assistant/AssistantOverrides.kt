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
import com.vapi4k.dsl.assistant.voice.Voice
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
import kotlinx.serialization.json.JsonElement

interface AssistantOverridesUnion {
  var firstMessageMode: String
  var recordingEnabled: Boolean?
  var hipaaEnabled: Boolean?
  var silenceTimeoutSeconds: Int
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var llmRequestNonPunctuatedDelaySeconds: Double
  var numWordsToInterruptAssistant: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var backchannelingEnabled: Boolean?
  var backgroundDenoisingEnabled: Boolean?
  var modelOutputInMessagesEnabled: Boolean?
  var name: String
  var firstMessage: String
  var voicemailMessage: String
  var endCallMessage: String
  var serverUrl: String
  var serverUrlSecret: String
  val clientMessages: MutableSet<String>
  val serverMessages: MutableSet<String>
}

@AssistantDslMarker
data class AssistantOverrides internal constructor(
  val request: JsonElement,
  private val cacheId: CacheId,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AssistantOverridesUnion by assistantOverridesDto {
  private val transcriberChecker = DuplicateChecker()
  private val modelChecker = DuplicateChecker()
  private val voiceChecker = DuplicateChecker()

  // Transcribers
  fun deepGramTranscriber(block: DeepgramTranscriber.() -> Unit): DeepgramTranscriber {
    transcriberChecker.check("deepGramTranscriber{} already called")
    val transcriberDto = DeepgramTranscriberDto().also { assistantOverridesDto.transcriberDto = it }
    return DeepgramTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit): GladiaTranscriber {
    transcriberChecker.check("gladiaTranscriber{} already called")
    val transcriberDto = GladiaTranscriberDto().also { assistantOverridesDto.transcriberDto = it }
    return GladiaTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit): TalkscriberTranscriber {
    transcriberChecker.check("talkscriberTranscriber{} already called")
    val transcriberDto = TalkscriberTranscriberDto().also { assistantOverridesDto.transcriberDto = it }
    return TalkscriberTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  // Models
  fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel {
    modelChecker.check("anyscaleModel{} already called")
    val modelDto = AnyscaleModelDto().also { assistantOverridesDto.modelDto = it }
    return AnyscaleModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel {
    modelChecker.check("anthropicModel{} already called")
    val modelDto = AnthropicModelDto().also { assistantOverridesDto.modelDto = it }
    return AnthropicModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel {
    modelChecker.check("customLLMModel{} already called")
    val modelDto = CustomLLMModelDto().also { assistantOverridesDto.modelDto = it }
    return CustomLLMModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel {
    modelChecker.check("deepInfraModel{} already called")
    val modelDto = DeepInfraModelDto().also { assistantOverridesDto.modelDto = it }
    return DeepInfraModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun groqModel(block: GroqModel.() -> Unit): GroqModel {
    modelChecker.check("groqModel{} already called")
    val modelDto = GroqModelDto().also { assistantOverridesDto.modelDto = it }
    return GroqModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    modelChecker.check("openAIModel{} already called")
    val modelDto = OpenAIModelDto().also { assistantOverridesDto.modelDto = it }
    return OpenAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel {
    modelChecker.check("openRouterModel{} already called")
    val modelDto = OpenRouterModelDto().also { assistantOverridesDto.modelDto = it }
    return OpenRouterModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel {
    modelChecker.check("perplexityAIModel{} already called")
    val modelDto = PerplexityAIModelDto().also { assistantOverridesDto.modelDto = it }
    return PerplexityAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel {
    modelChecker.check("togetherAIModel{} already called")
    val modelDto = TogetherAIModelDto().also { assistantOverridesDto.modelDto = it }
    return TogetherAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun vapiModel(block: VapiModel.() -> Unit): VapiModel {
    modelChecker.check("vapiModel{} already called")
    val modelDto = VapiModelDto().also { assistantOverridesDto.modelDto = it }
    return VapiModel(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }

  fun voice(block: Voice.() -> Unit) {
    Voice(assistantOverridesDto.voiceDto).apply(block)
  }
}
