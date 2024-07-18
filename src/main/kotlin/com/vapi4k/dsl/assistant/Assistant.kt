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

import com.vapi4k.AssistantDslMarker
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.responses.assistant.AssistantRequestMessageResponse
import kotlinx.serialization.json.JsonElement

interface AssistantUnion {
  var name: String
  var firstMessage: String
  var recordingEnabled: Boolean
  var hipaaEnabled: Boolean
  var serverUrl: String
  var serverUrlSecret: String
  var forwardingPhoneNumber: String
  var endCallFunctionEnabled: Boolean
  var dialKeypadFunctionEnabled: Boolean
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var silenceTimeoutSeconds: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var numWordsToInterruptAssistant: Int
  var voicemailMessage: String
  var endCallMessage: String
  var backchannelingEnabled: Boolean
  var backgroundDenoisingEnabled: Boolean
  var modelOutputInMessagesEnabled: Boolean
  var llmRequestNonPunctuatedDelaySeconds: Double
  var firstMessageMode: FirstMessageModeType
  var clientMessages: MutableSet<AssistantClientMessageType>
  var serverMessages: MutableSet<AssistantServerMessageType>
}

@AssistantDslMarker
data class Assistant internal constructor(
  internal val request: JsonElement,
  internal val requestResponse: AssistantRequestMessageResponse
) : AssistantUnion by requestResponse.messageResponse.assistant {
  internal val assistantDto get() = requestResponse.messageResponse.assistant

  fun model(block: Model.() -> Unit) {
    Model(this, assistantDto.model).apply(block)
  }

  fun voice(block: Voice.() -> Unit) {
    Voice(assistantDto.voice).apply(block)
  }

  fun overrides(block: AssistantOverrides.() -> Unit) {
    AssistantOverrides(request, requestResponse.messageResponse.assistantOverrides).apply(block)
  }

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
