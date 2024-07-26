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

package com.vapi4k.dsl.assistant.enums

import com.vapi4k.common.Constants.UNSPECIFIED_DEFAULT

enum class VoicemailDetectionType(val desc: String) {
  MACHINE_START("machine_start"),
  HUMAN("human"),
  FAX("fax"),
  UNKNOWN("unknown"),
  MACHINE_END_BEEP("machine_end_beep"),
  MACHINE_END_SILENCE("machine_end_silence"),
  MACHINE_END_OTHER("machine_end_other"),
  UNSPECIFIED(UNSPECIFIED_DEFAULT)
}
