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

import com.vapi4k.api.destination.NumberDestination
import com.vapi4k.api.destination.SipDestination
import com.vapi4k.api.squad.Squad
import com.vapi4k.api.squad.SquadId
import com.vapi4k.api.vapi4k.AssistantRequestContext
import com.vapi4k.dsl.assistant.AssistantDslMarker

@AssistantDslMarker
interface AssistantResponse {
  val assistantRequestContext: AssistantRequestContext

  fun assistant(block: Assistant.() -> Unit)

  fun assistantId(block: AssistantId.() -> Unit)

  fun squad(block: Squad.() -> Unit)

  fun squadId(block: SquadId.() -> Unit)

  fun numberDestination(block: NumberDestination.() -> Unit)

  fun sipDestination(block: SipDestination.() -> Unit)
}

