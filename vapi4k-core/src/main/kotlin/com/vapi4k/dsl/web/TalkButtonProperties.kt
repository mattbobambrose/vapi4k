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

package com.vapi4k.dsl.web

import com.vapi4k.api.web.MethodType
import com.vapi4k.api.web.TalkButton
import com.vapi4k.utils.JsonElementUtils
import kotlinx.serialization.json.JsonElement

class TalkButtonProperties(
  override var vapi4kUrl: String = "",
  override var vapiApiKey: String = "",
  override var serverSecret: String = "",
  override var method: MethodType = MethodType.POST,
  override var postArgs: JsonElement = JsonElementUtils.EMPTY_JSON_ELEMENT,
) : TalkButton {
  fun verifyTalkButtonValues() {
    require(vapi4kUrl.isNotBlank()) { "vapi4kUrl must not be blank in talkButton{}" }
    require(vapiApiKey.isNotBlank()) { "vapiApiKey must not be blank in talkButton{}" }
  }
}


