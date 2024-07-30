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

package com.vapi4k.dsl.functions

import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.functions.FunctionUtils.populateFunctionDto
import com.vapi4k.dsl.functions.FunctionUtils.verifyObject
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dsl.tools.ToolCache.Companion.functionCache
import com.vapi4k.dtos.functions.FunctionDto

@AssistantDslMarker
interface Functions {
  fun function(obj: Any)
}

data class FunctionsImpl internal constructor(
  internal val model: AbstractModelProperties,
) : Functions {
  override fun function(obj: Any) {
    model.functionDtos += FunctionDto().also { functionDto ->
      verifyObject(true, obj)
      populateFunctionDto(model, obj, functionDto)
      val sessionCacheId =
        if (model.sessionCacheId.isNotSpecified())
          model.sessionCacheId
        else
          model.messageCallId.toSessionCacheId()
      functionCache.addToCache(sessionCacheId, model.assistantCacheId, obj)
    }.also { func ->
      if (model.functionDtos.any { func.name == it.name }) {
        error("Duplicate function name declared: ${func.name}")
      }
    }
  }
}
