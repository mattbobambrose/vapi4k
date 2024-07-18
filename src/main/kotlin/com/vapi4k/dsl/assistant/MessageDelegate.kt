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

import com.vapi4k.dsl.assistant.enums.MessageRoleType
import com.vapi4k.responses.assistant.RoleMessage
import kotlin.reflect.KProperty

internal interface ModelDelegate {
  val messages: MutableList<RoleMessage>

  fun message(role: MessageRoleType, content: String)
}

internal class MessageDelegate(val messageRoleType: MessageRoleType) {
  operator fun getValue(
    model: ModelDelegate,
    property: KProperty<*>,
  ): String {
    val msgs = model.messages.filter { it.role == messageRoleType.desc }
    return if (msgs.isEmpty()) "" else (msgs.joinToString("") { it.content })
  }

  operator fun setValue(
    model: ModelDelegate,
    property: KProperty<*>,
    newVal: String,
  ) = model.message(messageRoleType, newVal)
}
