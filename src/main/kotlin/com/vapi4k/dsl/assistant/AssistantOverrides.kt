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

import com.vapi4k.dsl.assistant.model.Model
import com.vapi4k.dsl.assistant.transcriber.DeepgramTranscriber
import com.vapi4k.dsl.assistant.transcriber.GladiaTranscriber
import com.vapi4k.dsl.assistant.voice.VoiceOverrides
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.DeepgramTranscriberDto
import com.vapi4k.responses.assistant.GladiaTranscriberDto
import kotlinx.serialization.json.JsonElement

interface AssistantOverridesUnion {
  var firstMessageMode: String
  var recordingEnabled: Boolean
  var hipaaEnabled: Boolean
  var silenceTimeoutSeconds: Int
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var llmRequestNonPunctuatedDelaySeconds: Double
  var numWordsToInterruptAssistant: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var backchannelingEnabled: Boolean
  var backgroundDenoisingEnabled: Boolean
  var modelOutputInMessagesEnabled: Boolean
  var name: String
  var firstMessage: String
  var voicemailMessage: String
  var endCallMessage: String
  var serverUrl: String
  var serverUrlSecret: String
  val clientMessages: MutableSet<String>
  val serverMessages: MutableSet<String>
}

@AssistantDslMarker
data class AssistantOverrides internal constructor(
  val request: JsonElement,
  private val dto: AssistantOverridesDto,
) : AssistantOverridesUnion by dto {

  fun model(block: Model.() -> Unit) {
    Model(request, dto.modelDto).apply(block)
  }

  fun voice(block: VoiceOverrides.() -> Unit) {
    VoiceOverrides(dto.voiceDto).apply(block)
  }

  fun deepGramTranscriber(block: DeepgramTranscriber.() -> Unit) {
    dto.transcriberDto = DeepgramTranscriberDto().also { DeepgramTranscriber(it).apply(block) }
  }

  fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) {
    dto.transcriberDto = GladiaTranscriberDto().also { GladiaTranscriber(it).apply(block) }
  }
}
