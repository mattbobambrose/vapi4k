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

import com.vapi4k.api.functions.Functions
import com.vapi4k.api.tools.enums.ToolType
import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId
import com.vapi4k.dsl.model.AbstractModelProperties
import com.vapi4k.dsl.vapi4k.Vapi4kApplicationImpl
import com.vapi4k.dtos.functions.FunctionDto
import com.vapi4k.utils.ReflectionUtils.toolCallFunction
import kotlin.reflect.KFunction

data class FunctionsImpl internal constructor(
  internal val model: AbstractModelProperties,
) : Functions {
  override fun function(
    obj: Any,
    vararg functions: KFunction<*>,
  ) {
    val application = model.application as Vapi4kApplicationImpl
    if (functions.isEmpty()) {
      FunctionUtils.verifyObjectHasOnlyOneToolCall(obj)
      val function = obj.toolCallFunction
      FunctionUtils.verifyIsValidReturnType(false, function)
      addFunction(application, obj, function)
    } else {
      functions.forEach { function ->
        FunctionUtils.verifyIsToolCall(false, function)
        FunctionUtils.verifyIsValidReturnType(false, function)
        addFunction(application, obj, function)
      }
    }
  }

  private fun addFunction(
    application: Vapi4kApplicationImpl,
    obj: Any,
    function: KFunction<*>,
  ) {
    model.functionDtos +=
      FunctionDto().also { functionDto ->
        FunctionUtils.populateFunctionDto(ToolType.FUNCTION, model, obj, function, functionDto)
        val sessionCacheId =
          if (model.sessionCacheId.isNotSpecified())
            model.sessionCacheId
          else
            model.messageCallId.toSessionCacheId()
        application.toolCallCache.addToCache(sessionCacheId, model.assistantCacheId, ToolType.FUNCTION, obj, function)
      }.also { func ->
        if (model.functionDtos.any { func.name == it.name }) {
          error("Duplicate function name declared: ${func.name}")
        }
      }
  }
}
