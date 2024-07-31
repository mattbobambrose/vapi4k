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

package com.vapi4k.dsl.voice.enums

import com.vapi4k.common.Constants.UNSPECIFIED_DEFAULT
import kotlinx.serialization.Serializable

@Serializable
enum class DeepGramVoiceIdType(
  val desc: String,
) {
  ANGUS("angus"),
  ARCAS("arcas"),
  ASTERIA("asteria"),
  ATHENA("athena"),
  HELIOS("helios"),
  HERA("hera"),
  LUNA("luna"),
  ORION("orion"),
  ORPHEUS("orpheus"),
  PERSEUS("perseus"),
  STELLA("stella"),
  ZEUS("zeus"),
  UNSPECIFIED(UNSPECIFIED_DEFAULT),
  ;

  fun isSpecified() = this != UNSPECIFIED

  fun isNotSpecified() = this == UNSPECIFIED
}
