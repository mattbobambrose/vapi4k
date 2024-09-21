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

package assistants

import com.vapi4k.api.assistant.Assistant
import com.vapi4k.api.model.enums.AnthropicModelType
import com.vapi4k.api.model.enums.GroqModelType
import com.vapi4k.api.model.enums.OpenAIModelType

object Models {
  fun Assistant.anthropicExample() {
    anthropicModel {
      modelType = AnthropicModelType.CLAUDE_3_HAIKU
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.anyscaleExample() {
    anyscaleModel {
      model = "Model_Description"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.customLLMExample() {
    customLLMModel {
      model = "Model_Description"
      url = "Model_URL"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.deepInfraExample() {
    deepInfraModel {
      model = "Model_Description"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.groqExample() {
    groqModel {
      modelType = GroqModelType.LLAMA3_70B
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.openAIExample() {
    openAIModel {
      modelType = OpenAIModelType.GPT_4_TURBO
      semanticCachingEnabled = true
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      fallbackModelTypes += OpenAIModelType.GPT_4O
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.openRouterExample() {
    openRouterModel {
      model = "Model_Description"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.perplexityAIExample() {
    perplexityAIModel {
      model = "Model_Description"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.togetherAIExample() {
    togetherAIModel {
      model = "Model_Description"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }

  fun Assistant.vapiExample() {
    vapiModel {
      model = "Model_Description"
      emotionRecognitionEnabled = true
      maxTokens = 250
      temperature = 0.5
      systemMessage = "You're a polite AI assistant named Vapi who is fun to talk with."
    }
  }
}
