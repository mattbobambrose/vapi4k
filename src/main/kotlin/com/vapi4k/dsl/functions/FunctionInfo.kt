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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.DurationUnit

class FunctionInfo {
  val created: Instant = Clock.System.now()
  val functions = mutableMapOf<String, FunctionDetails>()

  val age get() = Clock.System.now() - created
  val ageSecs get() = age.toString(unit = DurationUnit.SECONDS)
  val ageMillis get() = age.toString(unit = DurationUnit.MILLISECONDS)

  fun getFunction(funcName: String) = functions[funcName] ?: error("Function not found: \"$funcName\"")
}

@Serializable
class FunctionInfoDto(
  @Transient
  val functionInfo: FunctionInfo? = null,
) {
  @EncodeDefault
  val created = functionInfo!!.created.toString()

  @EncodeDefault
  val age = functionInfo!!.age.toString()

  @EncodeDefault
  val functions = functionInfo!!.functions.mapValues { FunctionDetailsDto(it.value) }
}
