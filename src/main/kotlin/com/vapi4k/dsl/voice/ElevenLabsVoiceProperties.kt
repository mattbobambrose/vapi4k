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

package com.vapi4k.dsl.voice

import com.vapi4k.api.voice.enums.ElevenLabsVoiceIdType
import com.vapi4k.api.voice.enums.ElevenLabsVoiceModelType
import com.vapi4k.api.voice.enums.PunctuationType

interface ElevenLabsVoiceProperties {
  var customModel: String
  var customVoiceId: String
  var enableSsmlParsing: Boolean?
  var fillerInjectionEnabled: Boolean?
  var inputMinCharacters: Int
  var inputPreprocessingEnabled: Boolean?
  val inputPunctuationBoundaries: MutableSet<PunctuationType>
  var inputReformattingEnabled: Boolean?
  var modelType: ElevenLabsVoiceModelType
  var optimizeStreaming: Double
  var similarityBoost: Double
  var stability: Double
  var style: Double
  var useSpeakerBoost: Boolean?
  var voiceIdType: ElevenLabsVoiceIdType
}
