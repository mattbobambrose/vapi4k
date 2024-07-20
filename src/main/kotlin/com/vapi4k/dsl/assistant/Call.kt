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

import com.vapi4k.responses.CallRequest
import com.vapi4k.responses.CallUnion
import com.vapi4k.utils.JsonElementUtils

@AssistantDslMarker
class Call internal constructor(val callRequest: CallRequest) : CallUnion by callRequest {
  private var errorMsg = ""

  private fun checkIfDeclared(newStr: String) = if (errorMsg.isNotEmpty()) error(errorMsg) else errorMsg = newStr

  fun assistantId(block: AssistantId.() -> Unit) {
    checkIfDeclared("assistantId{} already called")
    AssistantId(JsonElementUtils.emptyJsonElement(), callRequest).apply(block)
  }

  fun assistant(block: Assistant.() -> Unit) {
    checkIfDeclared("assistant{} already called")
    with(callRequest) {
      Assistant(JsonElementUtils.emptyJsonElement(), assistantDto, assistantOverridesDto).apply(block)
    }
  }

  fun squadId(block: SquadId.() -> Unit) {
    checkIfDeclared("squadId{} already called")
    SquadId(JsonElementUtils.emptyJsonElement(), callRequest).apply(block)
  }


  fun squad(block: Squad.() -> Unit) {
    checkIfDeclared("squad{} already called")
    with(callRequest) {
      Squad(JsonElementUtils.emptyJsonElement(), squadDto).apply(block)
    }
  }


  fun customer(block: Customer.() -> Unit) {
    Customer(callRequest.customerDto).apply(block)
  }
}
