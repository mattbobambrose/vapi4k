package com.github.mattbobambrose.vapi4k.com.vapi4k.docs

import com.vapi4k.api.assistant.AssistantResponse
import com.vapi4k.api.model.enums.OpenAIModelType
import kotlinx.serialization.json.JsonElement

object Squad {
  fun AssistantResponse.getSquad(request: JsonElement) =
    squad {
      name = "Squad Name3"
      members {
        member {
          assistantId {
            id = "name1"
          }
          assistant {
            name = "Assistant Name"
            openAIModel {
              modelType = OpenAIModelType.GPT_4_TURBO
            }
          }
        }
        member {
          assistant {
            name = "Assistant Name"
            openAIModel {
              modelType = OpenAIModelType.GPT_4_TURBO
            }
          }
        }
      }
    }
}
