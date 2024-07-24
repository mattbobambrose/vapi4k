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

package com.vapi4k.responses.assistant.transcriber

import com.vapi4k.dsl.assistant.enums.GladiaLanguageType
import com.vapi4k.dsl.assistant.enums.GladiaModelType
import com.vapi4k.dsl.assistant.enums.TranscriberType
import com.vapi4k.dsl.assistant.transcriber.GladiaTranscriberUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GladiaTranscriberDto(
  @EncodeDefault
  override val provider: TranscriberType = TranscriberType.GLADIA,

  var model: String = "",

  @Transient
  override var transcriberModel: GladiaModelType = GladiaModelType.UNSPECIFIED,

  @Transient
  override var customModel: String = "",

  var language: String = "",

  @Transient
  override var transcriberLanguage: GladiaLanguageType = GladiaLanguageType.UNSPECIFIED,

  @Transient
  override var customLanguage: String = "",

  override var languageBehavior: String = "",
  override var transcriptionHint: String = "",
  override var prosody: Boolean = false,
  override var audioEnhancer: Boolean = false,
) : GladiaTranscriberUnion, AbstractTranscriberDto {
  override fun assignEnumOverrides() {
    model = if (customModel.isNotEmpty()) customModel else transcriberLanguage.desc
    language = if (customLanguage.isNotEmpty()) customLanguage else transcriberLanguage.desc
  }

  override fun verifyValues() {
    if (transcriberModel.isSpecified() && customModel.isNotEmpty())
      error("gladiaTranscriber{} cannot have both transcriberModel and customModel values")

    if (transcriberModel.isNotSpecified() && customModel.isEmpty())
      error("gladiaTranscriber{} requires a transcriberModel or customModel value")

    if (transcriberLanguage.isSpecified() && customLanguage.isNotEmpty())
      error("gladiaTranscriber{} cannot have both transcriberLanguage and customLanguage values")

    if (transcriberLanguage.isNotSpecified() && customLanguage.isEmpty())
      error("gladiaTranscriber{} requires a transcriberLanguage or customLanguage value")
  }
}
