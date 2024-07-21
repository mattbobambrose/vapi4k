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

package com.vapi4k.dsl.assistant

import com.vapi4k.dsl.assistant.enums.GladiaLanguageType
import com.vapi4k.dsl.assistant.enums.GladiaModelType
import com.vapi4k.dsl.assistant.enums.TranscriberType
import com.vapi4k.responses.assistant.GladiaTranscriberDto

interface GladiaTranscriberUnion {
  var transcriberModel: GladiaModelType
  var transcriberLanguage: GladiaLanguageType
  var languageBehavior: String
  var transcriptionHint: String
  var prosody: Boolean
  var audioEnhancer: Boolean
}

class GladiaTranscriber internal constructor(val transcriberDto: GladiaTranscriberDto) :
  GladiaTranscriberUnion by transcriberDto {
  init {
    transcriberDto.provider = TranscriberType.GLADIA
  }
}
