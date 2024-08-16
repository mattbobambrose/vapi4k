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

package com.vapi4k

import com.vapi4k.api.conditions.eq
import com.vapi4k.api.tools.enums.ToolMessageRoleType
import com.vapi4k.dsl.assistant.Param
import com.vapi4k.dsl.assistant.ToolCall
import com.vapi4k.dsl.toolservice.ToolCallService
import kotlinx.serialization.json.JsonElement

class WeatherLookupService0 {
  @ToolCall("Look up the favorite food in Chicago")
  fun getFavFoodInChicago() = "Pizza"
}

class WeatherLookupService1 : ToolCallService() {
  @ToolCall("Look up the weather for a city")
  fun getWeatherByCity(
    @Param(description = "The city name") city: String,
    state: String,
  ): String {
    return "The weather in city $city and state $state is windy"
  }

  override fun onToolCallComplete(
    toolCallRequest: JsonElement,
    result: String,
  ) = requestCompleteMessages {
    condition("city" eq "Chicago", "state" eq "Illinois") {
      requestCompleteMessage {
        role = ToolMessageRoleType.ASSISTANT
        content = "Tool call request complete with condition"
      }
    }
    requestCompleteMessage {
      role = ToolMessageRoleType.SYSTEM
      content = "Tool call request complete no condition"
    }
  }

  override fun onToolCallFailed(
    toolCallRequest: JsonElement,
    errorMessage: String,
  ) = requestFailedMessages {
    condition("city" eq "Houston", "state" eq "Texas") {
      requestFailedMessage {
        content = "The weather in Chicago is always sunny"
      }
    }
    requestFailedMessage {
      content = "Tool call request failed"
    }
  }
}

class WeatherLookupService2 {
  @ToolCall
  fun lookupWeatherByZipCode(zipCode: String) = "The weather in zip code $zipCode is sunny"
}

class WeatherLookupService3 {
  @ToolCall("Look up the weather for an area code", "weatherByAreaCode")
  fun lookupWeatherByAreaCode(areaCode: String) = "The weather in area code $areaCode is windy"
}

class IntBooleanStringService {
  @ToolCall
  fun intBooleanString(
    int: Int,
    boolean: Boolean,
    string: String,
  ) = "Int: $int, Boolean: $boolean, String: $string"
}
