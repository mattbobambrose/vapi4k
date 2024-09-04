package com.vapi4k.docs

import com.vapi4k.api.assistant.InboundCallAssistantResponse
import com.vapi4k.api.model.enums.OpenAIModelType
import com.vapi4k.api.tools.RequestContext

object Squad {
  fun InboundCallAssistantResponse.getSquad(requestContext: RequestContext) =
    squad {
      name = "Squad Name-31"
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
