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

import com.vapi4k.Vapi4k.logger
import com.vapi4k.dbms.Messages.insertRequest
import com.vapi4k.dbms.Messages.insertResponse
import com.vapi4k.dsl.vapi4k.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.dsl.vapi4k.ServerRequestType.FUNCTION_CALL
import com.vapi4k.dsl.vapi4k.ServerRequestType.STATUS_UPDATE
import com.vapi4k.dsl.vapi4k.ServerRequestType.TOOL_CALL
import com.vapi4k.dsl.vapi4k.configureKtor
import com.vapi4k.plugin.Vapi4k
import com.vapi4k.utils.DslUtils.logObject
import com.vapi4k.utils.DslUtils.printObject
import com.vapi4k.utils.JsonElementUtils.requestType
import io.ktor.server.application.Application
import io.ktor.server.application.install

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


fun Application.module() {
  configureKtor()
  //connectToDbms()

  install(Vapi4k) {
    val BASE_URL = "https://eocare-app-fiqm5.ondigitalocean.app"

    configure {
      serverUrl = "$BASE_URL/inboundRequest"
      serverUrlSecret = "12345"
    }

    toolCallEndpoints {
      endpoint {
        name = "endpoint1"
        url = "$BASE_URL/toolCall"
        secret = "456"
        timeoutSeconds = 20
      }

      endpoint {
        name = "endpoint2"
        url = "$BASE_URL/TC2"
        secret = "456"
        timeoutSeconds = 20
      }
    }

    onAssistantRequest { assistantRequest ->
      myAssistantRequest(assistantRequest)
    }

    onAllRequests { request ->
      logger.info { "All requests: ${request.requestType}" }
      insertRequest(request)
      logObject(request)
      printObject(request)
    }

    onRequest(TOOL_CALL) { request ->
      logger.info { "Tool call: $request" }
    }

    onRequest(FUNCTION_CALL) { request ->
      logger.info { "Function call: $request" }
    }

    onRequest(STATUS_UPDATE) { request ->
      logger.info { "Status update: STATUS_UPDATE" }
    }

    onRequest(STATUS_UPDATE) { request ->
//      if (request.hasStatusUpdateError) {
//        logger.info { "Status update error: ${request.statusUpdateError}" }
//      }
    }

    onAllResponses { requestType, response, elapsedTime ->
//      logger.info { "All responses: $response" }
//      logObject(response)
//      printObject(response)
      insertResponse(requestType, response, elapsedTime)
    }

    onResponse(ASSISTANT_REQUEST) { requestType, response, elapsed ->
//      logger.info { "Response: $response" }
    }
  }
}
