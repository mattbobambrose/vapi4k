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

import com.vapi4k.common.SessionCacheId
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
import com.vapi4k.dtos.assistant.AssistantOverridesDto
import com.vapi4k.dtos.assistant.ModelBridge
import com.vapi4k.dtos.assistant.anthropicModel
import com.vapi4k.dtos.assistant.anyscaleModel
import com.vapi4k.dtos.assistant.azureVoice
import com.vapi4k.dtos.assistant.cartesiaVoice
import com.vapi4k.dtos.assistant.customLLMModel
import com.vapi4k.dtos.assistant.deepInfraModel
import com.vapi4k.dtos.assistant.deepgramTranscriber
import com.vapi4k.dtos.assistant.deepgramVoice
import com.vapi4k.dtos.assistant.elevenLabsVoice
import com.vapi4k.dtos.assistant.gladiaTranscriber
import com.vapi4k.dtos.assistant.groqModel
import com.vapi4k.dtos.assistant.lmntVoice
import com.vapi4k.dtos.assistant.neetsVoice
import com.vapi4k.dtos.assistant.openAIModel
import com.vapi4k.dtos.assistant.openAIVoice
import com.vapi4k.dtos.assistant.openRouterModel
import com.vapi4k.dtos.assistant.perplexityAIModel
import com.vapi4k.dtos.assistant.playHTVoice
import com.vapi4k.dtos.assistant.rimeAIVoice
import com.vapi4k.dtos.assistant.talkscriberTranscriber
import com.vapi4k.dtos.assistant.togetherAIModel
import com.vapi4k.dtos.assistant.vapiModel
import com.vapi4k.utils.DuplicateChecker
import com.vapi4k.utils.Utils.nextAssistantCacheId
import kotlinx.serialization.json.JsonElement

interface AssistantOverridesProperties {
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
interface AssistantOverrides : AssistantOverridesProperties {
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
}

data class AssistantOverridesImpl internal constructor(
  override val request: JsonElement,
  override val sessionCacheId: SessionCacheId,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AssistantOverridesProperties by assistantOverridesDto, AssistantOverrides, ModelBridge {
  private val transcriberChecker = DuplicateChecker()
  override val modelChecker = DuplicateChecker()
  private val voiceChecker = DuplicateChecker()
  override val assistantCacheId = nextAssistantCacheId()

  // Transcribers
  override fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit): DeepgramTranscriber =
    deepgramTranscriber(assistantOverridesDto, transcriberChecker, block)

  override fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit): GladiaTranscriber =
    gladiaTranscriber(assistantOverridesDto, transcriberChecker, block)

  override fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit): TalkscriberTranscriber =
    talkscriberTranscriber(assistantOverridesDto, transcriberChecker, block)


  // Models
  override fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel =
    anyscaleModel(this, assistantOverridesDto, block)

  override fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel =
    anthropicModel(this, assistantOverridesDto, block)

  override fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel =
    customLLMModel(this, assistantOverridesDto, block)

  override fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel =
    deepInfraModel(this, assistantOverridesDto, block)

  override fun groqModel(block: GroqModel.() -> Unit): GroqModel =
    groqModel(this, assistantOverridesDto, block)

  override fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel =
    openAIModel(this, assistantOverridesDto, block)

  override fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel =
    openRouterModel(this, assistantOverridesDto, block)

  override fun perplexityAIModel(block: PerplexityAIModel.() -> Unit): PerplexityAIModel =
    perplexityAIModel(this, assistantOverridesDto, block)

  override fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel =
    togetherAIModel(this, assistantOverridesDto, block)

  override fun vapiModel(block: VapiModel.() -> Unit): VapiModel =
    vapiModel(this, assistantOverridesDto, block)

  // Voices
  override fun azureVoice(block: AzureVoice.() -> Unit): AzureVoice =
    azureVoice(assistantOverridesDto, voiceChecker, block)

  override fun cartesiaVoice(block: CartesiaVoice.() -> Unit): CartesiaVoice =
    cartesiaVoice(assistantOverridesDto, voiceChecker, block)

  override fun deepgramVoice(block: DeepgramVoice.() -> Unit): DeepgramVoice =
    deepgramVoice(assistantOverridesDto, voiceChecker, block)

  override fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit): ElevenLabsVoice =
    elevenLabsVoice(assistantOverridesDto, voiceChecker, block)

  override fun lmntVoice(block: LMNTVoice.() -> Unit): LMNTVoice =
    lmntVoice(assistantOverridesDto, voiceChecker, block)

  override fun neetsVoice(block: NeetsVoice.() -> Unit): NeetsVoice =
    neetsVoice(assistantOverridesDto, voiceChecker, block)

  override fun openAIVoice(block: OpenAIVoice.() -> Unit): OpenAIVoice =
    openAIVoice(assistantOverridesDto, voiceChecker, block)

  override fun playHTVoice(block: PlayHTVoice.() -> Unit): PlayHTVoice =
    playHTVoice(assistantOverridesDto, voiceChecker, block)

  override fun rimeAIVoice(block: RimeAIVoice.() -> Unit): RimeAIVoice =
    rimeAIVoice(assistantOverridesDto, voiceChecker, block)
}
