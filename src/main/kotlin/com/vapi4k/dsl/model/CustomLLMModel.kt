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

package com.vapi4k.dsl.model

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.ModelUnion
import com.vapi4k.dsl.functions.Functions
import com.vapi4k.dsl.model.enums.MetaDataSendModeType
import com.vapi4k.dsl.tools.Tools
import com.vapi4k.dtos.model.CustomLLMModelDto

interface CustomLLMModelProperties {
  var model: String
  val emotionRecognitionEnabled: Boolean?
  var maxTokens: Int
  var metadataSendMode: MetaDataSendModeType
  var numFastTurns: Int
  var temperature: Int
  var toolIds: MutableSet<String>
  var url: String
}

@AssistantDslMarker
interface CustomLLMModel : CustomLLMModelProperties {
  var systemMessage: String
  var assistantMessage: String
  var functionMessage: String
  var toolMessage: String
  var userMessage: String

  fun tools(block: Tools.() -> Unit): Tools

  fun functions(block: Functions.() -> Unit): Functions

  fun knowledgeBase(block: KnowledgeBase.() -> Unit): KnowledgeBase
}

class CustomLLMModelImpl(
  modelUnion: ModelUnion,
  modelDto: CustomLLMModelDto,
) : AbstractModel(modelUnion, modelDto),
  CustomLLMModelProperties by modelDto,
  CustomLLMModel
