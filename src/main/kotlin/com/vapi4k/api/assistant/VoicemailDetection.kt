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

package com.vapi4k.api.assistant

import com.vapi4k.api.assistant.enums.VoicemailDetectionType
import com.vapi4k.dtos.VoicemailDetectionDto

interface VoicemailDetectionProperties {
  var enabled: Boolean?
  var machineDetectionTimeout: Int
  var machineDetectionSpeechThreshold: Int
  var machineDetectionSpeechEndThreshold: Int
  var machineDetectionSilenceTimeout: Int
  val voicemailDetectionTypes: MutableSet<VoicemailDetectionType>
}

@AssistantDslMarker
class VoicemailDetection internal constructor(
  private val dto: VoicemailDetectionDto,
) : VoicemailDetectionProperties by dto
