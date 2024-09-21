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

package assistants

import com.vapi4k.api.reponse.InboundCallAssistantResponse

object Assistants {
  fun InboundCallAssistantResponse.assistantIdExample() {
    assistantId {
      id = "41ba80bc-807c-4cf5-a8c3-0a88a5a5882g"

      assistantOverrides {
        // Declare the assistant overrides here
      }
    }
  }

  fun InboundCallAssistantResponse.squadIdExample() {
    squadId {
      id = "51ba90bc-807c-4cf5-a8c4-1a88a5a5882h"
    }
  }
}
