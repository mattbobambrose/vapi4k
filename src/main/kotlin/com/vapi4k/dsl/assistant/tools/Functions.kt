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

package com.vapi4k.dsl.assistant.tools

import com.vapi4k.common.SessionId.Companion.toSessionId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.model.ModelMessageProperties
import com.vapi4k.dsl.assistant.tools.FunctionUtils.populateFunctionDto
import com.vapi4k.dsl.assistant.tools.FunctionUtils.verifyObject
import com.vapi4k.dsl.assistant.tools.ToolCache.addFunctionToCache
import com.vapi4k.responses.assistant.FunctionDto

@AssistantDslMarker
interface Functions {
  fun function(obj: Any)
}

data class FunctionsImpl internal constructor(internal val model: ModelMessageProperties) : Functions {
  override fun function(obj: Any) {
    model.functionDtos += FunctionDto().also { functionDto ->
      verifyObject(true, obj)
      populateFunctionDto(obj, functionDto)
      val cacheId =
        if (model.sessionId.isNotSpecified())
          model.sessionId
        else
          model.messageCallId.toSessionId()
      addFunctionToCache(cacheId, obj)
    }.also { func ->
      if (model.functionDtos.any { func.name == it.name }) {
        error("Duplicate function name declared: ${func.name}")
      }
    }
  }
}
