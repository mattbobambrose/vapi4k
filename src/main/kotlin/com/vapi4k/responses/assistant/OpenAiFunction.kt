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

package com.vapi4k.responses.assistant


import kotlinx.serialization.Serializable

@Serializable
data class OpenAiFunction(
  var functions: List<Function> = listOf(),
) {
  @Serializable
  data class Function(
    var name: String = "",
    var async: Boolean = false,
    var serverUrl: String = "",
    var parameters: Parameters = Parameters(),
    var description: String = "",
    var serverUrlSecret: String = "",
  ) {
    @Serializable
    data class Parameters(
      var type: String = "",
      var properties: Properties = Properties(),
    ) {
      @Serializable
      data class Properties(
        var fullName: FullName = FullName(),
        var appointmentTime: AppointmentTime = AppointmentTime(),
      ) {
        @Serializable
        data class FullName(
          var type: String = "",
          var description: String = "",
        )

        @Serializable
        data class AppointmentTime(
          var type: String = "",
          var description: String = "",
        )
      }
    }
  }
}
