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

package com.vapi4k.responses

import com.vapi4k.Vapi4k.logger
import com.vapi4k.dsl.assistant.Functions.Companion.functionCache
import com.vapi4k.responses.ResponseUtils.deriveNames
import com.vapi4k.responses.ResponseUtils.invokeMethod
import com.vapi4k.utils.JsonElementUtils.phoneNumber
import com.vapi4k.utils.Utils.functionName
import com.vapi4k.utils.Utils.functionParameters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class FunctionResponse(var result: String = "") {
  companion object {
    fun getFunctionCallResponse(request: JsonElement) =
      FunctionResponse()
        .also { response ->
          val funcName = request.functionName
          val args = request.functionParameters
          val (className, methodName) = deriveNames(funcName)
          val serviceInstance =
            runCatching {
              with(Class.forName(className)) {
                kotlin.objectInstance ?: functionCache.get(request.phoneNumber)?.get(className)
                ?: error("No object instance found for $className")
//                this.constructors.toList().first().newInstance()
              }
            }

          response.result =
            if (serviceInstance.isSuccess) {
              val service = serviceInstance.getOrThrow()
              val toolResult = runCatching { invokeMethod(service, methodName, args) }
              toolResult.getOrElse {
                logger.error(toolResult.exceptionOrNull()) { "Error invoking method $className.$methodName" }
                "Error calling function"
              }
            } else {
              logger.error(serviceInstance.exceptionOrNull()) { "Error creating instance of $className" }
              "Error creating instance"
            }
        }

  }
}
