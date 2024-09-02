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

import com.vapi4k.api.toolservice.RequestCompleteMessages
import com.vapi4k.api.toolservice.RequestFailedMessages
import com.vapi4k.dsl.assistant.AssistantDslMarker

/**
This is the `Tool` response that is expected from the server to the message.
 */
@AssistantDslMarker
interface DtmfToolResponse {
  /**
  <p>This is the result if the tool call was successful. This is added to the conversation history.
  <br>Further, if this is returned, assistant will speak:
  <ol>
  <li>the <code>message</code>, if it exists and is of type <code>request-complete</code></li>
  <li>a <code>request-complete</code> message from <code>tool.messages</code>, if it exists</li>
  <li>a response generated by the model, if neither exist</li>
  </ol>
  </p>
   */
  var result: String

  /**
  <p>This is the error if the tool call was not successful. This is added to the conversation history.
  <br>Further, if this is returned, assistant will speak:
  <ol>
  <li>the <code>message</code>, if it exists and is of type <code>request-failed</code></li>
  <li>a <code>request-failed</code> message from <code>tool.messages</code>, if it exists</li>
  <li>a response generated by the model, if neither exist</li>
  </ol>
  </p>
   */
  var error: String

  /**
  <p>This message is triggered when the tool call is complete and will be spoken to the user.
  <br>This message is triggered immediately without waiting for your server to respond for async tool calls.
  <br>If this message is not provided, the model will be requested to respond.
  <br>If this message is provided, only this message will be spoken and the model will not be requested to come up with a response. It's an exclusive OR.
  </p>
   */
  fun requestCompleteMessages(block: RequestCompleteMessages.() -> Unit)

  /**
  <p>This message is triggered when the tool call fails and will be spoken to the user.
  <br>This message is never triggered for async tool calls.
  <br>If this message is not provided, the model will be requested to respond.
  <br>If this message is provided, only this message will be spoken and the model will not be requested to come up with a response. It's an exclusive OR.
  </p>
   */
  fun requestFailedMessages(block: RequestFailedMessages.() -> Unit)
}
