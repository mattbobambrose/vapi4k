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

package com.vapi4k.responses.assistant

import com.vapi4k.dsl.assistant.ModelUnion
import kotlinx.serialization.Serializable

@Serializable
data class ModelDto(
  override var provider: String = "",
  override var model: String = "",
  override var temperature: Int = 0,
  override var maxTokens: Int = 0,
  override var emotionRecognitionEnabled: Boolean = false,

  val messages: MutableList<RoleMessage> = mutableListOf(),
  val tools: MutableList<ToolDto> = mutableListOf(),
  val toolIds: MutableList<String> = mutableListOf(),
  val functions: MutableList<FunctionDto> = mutableListOf(),
  val knowledgeBase: KnowledgeBaseDto = KnowledgeBaseDto(),
) : ModelUnion

@Serializable
data class RoleMessage(
  var role: String = "",
  var content: String = "",
  var delay: Int = -1,
)
