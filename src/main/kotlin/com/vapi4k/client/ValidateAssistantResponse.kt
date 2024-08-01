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

package com.vapi4k.client

import com.vapi4k.common.EnvVar.REQUEST_VALIDATION_FILENAME
import com.vapi4k.common.EnvVar.REQUEST_VALIDATION_URL
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.Utils.resourceFile
import com.vapi4k.utils.get
import com.vapi4k.utils.toJsonElement
import com.vapi4k.utils.toJsonString
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object ValidateAssistantResponse {
  fun assistantRequestWitNewCallId(je: JsonElement): JsonElement =
    buildJsonObject {
      putJsonObject("message") {
        put("type", je["message.type"].jsonPrimitive)
        put("phoneNumber", je["message.phoneNumber"].jsonObject)
        putJsonObject("call") {
          put(
            "id",
            "${getRandomSecret(8)}-${getRandomSecret(4)}-${getRandomSecret(4)}-${getRandomSecret(4)}-${
              getRandomSecret(12)
            }"
          )
          put("orgId", je["message.call.orgId"].jsonPrimitive)
          put("createdAt", je["message.call.createdAt"].jsonPrimitive)
          put("updatedAt", je["message.call.updatedAt"].jsonPrimitive)
          put("type", je["message.call.type"].jsonPrimitive)
          put("status", je["message.call.status"].jsonPrimitive)
          put("phoneCallProvider", je["message.call.phoneCallProvider"].jsonPrimitive)
          put("phoneCallProviderId", je["message.call.phoneCallProviderId"].jsonPrimitive)
          put("phoneCallTransport", je["message.call.phoneCallTransport"].jsonPrimitive)
          put("phoneNumberId", je["message.call.phoneNumberId"].jsonPrimitive)
          put("assistantId", je["message.call.assistantId"].jsonPrimitive)
          put("squadId", je["message.call.squadId"].jsonPrimitive)
          put("customer", je["message.call.customer"].jsonObject)
        }
        put("customer", je["message.customer"].jsonObject)
        put("timestamp", je["message.timestamp"].jsonPrimitive)
      }
    }

  fun validateAssistantRequestResponse(
    secret: String,
  ) =
    runBlocking {
      val response = httpClient.post(REQUEST_VALIDATION_URL.value) {
        contentType(Application.Json)
        if (secret.isNotEmpty())
          headers.append("x-vapi-secret", secret)
        val request = runCatching {
          resourceFile(REQUEST_VALIDATION_FILENAME.value)
        }.getOrElse { assistantRequest }

        val newObject = assistantRequestWitNewCallId(request.toJsonElement())
        setBody(newObject)
      }
      buildString {
        append("\nStatus: ${response.status}\n\n")
        val body = response.bodyAsText()
        if (response.status.value == 200) {
          append("Response:\n${body.toJsonElement().toJsonString()}")
        } else {
          if (body.isNotEmpty()) {
            if (body.length < 80)
              append("Error: $body")
            else
              append("Error:\n$body")
          } else {
            append("Check the ktor log for stack trace")
          }
        }
      }
    }

  const val assistantRequest = """
    {
      "message": {
        "type": "assistant-request",
        "call": {
          "id": "305b7217-6d48-433b-bda9-0f00a1731234",
          "orgId": "679a13ec-f40d-4055-8959-797c4ee11234",
          "createdAt": "2024-07-25T06:07:29.604Z",
          "updatedAt": "2024-07-25T06:07:29.604Z",
          "type": "inboundPhoneCall",
          "status": "ringing",
          "phoneCallProvider": "twilio",
          "phoneCallProviderId": "CAef753577823739784a4a250331e4ab5a",
          "phoneCallTransport": "pstn",
          "phoneNumberId": "5a5a04dc-dcbe-45b1-8f64-fd32a253d135",
          "assistantId": null,
          "squadId": null,
          "customer": {
            "number": "+1234567890"
          }
        },
        "phoneNumber": {
          "id": "5a5a04dc-dcbe-45b1-8f64-fd32a253d135",
          "orgId": "679a13ec-f40d-4055-8959-797c4ee1694b",
          "assistantId": null,
          "number": "+1234567890",
          "createdAt": "2024-06-29T03:03:00.576Z",
          "updatedAt": "2024-07-20T04:24:05.533Z",
          "stripeSubscriptionId": "sub_1PWrYyCRkod4mKy33cFxM9B7",
          "twilioAccountSid": null,
          "twilioAuthToken": null,
          "stripeSubscriptionStatus": "active",
          "stripeSubscriptionCurrentPeriodStart": "2024-06-29T03:02:56.000Z",
          "name": null,
          "credentialId": null,
          "serverUrl": null,
          "serverUrlSecret": null,
          "twilioOutgoingCallerId": null,
          "sipUri": null,
          "provider": "twilio",
          "fallbackForwardingPhoneNumber": null,
          "fallbackDestination": null,
          "squadId": null
        },
        "customer": {
          "number": "+19256831234"
        },
        "timestamp": "2024-07-25T06:07:29.733Z"
      }
    }
  """
}
