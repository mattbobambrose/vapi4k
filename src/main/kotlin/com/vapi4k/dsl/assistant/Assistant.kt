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

import com.vapi4k.common.DuplicateChecker
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
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
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.dtos.assistant.assistant.AssistantOverridesDto
import com.vapi4k.dtos.assistant.assistant.anthropicModel
import com.vapi4k.dtos.assistant.assistant.anyscaleModel
import com.vapi4k.dtos.assistant.assistant.azureVoice
import com.vapi4k.dtos.assistant.assistant.cartesiaVoice
import com.vapi4k.dtos.assistant.assistant.customLLMModel
import com.vapi4k.dtos.assistant.assistant.deepInfraModel
import com.vapi4k.dtos.assistant.assistant.deepgramTranscriber
import com.vapi4k.dtos.assistant.assistant.deepgramVoice
import com.vapi4k.dtos.assistant.assistant.elevenLabsVoice
import com.vapi4k.dtos.assistant.assistant.gladiaTranscriber
import com.vapi4k.dtos.assistant.assistant.groqModel
import com.vapi4k.dtos.assistant.assistant.lmntVoice
import com.vapi4k.dtos.assistant.assistant.neetsVoice
import com.vapi4k.dtos.assistant.assistant.openAIModel
import com.vapi4k.dtos.assistant.assistant.openAIVoice
import com.vapi4k.dtos.assistant.assistant.openRouterModel
import com.vapi4k.dtos.assistant.assistant.perplexityAIModel
import com.vapi4k.dtos.assistant.assistant.playHTVoice
import com.vapi4k.dtos.assistant.assistant.rimeAIVoice
import com.vapi4k.dtos.assistant.assistant.talkscriberTranscriber
import com.vapi4k.dtos.assistant.assistant.togetherAIModel
import com.vapi4k.dtos.assistant.assistant.vapiModel
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
  fun azureVoice(block: com.vapi4k.dsl.voice.AzureVoice.() -> Unit): com.vapi4k.dsl.voice.AzureVoice
  fun cartesiaVoice(block: com.vapi4k.dsl.voice.CartesiaVoice.() -> Unit): com.vapi4k.dsl.voice.CartesiaVoice
  fun deepgramVoice(block: com.vapi4k.dsl.voice.DeepgramVoice.() -> Unit): com.vapi4k.dsl.voice.DeepgramVoice
  fun elevenLabsVoice(block: com.vapi4k.dsl.voice.ElevenLabsVoice.() -> Unit): com.vapi4k.dsl.voice.ElevenLabsVoice
  fun lmntVoice(block: com.vapi4k.dsl.voice.LMNTVoice.() -> Unit): com.vapi4k.dsl.voice.LMNTVoice
  fun neetsVoice(block: com.vapi4k.dsl.voice.NeetsVoice.() -> Unit): com.vapi4k.dsl.voice.NeetsVoice
  fun openAIVoice(block: com.vapi4k.dsl.voice.OpenAIVoice.() -> Unit): com.vapi4k.dsl.voice.OpenAIVoice
  fun playHTVoice(block: com.vapi4k.dsl.voice.PlayHTVoice.() -> Unit): com.vapi4k.dsl.voice.PlayHTVoice
  fun rimeAIVoice(block: com.vapi4k.dsl.voice.RimeAIVoice.() -> Unit): com.vapi4k.dsl.voice.RimeAIVoice

  // AssistantOverrides
  fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides
}

data class AssistantImpl internal constructor(
  internal val request: JsonElement,
  private val sessionId: SessionId,
  internal val assistantDto: com.vapi4k.dtos.assistant.assistant.AssistantDto,
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
    anyscaleModel(request, sessionId, assistantDto, modelChecker, block)

  override fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel =
    anthropicModel(request, sessionId, assistantDto, modelChecker, block)

  override fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel =
    customLLMModel(request, sessionId, assistantDto, modelChecker, block)

  override fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel =
    deepInfraModel(request, sessionId, assistantDto, modelChecker, block)

  override fun groqModel(block: GroqModel.() -> Unit): GroqModel =
    groqModel(request, sessionId, assistantDto, modelChecker, block)

  override fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel =
    openAIModel(request, sessionId, assistantDto, modelChecker, block)

  override fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel =
    openRouterModel(request, sessionId, assistantDto, modelChecker, block)

  override fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel =
    perplexityAIModel(request, sessionId, assistantDto, modelChecker, block)

  override fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel =
    togetherAIModel(request, sessionId, assistantDto, modelChecker, block)

  override fun vapiModel(block: VapiModel.() -> Unit): VapiModel =
    vapiModel(request, sessionId, assistantDto, modelChecker, block)


  // Voices
  override fun azureVoice(block: com.vapi4k.dsl.voice.AzureVoice.() -> Unit): com.vapi4k.dsl.voice.AzureVoice =
    azureVoice(assistantDto, voiceChecker, block)

  override fun cartesiaVoice(block: com.vapi4k.dsl.voice.CartesiaVoice.() -> Unit): com.vapi4k.dsl.voice.CartesiaVoice =
    cartesiaVoice(assistantDto, voiceChecker, block)

  override fun deepgramVoice(block: com.vapi4k.dsl.voice.DeepgramVoice.() -> Unit): com.vapi4k.dsl.voice.DeepgramVoice =
    deepgramVoice(assistantDto, voiceChecker, block)

  override fun elevenLabsVoice(block: com.vapi4k.dsl.voice.ElevenLabsVoice.() -> Unit): com.vapi4k.dsl.voice.ElevenLabsVoice =
    elevenLabsVoice(assistantDto, voiceChecker, block)

  override fun lmntVoice(block: com.vapi4k.dsl.voice.LMNTVoice.() -> Unit): com.vapi4k.dsl.voice.LMNTVoice =
    lmntVoice(assistantDto, voiceChecker, block)

  override fun neetsVoice(block: com.vapi4k.dsl.voice.NeetsVoice.() -> Unit): com.vapi4k.dsl.voice.NeetsVoice =
    neetsVoice(assistantDto, voiceChecker, block)

  override fun openAIVoice(block: com.vapi4k.dsl.voice.OpenAIVoice.() -> Unit): com.vapi4k.dsl.voice.OpenAIVoice =
    openAIVoice(assistantDto, voiceChecker, block)

  override fun playHTVoice(block: com.vapi4k.dsl.voice.PlayHTVoice.() -> Unit): com.vapi4k.dsl.voice.PlayHTVoice =
    playHTVoice(assistantDto, voiceChecker, block)

  override fun rimeAIVoice(block: com.vapi4k.dsl.voice.RimeAIVoice.() -> Unit): com.vapi4k.dsl.voice.RimeAIVoice =
    rimeAIVoice(assistantDto, voiceChecker, block)


  // AssistantOverrides
  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(request, sessionId, assistantOverridesDto).apply(block)

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
