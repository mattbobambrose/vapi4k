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
import com.vapi4k.common.Constants.SESSION_ID
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.JsonUtils
import com.vapi4k.utils.MiscUtils.addQueryParam
import kotlinx.serialization.json.JsonElement

class TalkButtonProperties(
  override var serverPath: String = "",
  override var serverSecret: String = "",
  override var vapiPublicApiKey: String = "",
  override var sessionId: String = "",
  override var method: MethodType = MethodType.POST,
  override var postArgs: JsonElement = JsonUtils.EMPTY_JSON_ELEMENT,
) : TalkButton {
  fun verifyTalkButtonValues() {
    require(serverPath.isNotBlank()) { "serverPath must be assigned in talkButton{}" }
    require(vapiPublicApiKey.isNotBlank()) { "vapiPublicApiKey must be assigned in talkButton{}" }

    if (sessionId.isBlank()) {
      sessionId = "Web-${getRandomSecret(8, 4, 4, 12)}"
    }
    serverPath = serverPath.addQueryParam(SESSION_ID, sessionId)
  }
}
