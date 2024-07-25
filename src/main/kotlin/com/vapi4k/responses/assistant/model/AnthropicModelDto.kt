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

import com.vapi4k.dsl.assistant.enums.AnthropicModelType
import com.vapi4k.dsl.assistant.enums.ModelType
import com.vapi4k.dsl.assistant.model.AnthropicModelProperties
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AnthropicModelDto(
  var model: String = "",
  @Transient
  override var modelType: AnthropicModelType = AnthropicModelType.UNSPECIFIED,
  @Transient
  override var customModel: String = "",
) : AbstractModelDto(), AnthropicModelProperties, CommonModelDto {
  @EncodeDefault
  override val provider: ModelType = ModelType.ANTHROPIC

  fun assignEnumOverrides() {
    model = if (customModel.isNotEmpty()) customModel else modelType.desc
  }

  fun verifyValues() {
    if (modelType.isSpecified() && customModel.isNotEmpty())
      error("anthropicModel{} cannot have both modelType and customModel values")

    if (modelType.isNotSpecified() && customModel.isEmpty())
      error("anthropicModel{} must have either a modelType or customModel value")
  }
}
