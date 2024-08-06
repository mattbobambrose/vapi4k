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

import com.vapi4k.dsl.functions.FunctionDetails.FunctionDetailsDto
import com.vapi4k.dsl.functions.FunctionDetails.FunctionDetailsDto.Companion.toFunctionDetailsDto
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.DurationUnit

class FunctionInfo internal constructor() {
  val created: Instant = Clock.System.now()
  val functions = mutableMapOf<String, FunctionDetails>()

  val age get() = Clock.System.now() - created
  val ageSecs get() = age.toString(unit = DurationUnit.SECONDS)

  fun hasFunction(funcName: String) = functions.contains(funcName)

  fun getFunction(funcName: String) = functions[funcName] ?: error("Function not found: \"$funcName\"")

  override fun toString() = "FunctionInfo(age=$age, functions=$functions)"
}

@Serializable
class FunctionInfoDto(
  val created: String = "",
  val age: String = "",
  val functions: Map<String, FunctionDetailsDto>? = null,
) {
  companion object {
    fun FunctionInfo.toFunctionInfoDto() =
      FunctionInfoDto(
        created.toString(),
        age.toString(),
        functions.mapValues { it.value.toFunctionDetailsDto() },
      )
  }
}
