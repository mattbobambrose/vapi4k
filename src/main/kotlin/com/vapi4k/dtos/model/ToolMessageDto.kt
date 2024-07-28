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

package com.vapi4k.dtos.model


import com.vapi4k.dsl.tools.enums.ToolMessageRoleType
import com.vapi4k.dsl.tools.enums.ToolMessageType
import com.vapi4k.dsl.tools.toolMessages.ToolMessageCompleteProperties
import com.vapi4k.dsl.tools.toolMessages.ToolMessageDelayedProperties
import com.vapi4k.dsl.tools.toolMessages.ToolMessageFailedProperties
import com.vapi4k.dsl.tools.toolMessages.ToolMessageStartProperties
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
abstract class AbstractToolMessageDto(
  @EncodeDefault
  val type: ToolMessageType,
  var content: String = "",
  val conditions: MutableSet<ToolMessageConditionDto> = mutableSetOf()
) {
  fun verifyValues() {
    if (content.isEmpty()) error("Content is required for ToolMessage")
  }
}

@Serializable
class ToolMessageStartDto : AbstractToolMessageDto(ToolMessageType.REQUEST_START), ToolMessageStartProperties

@Serializable
data class ToolMessageCompleteDto(
  override var role: ToolMessageRoleType = ToolMessageRoleType.UNSPECIFIED,
  override var endCallAfterSpokenEnabled: Boolean? = null,
) : AbstractToolMessageDto(ToolMessageType.REQUEST_COMPLETE), ToolMessageCompleteProperties

@Serializable
data class ToolMessageFailedDto(override var endCallAfterSpokenEnabled: Boolean? = null) :
  AbstractToolMessageDto(ToolMessageType.REQUEST_FAILED), ToolMessageFailedProperties

@Serializable
data class ToolMessageDelayedDto(override var timingMilliseconds: Int = -1) :
  AbstractToolMessageDto(ToolMessageType.REQUEST_RESPONSE_DELAYED), ToolMessageDelayedProperties

@Serializable
data class ToolMessageDto(
  var type: String = "",
  var content: String = "",
  var timingMilliseconds: Int = -1,
  val conditions: MutableSet<ToolMessageConditionDto> = mutableSetOf(),
)
