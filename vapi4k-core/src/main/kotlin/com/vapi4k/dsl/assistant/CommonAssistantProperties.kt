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
import com.vapi4k.dtos.TransportConfigurationDto

interface CommonAssistantProperties {
  /**
  <p>This determines whether the model says 'mhmm', 'ahem' etc. while user is speaking.
  <br>Default <code>false</code> while in beta.
  </p>
   */
  var backchannelingEnabled: Boolean?

  /**
  <p>This enables filtering of noise and background speech while the user is talking.
  <br>Default <code>false</code> while in beta.
  </p>
   */
  var backgroundDenoisingEnabled: Boolean?

  /**
  This is the background sound in the call. Default for phone calls is 'office' and default for web calls is 'off'.
   */
  var backgroundSound: BackgroundSoundType

  /**
  <p>This is the message that the assistant will say if it ends the call.
  <br>If unspecified, it will hang up without saying anything.
  </p>
   */
  var endCallMessage: String

  // TODO Needs docs
  val endCallPhrases: MutableSet<String>
  val metadata: MutableMap<String, String>
  val transportConfigurations: MutableList<TransportConfigurationDto>

  // TODO: Not in the docs
  var dialKeypadFunctionEnabled: Boolean?
  var endCallFunctionEnabled: Boolean?
  var forwardingPhoneNumber: String

  /**
  <p>This is the first message that the assistant will say. This can also be a URL to a containerized audio file (mp3, wav, etc.).
  <br>If unspecified, assistant will wait for user to speak and use the model to respond once they speak.
  </p>
   */
  var firstMessage: String

  /**
  <p>This is the mode for the first message. Default is 'assistant-speaks-first'.
  Use:
  <li>'assistant-speaks-first' to have the assistant speak first.
  <li>'assistant-waits-for-user' to have the assistant wait for the user to speak first.
  <li>'assistant-speaks-first-with-model-generated-message' to have the assistant speak first with a message generated by the model based on the conversation state. (<code>assistant.model.messages</code> at call start, <code>call.messages</code> at squad transfer points).
  <br>@default 'assistant-speaks-first'
  </p>
   */
  var firstMessageMode: FirstMessageModeType

  /**
  When this is enabled, no logs, recordings, or transcriptions will be stored.
  At the end of the call, you will still receive an end-of-call-report message
  to store on your server. Defaults to false.
   */
  var hipaaEnabled: Boolean?

  /**
  <p>The minimum number of seconds to wait after transcription (with punctuation) before sending a request to the model. Defaults to 0.1.
  <br>@default 0.1
  </p>
   */
  var llmRequestDelaySeconds: Double

  /**
  <p>The minimum number of seconds to wait after transcription (without punctuation) before sending a request to the model. Defaults to 1.5.
  <br>@default 1.5
  </p>
   */
  var llmRequestNonPunctuatedDelaySeconds: Double

  /**
  <p>This is the maximum number of seconds that the call will last. When the call reaches this duration, it will be ended.
  <br>@default 1800 (~30 minutes)
  </p>
   */
  var maxDurationSeconds: Int

  /**
  <p>This determines whether the model's output is used in conversation history rather than the transcription of assistant's speech.
  <br>Default <code>false</code> while in beta.
  </p>
   */
  var modelOutputInMessagesEnabled: Boolean?

  /**
  <p>This is the name of the assistant.
  <br>This is required when you want to transfer between assistants in a call.
  </p>
   */
  var name: String

  /**
  <p>The number of words to wait for before interrupting the assistant.
  <br>Words like "stop", "actually", "no", etc. will always interrupt immediately regardless of this value.
  <br>Words like "okay", "yeah", "right" will never interrupt.
  <br>When set to 0, it will rely solely on the VAD (Voice Activity Detector) and will not wait for any transcription. Defaults to this (0).
  <br>@default 0
  </p>
   */
  var numWordsToInterruptAssistant: Int

  /**
  This sets whether the assistant's calls are recorded. Defaults to true.
   */
  var recordingEnabled: Boolean?

  /**
  <p>The minimum number of seconds after user speech to wait before the assistant starts speaking. Defaults to 0.4.
  <br>@default 0.4
  </p>
   */
  var responseDelaySeconds: Double

  /**
  <p>This is the URL Vapi will communicate with via HTTP GET and POST Requests. This is used for retrieving context, function calling, and end-of-call reports.
  <br>All requests will be sent with the call object among other things relevant to that message. You can find more details in the Server URL documentation.
  <br>This overrides the serverUrl set on the org and the phoneNumber. Order of precedence: tool.server.url > assistant.serverUrl > phoneNumber.serverUrl > org.serverUrl
  </p>
   */
  var serverUrl: String

  /**
  <p>This is the secret you can set that Vapi will send with every request to your server. Will be sent as a header called x-vapi-secret.
  <br>Same precedence logic as serverUrl.
  </p>
   */
  var serverUrlSecret: String

  /**
  How many seconds of silence to wait before ending the call. Defaults to 30.
   */
  var silenceTimeoutSeconds: Int

  /**
  <p>This is the message that the assistant will say if the call is forwarded to voicemail.
  <br>If unspecified, it will hang up.
  </p>
   */
  var voicemailMessage: String

  /**
  These are the messages that will be sent to your Client SDKs.
  Default is CONVERSATION_UPDATE, FUNCTION_CALL, HANG, MODEL_OUTPUT, SPEECH_UPDATE,
  STATUS_UPDATE, TRANSCRIPT, TOOL_CALLS, USER_INTERRUPTED, and VOICE_INPUT. You can check the shape of the messages in
  ClientMessage schema.
   */
  val clientMessages: MutableSet<AssistantClientMessageType>

  /**
  These are the messages that will be sent to your Server URL. Default is CONVERSATION_UPDATE, END_OF_CALL_REPORT,
  FUNCTION_CALL, HANG, SPEECH_UPDATE, STATUS_UPDATE, TOOL_CALLS, TRANSFER_DESTINATION_REQUEST, USER_INTERRUPTED.
  You can check the shape of the messages in ServerMessage schema.
   */
  val serverMessages: MutableSet<AssistantServerMessageType>
}
