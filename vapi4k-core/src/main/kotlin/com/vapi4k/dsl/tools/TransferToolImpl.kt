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

package com.vapi4k.dsl.tools

import com.vapi4k.api.destination.AssistantDestination
import com.vapi4k.api.destination.NumberDestination
import com.vapi4k.api.destination.SipDestination
import com.vapi4k.api.tools.TransferDestinationResponse
import com.vapi4k.api.tools.TransferTool
import com.vapi4k.api.vapi4k.TransferDestinationRequestContext
import com.vapi4k.dsl.destination.AssistantDestinationImpl
import com.vapi4k.dsl.destination.NumberDestinationImpl
import com.vapi4k.dsl.destination.SipDestinationImpl
import com.vapi4k.dtos.api.destination.AssistantDestinationDto
import com.vapi4k.dtos.api.destination.CommonDestinationDto
import com.vapi4k.dtos.api.destination.NumberDestinationDto
import com.vapi4k.dtos.api.destination.SipDestinationDto
import com.vapi4k.dtos.tools.ToolDto
import com.vapi4k.responses.AssistantRequestResponseDto

interface TransferToolProperties {
  val destinations: MutableList<CommonDestinationDto>
}

class TransferToolImpl internal constructor(
  callerName: String,
  val dto: ToolDto,
) : ToolWithServerImpl(callerName, dto),
  TransferToolProperties by dto,
  TransferTool,
  TransferDestinationResponse {
  override lateinit var transferDestinationRequest: TransferDestinationRequestContext

  override fun assistantDestination(block: AssistantDestination.() -> Unit) {
    AssistantRequestResponseDto().apply {
      val assistantDto = AssistantDestinationDto().also { destinations += it }
      AssistantDestinationImpl(assistantDto).apply(block)
      if (assistantDto.assistantName.isEmpty()) {
        error("assistantDestination{} requires an assistantName value")
      }
    }
  }

  override fun numberDestination(block: NumberDestination.() -> Unit) {
    AssistantRequestResponseDto().apply {
      val numDto = NumberDestinationDto().also { destinations += it }
      NumberDestinationImpl(numDto).apply(block)
    }
  }

  override fun sipDestination(block: SipDestination.() -> Unit) {
    AssistantRequestResponseDto().apply {
      val sipDto = SipDestinationDto().also { destinations += it }
      SipDestinationImpl(sipDto).apply(block)
    }
  }
}
