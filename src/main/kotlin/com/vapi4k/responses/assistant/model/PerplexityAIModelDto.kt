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

package com.vapi4k.responses.assistant.model

import com.vapi4k.dsl.assistant.enums.ModelType
import com.vapi4k.dsl.assistant.model.PerplexityAIModelUnion
import com.vapi4k.responses.assistant.FunctionDto
import com.vapi4k.responses.assistant.KnowledgeBaseDto
import com.vapi4k.responses.assistant.RoleMessage
import com.vapi4k.responses.assistant.ToolDto
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerplexityAIModelDto(
  @EncodeDefault
  override val provider: ModelType = ModelType.OPEN_AI,

  override var model: String = "",
  override var temperature: Int = -1,
  override var maxTokens: Int = -1,
  override var emotionRecognitionEnabled: Boolean? = null,
  override var numFastTurns: Int = -1,

  override val messages: MutableList<RoleMessage> = mutableListOf(),
  override val tools: MutableList<ToolDto> = mutableListOf(),
  override val toolIds: MutableList<String> = mutableListOf(),
  override val functions: MutableList<FunctionDto> = mutableListOf(),

  @SerialName("knowledgeBase")
  var knowledgeBaseDto: KnowledgeBaseDto? = null,
) : PerplexityAIModelUnion, AbstractModelDto {
  override fun verifyValues() {
    if (model.isEmpty())
      error("perplexityAIModel{} requires a model value")
  }
}
