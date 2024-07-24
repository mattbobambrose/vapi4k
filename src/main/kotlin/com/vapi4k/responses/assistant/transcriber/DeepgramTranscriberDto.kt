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

import com.vapi4k.dsl.assistant.enums.DeepgramLanguageType
import com.vapi4k.dsl.assistant.enums.DeepgramModelType
import com.vapi4k.dsl.assistant.enums.TranscriberType
import com.vapi4k.dsl.assistant.transcriber.DeepgramTranscriberUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class DeepgramTranscriberDto(
  @EncodeDefault
  override val provider: TranscriberType = TranscriberType.DEEPGRAM,

  var model: String = "",

  @Transient
  override var transcriberModel: DeepgramModelType = DeepgramModelType.UNSPECIFIED,

  @Transient
  override var customModel: String = "",

  var language: String = "",

  @Transient
  override var transcriberLanguage: DeepgramLanguageType = DeepgramLanguageType.UNSPECIFIED,

  @Transient
  override var customLanguage: String = "",

  override var smartFormat: Boolean = false,
  override val keywords: MutableSet<String> = mutableSetOf(),
) : DeepgramTranscriberUnion, AbstractTranscriberDto {

  override fun assignEnumOverrides() {
    model = if (customModel.isNotEmpty()) customModel else transcriberLanguage.desc
    language = if (customLanguage.isNotEmpty()) customLanguage else transcriberLanguage.desc
  }

  override fun verifyValues() {
    if (transcriberModel.isSpecified() && customModel.isNotEmpty())
      error("deepgramTranscriber{} cannot have both transcriberModel and customModel values")

    if (transcriberModel.isNotSpecified() && customModel.isEmpty())
      error("deepgramTranscriber{} requires transcriberModel or customModel value")

    if (transcriberLanguage.isSpecified() && customLanguage.isNotEmpty())
      error("deepgramTranscriber{} cannot have both transcriberLanguage and customLanguage values")

    if (transcriberLanguage.isNotSpecified() && customLanguage.isEmpty())
      error("deepgramTranscriber{} requires a transcriberLanguage or customLanguagevalue")
  }
}
