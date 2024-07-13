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

import com.vapi4k.plugin.Vapi4kConfig
import com.vapi4k.responses.assistant.AssistantDto

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
}

@AssistantDslMarker
data class Assistant(
  internal val config: Vapi4kConfig,
  internal val assistantDto: AssistantDto,
) :
  AssistantUnion by assistantDto {
  fun model(block: Model.() -> Unit) {
    Model(this, assistantDto.model).apply(block)
  }

}
