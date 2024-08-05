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

import com.vapi4k.api.assistant.enums.AssistantClientMessageType
import com.vapi4k.api.assistant.enums.AssistantServerMessageType
import com.vapi4k.api.assistant.enums.BackgroundSoundType
import com.vapi4k.api.assistant.enums.FirstMessageModeType

interface AssistantProperties {
  var backchannelingEnabled: Boolean?
  var backgroundDenoisingEnabled: Boolean?
  var backgroundSound: BackgroundSoundType
  var endCallMessage: String
  var firstMessage: String
  var firstMessageMode: FirstMessageModeType

  /**
  When this is enabled, no logs, recordings, or transcriptions will be stored.
  At the end of the call, you will still receive an end-of-call-report message
  to store on your server. Defaults to false.
   */
  var hipaaEnabled: Boolean?
  var llmRequestDelaySeconds: Double
  var llmRequestNonPunctuatedDelaySeconds: Double
  var maxDurationSeconds: Int
  var modelOutputInMessagesEnabled: Boolean?
  var name: String
  var numWordsToInterruptAssistant: Int

  /**
  This sets whether the assistant's calls are recorded. Defaults to true.
   */
  var recordingEnabled: Boolean?
  var responseDelaySeconds: Double
  var serverUrl: String
  var serverUrlSecret: String

  /**
  How many seconds of silence to wait before ending the call. Defaults to 30.
   */
  var silenceTimeoutSeconds: Int

  /**
  This is the message that the assistant will say if the call is forwarded to voicemail.

  If unspecified, it will hang up.
   */
  var voicemailMessage: String

  var dialKeypadFunctionEnabled: Boolean?
  var endCallFunctionEnabled: Boolean?
  var forwardingPhoneNumber: String

  /**
  These are the messages that will be sent to your Client SDKs.
  Default is CONVERSATION_UPDATE, FUNCTION_CALL, HANG, MODEL_OUTPUT, SPEECH_UPDATE,
  STATUS_UPDATE, TRANSCRIPT, TOOL_CALLS, USER_INTERRUPTED, and VOICE_INPUT.
   */
  val clientMessages: MutableSet<AssistantClientMessageType>
  val serverMessages: MutableSet<AssistantServerMessageType>
}
