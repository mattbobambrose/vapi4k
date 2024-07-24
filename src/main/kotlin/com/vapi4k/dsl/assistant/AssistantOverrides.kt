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
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.model.AnthropicModelDto
import com.vapi4k.responses.assistant.model.AnyscaleModelDto
import com.vapi4k.responses.assistant.model.CustomLLMModelDto
import com.vapi4k.responses.assistant.model.DeepInfraModelDto
import com.vapi4k.responses.assistant.model.GroqModelDto
import com.vapi4k.responses.assistant.model.OpenAIModelDto
import com.vapi4k.responses.assistant.model.OpenRouterModelDto
import com.vapi4k.responses.assistant.model.TogetherAIModelDto
import com.vapi4k.responses.assistant.model.VapiModelDto
import com.vapi4k.responses.assistant.transcriber.DeepgramTranscriberDto
import com.vapi4k.responses.assistant.transcriber.GladiaTranscriberDto
import com.vapi4k.responses.assistant.transcriber.TalkscriberTranscriberDto
import kotlinx.serialization.json.JsonElement

interface AssistantOverridesUnion {
  var firstMessageMode: String
  var recordingEnabled: Boolean
  var hipaaEnabled: Boolean
  var silenceTimeoutSeconds: Int
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var llmRequestNonPunctuatedDelaySeconds: Double
  var numWordsToInterruptAssistant: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var backchannelingEnabled: Boolean
  var backgroundDenoisingEnabled: Boolean
  var modelOutputInMessagesEnabled: Boolean
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
  fun anyscaleModel(block: AnyscaleModel.() -> Unit): AnyscaleModel {
    val modelDto = AnyscaleModelDto()
    assistantOverridesDto.modelDto = modelDto
    return AnyscaleModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun anthropicModel(block: AnthropicModel.() -> Unit): AnthropicModel {
    val modelDto = AnthropicModelDto()
    assistantOverridesDto.modelDto = modelDto
    return AnthropicModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun customLLMModel(block: CustomLLMModel.() -> Unit): CustomLLMModel {
    val modelDto = CustomLLMModelDto()
    assistantOverridesDto.modelDto = modelDto
    return CustomLLMModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun deepInfraModel(block: DeepInfraModel.() -> Unit): DeepInfraModel {
    val modelDto = DeepInfraModelDto()
    assistantOverridesDto.modelDto = modelDto
    return DeepInfraModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun groqModel(block: GroqModel.() -> Unit): GroqModel {
    val modelDto = GroqModelDto()
    assistantOverridesDto.modelDto = modelDto
    return GroqModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun openAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    val modelDto = OpenAIModelDto()
    assistantOverridesDto.modelDto = modelDto
    return OpenAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun openRouterModel(block: OpenRouterModel.() -> Unit): OpenRouterModel {
    val modelDto = OpenRouterModelDto()
    assistantOverridesDto.modelDto = modelDto
    return OpenRouterModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun perplexityAIModel(block: OpenAIModel.() -> Unit): OpenAIModel {
    val modelDto = OpenAIModelDto()
    assistantOverridesDto.modelDto = modelDto
    return OpenAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun togetherAIModel(block: TogetherAIModel.() -> Unit): TogetherAIModel {
    val modelDto = TogetherAIModelDto()
    assistantOverridesDto.modelDto = modelDto
    return TogetherAIModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun vapiModel(block: VapiModel.() -> Unit): VapiModel {
    val modelDto = VapiModelDto()
    assistantOverridesDto.modelDto = modelDto
    return VapiModel(request, cacheId, modelDto)
      .apply(block)
      .apply {
        if (model.isEmpty()) error("Model model must be assigned")
      }
  }

  fun voice(block: Voice.() -> Unit) {
    Voice(assistantOverridesDto.voiceDto).apply(block)
  }

  fun deepGramTranscriber(block: DeepgramTranscriber.() -> Unit) {
    assistantOverridesDto.transcriberDto = DeepgramTranscriberDto().also { DeepgramTranscriber(it).apply(block) }
  }

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) {
    assistantOverridesDto.transcriberDto = GladiaTranscriberDto().also { GladiaTranscriber(it).apply(block) }
  }

  fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit) {
    assistantOverridesDto.transcriberDto = TalkscriberTranscriberDto().also { TalkscriberTranscriber(it).apply(block) }
  }
}
