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

package com.vapi4k.dsl.transcriber

import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.model.enums.DeepgramModelType
import com.vapi4k.dsl.transcriber.enums.DeepgramLanguageType
import com.vapi4k.dtos.transcriber.DeepgramTranscriberDto

interface DeepgramTranscriberProperties {
  var transcriberModel: DeepgramModelType
  var customModel: String
  var transcriberLanguage: DeepgramLanguageType
  var customLanguage: String
  var smartFormat: Boolean?
  val keywords: MutableSet<String>
}

@AssistantDslMarker
class DeepgramTranscriber internal constructor(private val dto: DeepgramTranscriberDto) :
  DeepgramTranscriberProperties by dto
