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

package com.vapi4k.api.destination

import com.vapi4k.api.destination.enums.AssistantTransferMode
import com.vapi4k.dsl.assistant.AssistantDslMarker

/**
This is the assistant destination you'd like the call to be transferred to.
 */
@AssistantDslMarker
interface AssistantDestination {
  /**
  This is the assistant to transfer the call to.
   */
  var assistantName: String

  /**
  <p>This is the mode to use for the transfer. Default is <code>rolling-history</code>.
  <blockquote><li><code>rolling-history</code>: This is the default mode. It keeps the entire conversation history and appends the new assistant's system message on transfer.
  Example:
  Pre-transfer: system: assistant1 system message assistant: assistant1 first message user: hey, good morning assistant: how can i help? user: i need help with my account assistant: (destination.message)
  Post-transfer: system: assistant1 system message assistant: assistant1 first message user: hey, good morning assistant: how can i help? user: i need help with my account assistant: (destination.message) system: assistant2 system message assistant: assistant2 first message (or model generated if firstMessageMode is set to assistant-speaks-first-with-model-generated-message)
  <li><code>swap-system-message-in-history</code>: This replaces the original system message with the new assistant's system message on transfer.
  Example:
  Pre-transfer: system: assistant1 system message assistant: assistant1 first message user: hey, good morning assistant: how can i help? user: i need help with my account assistant: (destination.message)
  Post-transfer: system: assistant2 system message assistant: assistant1 first message user: hey, good morning assistant: how can i help? user: i need help with my account assistant: (destination.message) assistant: assistant2 first message (or model generated if firstMessageMode is set to <code>assistant-speaks-first-with-model-generated-message</code>)
  </blockquote>
  </p>
   */
  var transferMode: AssistantTransferMode

  /**
  <p>This is the message to say before transferring the call to the destination.
  <br>If this is not provided and transfer tool messages is not provided, default is "Transferring the call now".
  <br>If set to "", nothing is spoken. This is useful when you want to silently transfer. This is especially useful when transferring between assistants in a squad. In this scenario, you likely also want to set <code>assistant.firstMessageMode=assistant-speaks-first-with-model-generated-message</code> for the destination assistant.
  </p>
   */
  var message: String

  /**
  This is the description of the destination, used by the AI to choose when and how to transfer the call.
   */
  var description: String
}
