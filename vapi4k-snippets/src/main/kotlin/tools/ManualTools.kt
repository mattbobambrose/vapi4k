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

package tools

import com.vapi4k.dsl.model.CommonModelProperties
import com.vapi4k.utils.api.json.JsonElementUtils.stringValue
import kotlinx.serialization.json.JsonElement

object ManualTools {
  fun CommonModelProperties.manualToolExample() {
    tools {
      manualTool {
        name = "manualWeatherLookup"
        description = "Look up the weather for a city and state"

        parameters {
          parameter {
            name = "city"
            description = "The city to look up"
          }
          parameter {
            name = "state"
            description = "The state to look up"
          }
        }

        requestStartMessage {
          content = "This is the manual weather lookup start message"
        }

        onInvoke { args: JsonElement ->
          val city = args.stringValue("city")
          val state = args.stringValue("state")

          result = "The weather in $city, $state is ${listOf("sunny", "cloudy", "rainy").random()}"

          requestCompleteMessages {
            requestCompleteMessage {
              content = "This is the manual weather lookup complete message"
            }
          }

          requestFailedMessages {
            requestFailedMessage {
              content = "This is the manual weather lookup failed message"
            }
          }
        }
      }
    }
  }
}
