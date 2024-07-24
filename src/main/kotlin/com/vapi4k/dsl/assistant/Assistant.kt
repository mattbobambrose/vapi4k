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
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import com.vapi4k.dsl.assistant.enums.VoiceType
import com.vapi4k.dsl.assistant.model.AnthropicModel
import com.vapi4k.dsl.assistant.model.AnyscaleModel
import com.vapi4k.dsl.assistant.model.CustomLLMModel
import com.vapi4k.dsl.assistant.model.DeepInfraModel
import com.vapi4k.dsl.assistant.model.GroqModel
import com.vapi4k.dsl.assistant.model.OpenAIModel
import com.vapi4k.dsl.assistant.model.OpenRouterModel
import com.vapi4k.dsl.assistant.model.TogetherAIModel
import com.vapi4k.dsl.assistant.model.VapiModel
import com.vapi4k.dsl.assistant.transcriber.DeepgramTranscriber
import com.vapi4k.dsl.assistant.transcriber.GladiaTranscriber
import com.vapi4k.dsl.assistant.transcriber.TalkscriberTranscriber
import com.vapi4k.dsl.assistant.voice.Voice
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.responses.assistant.AssistantDto
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.DeepgramTranscriberDto
import com.vapi4k.responses.assistant.GladiaTranscriberDto
import com.vapi4k.responses.assistant.TalkscriberTranscriberDto
import com.vapi4k.responses.assistant.VoiceDto
import com.vapi4k.responses.assistant.model.AnthropicModelDto
import com.vapi4k.responses.assistant.model.AnyscaleModelDto
import com.vapi4k.responses.assistant.model.CustomLLMModelDto
import com.vapi4k.responses.assistant.model.DeepInfraModelDto
import com.vapi4k.responses.assistant.model.GroqModelDto
import com.vapi4k.responses.assistant.model.OpenAIModelDto
import com.vapi4k.responses.assistant.model.OpenRouterModelDto
import com.vapi4k.responses.assistant.model.TogetherAIModelDto
import com.vapi4k.responses.assistant.model.VapiModelDto
import kotlinx.serialization.json.JsonElement

interface AssistantUnion {
  var name: String
  var firstMessage: String
  var recordingEnabled: Boolean
  var hipaaEnabled: Boolean
  var serverUrl: String
  var serverUrlSecret: String
  var forwardingPhoneNumber: String
  var endCallFunctionEnabled: Boolean
  var dialKeypadFunctionEnabled: Boolean
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var silenceTimeoutSeconds: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var numWordsToInterruptAssistant: Int
  var voicemailMessage: String
  var endCallMessage: String
  var backchannelingEnabled: Boolean
  var backgroundDenoisingEnabled: Boolean
  var modelOutputInMessagesEnabled: Boolean
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
  // errorMsg prevents further assistant or assistantId assignments
  private var errorMsg = ""

  private fun checkIfDeclared(newStr: String) = if (errorMsg.isNotEmpty()) error(errorMsg) else errorMsg = newStr

  // Transcribers
  fun deepGramTranscriber(block: DeepgramTranscriber.() -> Unit) {
    checkIfDeclared("Member already has an deepGramTranscriber assigned")
    assistantDto.transcriberDto = DeepgramTranscriberDto().also { DeepgramTranscriber(it).apply(block) }
  }

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) {
    checkIfDeclared("Member already has an gladiaTranscriber assigned")
    assistantDto.transcriberDto = GladiaTranscriberDto().also { GladiaTranscriber(it).apply(block) }
  }

  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit) {
    checkIfDeclared("Member already has an talkscriberTranscriber assigned")
    assistantDto.transcriberDto = TalkscriberTranscriberDto().also { TalkscriberTranscriber(it).apply(block) }
  }

  // Models
  fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel {
    val modelDto = AnyscaleModelDto()
    assistantDto.modelDto = modelDto
    return AnyscaleModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel {
    val modelDto = AnthropicModelDto()
    assistantDto.modelDto = modelDto
    return AnthropicModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel {
    val modelDto = CustomLLMModelDto()
    assistantDto.modelDto = modelDto
    return CustomLLMModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel {
    val modelDto = DeepInfraModelDto()
    assistantDto.modelDto = modelDto
    return DeepInfraModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun groqModel(block: GroqModel.() -> Unit): GroqModel {
    val modelDto = GroqModelDto()
    assistantDto.modelDto = modelDto
    return GroqModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    val modelDto = OpenAIModelDto()
    assistantDto.modelDto = modelDto
    return OpenAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel {
    val modelDto = OpenRouterModelDto()
    assistantDto.modelDto = modelDto
    return OpenRouterModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun perplexityAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    val modelDto = OpenAIModelDto()
    assistantDto.modelDto = modelDto
    return OpenAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel {
    val modelDto = TogetherAIModelDto()
    assistantDto.modelDto = modelDto
    return TogetherAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun vapiModel(block: VapiModel.() -> Unit): VapiModel {
    val modelDto = VapiModelDto()
    assistantDto.modelDto = modelDto
    return VapiModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }


  // Voices
  fun voice(block: Voice.() -> Unit) {
    val voiceDto = VoiceDto()
    assistantDto.voiceDto = voiceDto
    Voice(voiceDto).apply(block)
    if (voiceDto.voiceId == VoiceType.UNSPECIFIED) error("VoiceId must be set")
  }

  // AssistantOverrides
  fun assistantOverrides(block: AssistantOverrides.() -> Unit) {
    AssistantOverrides(request, cacheId, assistantOverridesDto).apply(block)
  }

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
