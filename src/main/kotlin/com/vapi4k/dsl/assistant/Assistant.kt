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
import com.vapi4k.dsl.voice.AzureVoice
import com.vapi4k.dsl.voice.CartesiaVoice
import com.vapi4k.dsl.voice.DeepgramVoice
import com.vapi4k.dsl.voice.ElevenLabsVoice
import com.vapi4k.dsl.voice.LMNTVoice
import com.vapi4k.dsl.voice.NeetsVoice
import com.vapi4k.dsl.voice.OpenAIVoice
import com.vapi4k.dsl.voice.PlayHTVoice
import com.vapi4k.dsl.voice.RimeAIVoice
import com.vapi4k.dtos.assistant.AssistantDto
import com.vapi4k.dtos.assistant.AssistantOverridesDto
import com.vapi4k.utils.DuplicateChecker
import com.vapi4k.utils.Utils.nextAssistantCacheId
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
  override val request: JsonElement,
  override val sessionCacheId: SessionCacheId,
  private val assistantDto: AssistantDto,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AssistantProperties by assistantDto, Assistant, ModelBridge {
  override val transcriberChecker = DuplicateChecker()
  override val modelChecker = DuplicateChecker()
  override val voiceChecker = DuplicateChecker()
  override val assistantCacheId = nextAssistantCacheId()

  // Transcribers
  override fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit) = deepgramTranscriber(assistantDto, block)
  override fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) = gladiaTranscriber(assistantDto, block)
  override fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit) =
    talkscriberTranscriber(assistantDto, block)

  // Models
  override fun anyscaleModel(block: AnyscaleModel.() -> Unit) = anyscaleModel(assistantDto, block)
  override fun anthropicModel(block: AnthropicModel.() -> Unit) = anthropicModel(assistantDto, block)
  override fun customLLMModel(block: CustomLLMModel.() -> Unit) = customLLMModel(assistantDto, block)
  override fun deepInfraModel(block: DeepInfraModel.() -> Unit) = deepInfraModel(assistantDto, block)
  override fun groqModel(block: GroqModel.() -> Unit) = groqModel(assistantDto, block)
  override fun openAIModel(block: OpenAIModel.() -> Unit) = openAIModel(assistantDto, block)
  override fun openRouterModel(block: OpenRouterModel.() -> Unit) = openRouterModel(assistantDto, block)
  override fun perplexityAIModel(block: PerplexityAIModel.() -> Unit) = perplexityAIModel(assistantDto, block)
  override fun togetherAIModel(block: TogetherAIModel.() -> Unit) = togetherAIModel(assistantDto, block)
  override fun vapiModel(block: VapiModel.() -> Unit) = vapiModel(assistantDto, block)

  // Voices
  override fun azureVoice(block: AzureVoice.() -> Unit) = azureVoice(assistantDto, block)
  override fun cartesiaVoice(block: CartesiaVoice.() -> Unit) = cartesiaVoice(assistantDto, block)
  override fun deepgramVoice(block: DeepgramVoice.() -> Unit) = deepgramVoice(assistantDto, block)
  override fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit) = elevenLabsVoice(assistantDto, block)
  override fun lmntVoice(block: LMNTVoice.() -> Unit) = lmntVoice(assistantDto, block)
  override fun neetsVoice(block: NeetsVoice.() -> Unit) = neetsVoice(assistantDto, block)
  override fun openAIVoice(block: OpenAIVoice.() -> Unit) = openAIVoice(assistantDto, block)
  override fun playHTVoice(block: PlayHTVoice.() -> Unit) = playHTVoice(assistantDto, block)
  override fun rimeAIVoice(block: RimeAIVoice.() -> Unit) = rimeAIVoice(assistantDto, block)

  // AssistantOverrides
  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(request, sessionCacheId, assistantOverridesDto).apply(block)

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
