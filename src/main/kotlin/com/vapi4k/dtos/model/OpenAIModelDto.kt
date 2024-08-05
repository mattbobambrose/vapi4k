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

package com.vapi4k.dtos.model

import com.vapi4k.api.model.enums.ModelType
import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.dsl.model.OpenAIModelProperties
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class OpenAIModelDto(
  var model: String = "",
  @Transient
  override var modelType: OpenAIModelType = OpenAIModelType.UNSPECIFIED,
  @Transient
  override var customModel: String = "",
  var fallbackModels: MutableList<String> = mutableListOf(),
  @Transient
  override val fallbackModelTypes: MutableList<OpenAIModelType> = mutableListOf(),
  @Transient
  override val customFallbackModels: MutableList<String> = mutableListOf(),
  override var semanticCachingEnabled: Boolean? = null,
) : AbstractModelDto(),
  OpenAIModelProperties,
  CommonModelDto {
  @EncodeDefault
  override val provider: ModelType = ModelType.OPEN_AI

  fun assignEnumOverrides() {
    model = customModel.ifEmpty { modelType.desc }
    fallbackModels.addAll(fallbackModelTypes.map { it.desc } + customFallbackModels)
  }

  fun verifyValues() {
    if (modelType.isSpecified() && customModel.isNotEmpty())
      error("openAIModel{} cannot have both modelType and customModel values")

    if (modelType.isNotSpecified() && customModel.isEmpty())
      error("openAIModel{} must have either a modelType or customModel value")
  }
}
