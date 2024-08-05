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

package com.vapi4k.dtos.vapi4k

import com.vapi4k.dsl.vapi4k.ToolServerProperties
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolServerDto(
  override var name: String = "",

  @SerialName("url")
  override var serverUrl: String = "",

  @SerialName("secret")
  override var serverSecret: String = "",
  override var timeoutSeconds: Int = -1,
) : ToolServerProperties
