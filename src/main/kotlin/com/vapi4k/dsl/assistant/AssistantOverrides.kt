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
import com.vapi4k.dsl.assistant.enums.BackgroundSoundType
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
  var backgroundSound: BackgroundSoundType
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
}

data class AssistantOverridesImpl internal constructor(
  override val request: JsonElement,
  override val sessionCacheId: SessionCacheId,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AssistantOverridesProperties by assistantOverridesDto, AssistantOverrides, ModelUnion {
  override val transcriberChecker = DuplicateChecker()
  override val modelChecker = DuplicateChecker()
  override val voiceChecker = DuplicateChecker()
  override val assistantCacheId = nextAssistantCacheId()
  override val modelDtoUnion get() = assistantOverridesDto
  override val voicemailDetectionDto get() = assistantOverridesDto.voicemailDetectionDto

  override fun voicemailDetection(block: VoicemailDetection.() -> Unit): VoicemailDetection =
    voicemailDetectionBridge(block)

  // Transcribers
  override fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit) = deepgramTranscriberBridge(block)
  override fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) = gladiaTranscriberBridge(block)
  override fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit) = talkscriberTranscriberBridge(block)

  // Models
  override fun anyscaleModel(block: AnyscaleModel.() -> Unit) = anyscaleModelBridge(block)
  override fun anthropicModel(block: AnthropicModel.() -> Unit) = anthropicModelBridge(block)
  override fun customLLMModel(block: CustomLLMModel.() -> Unit) = customLLMModelBridge(block)
  override fun deepInfraModel(block: DeepInfraModel.() -> Unit) = deepInfraModelBridge(block)
  override fun groqModel(block: GroqModel.() -> Unit) = groqModelBridge(block)
  override fun openAIModel(block: OpenAIModel.() -> Unit) = openAIModelBridge(block)
  override fun openRouterModel(block: OpenRouterModel.() -> Unit) = openRouterModelBridge(block)
  override fun perplexityAIModel(block: PerplexityAIModel.() -> Unit) = perplexityAIModelBridge(block)
  override fun togetherAIModel(block: TogetherAIModel.() -> Unit) = togetherAIModelBridge(block)
  override fun vapiModel(block: VapiModel.() -> Unit) = vapiModelBridge(block)

  // Voices
  override fun azureVoice(block: AzureVoice.() -> Unit) = azureVoiceBridge(block)
  override fun cartesiaVoice(block: CartesiaVoice.() -> Unit) = cartesiaVoiceBridge(block)
  override fun deepgramVoice(block: DeepgramVoice.() -> Unit) = deepgramVoiceBridge(block)
  override fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit) = elevenLabsVoiceBridge(block)
  override fun lmntVoice(block: LMNTVoice.() -> Unit) = lmntVoiceBridge(block)
  override fun neetsVoice(block: NeetsVoice.() -> Unit) = neetsVoiceBridge(block)
  override fun openAIVoice(block: OpenAIVoice.() -> Unit) = openAIVoiceBridge(block)
  override fun playHTVoice(block: PlayHTVoice.() -> Unit) = playHTVoiceBridge(block)
  override fun rimeAIVoice(block: RimeAIVoice.() -> Unit) = rimeAIVoiceBridge(block)
}
