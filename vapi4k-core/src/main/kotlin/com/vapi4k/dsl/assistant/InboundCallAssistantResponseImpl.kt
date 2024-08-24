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

import com.vapi4k.api.assistant.InboundCallAssistantResponse
import com.vapi4k.api.destination.NumberDestination
import com.vapi4k.api.destination.SipDestination
import com.vapi4k.dsl.destination.NumberDestinationImpl
import com.vapi4k.dsl.destination.SipDestinationImpl
import com.vapi4k.dsl.vapi4k.AssistantRequestContext
import com.vapi4k.dtos.api.destination.NumberDestinationDto
import com.vapi4k.dtos.api.destination.SipDestinationDto

class InboundCallAssistantResponseImpl(
  assistantRequestContext: AssistantRequestContext,
) : AbstractAssistantResponseImpl(assistantRequestContext),
  InboundCallAssistantResponse {
  override var error: String
    get() = assistantRequestResponse.messageResponse.error
    set(value) {
      checker.check("error already declared")
      assistantRequestResponse.messageResponse.error = value
    }

  override fun numberDestination(block: NumberDestination.() -> Unit) {
    checker.check("numberDestination{} already declared")
    assistantRequestResponse.apply {
      val numDto = NumberDestinationDto().also { messageResponse.destination = it }
      NumberDestinationImpl(numDto).apply(block).checkForRequiredFields()
    }
  }

  override fun sipDestination(block: SipDestination.() -> Unit) {
    checker.check("sipDestination{} already declared")
    assistantRequestResponse.apply {
      val sipDto = SipDestinationDto().also { messageResponse.destination = it }
      SipDestinationImpl(sipDto).apply(block).checkForRequiredFields()
    }
  }
}
