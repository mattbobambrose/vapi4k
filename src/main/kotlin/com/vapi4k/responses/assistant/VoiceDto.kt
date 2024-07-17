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


import com.vapi4k.dsl.assistant.ProviderType
import com.vapi4k.dsl.assistant.VoiceType
import com.vapi4k.dsl.assistant.VoiceUnion
import kotlinx.serialization.Serializable

@Serializable
data class VoiceDto(
  override var inputPreprocessingEnabled: Boolean = false,
  override var inputReformattingEnabled: Boolean = false,
  override var inputMinCharacters: Int = 0,
  override var fillerInjectionEnabled: Boolean = false,
  override var provider: ProviderType = ProviderType.UNKNOWN,
  override var voiceId: VoiceType = VoiceType.UNKNOWN,
  override var speed: Double = 0.0,
  override var inputPunctuationBoundaries: List<PunctuationType> = listOf()
) : VoiceUnion
