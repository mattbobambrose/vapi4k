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

package com.vapi4k.dsl.call

import com.vapi4k.api.call.OutboundCall
import com.vapi4k.api.call.Phone
import com.vapi4k.common.CoreEnvVars.vapi4kBaseUrl
import com.vapi4k.common.CoreEnvVars.vapiBaseUrl
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.dsl.call.VapiApiImpl.Companion.configCall
import com.vapi4k.dsl.vapi4k.ApplicationType.OUTBOUND_CALL
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.MiscUtils.removeEnds
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.json.JsonElementUtils.keys
import com.vapi4k.utils.json.JsonElementUtils.toJsonElement
import com.vapi4k.utils.json.get
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject

object ProcessOutboundCall {
  internal fun processOutboundCall(
    authString: String,
    block: Phone.() -> OutboundCall,
  ): HttpResponse =
    runBlocking {
      val phone = Phone()
      val outboundCall =
        phone.runCatching(block)
          .onFailure { e -> logger.error { "Failed to create outbound call: ${e.errorMsg}" } }
          .getOrThrow()

      val assistantResponse =
        runCatching {
          val url = "$vapi4kBaseUrl/${OUTBOUND_CALL.pathPrefix}/${outboundCall.serverPath.removeEnds("/")}"
          if (outboundCall.method.isPost()) {
            httpClient.post(url) {
              contentType(Application.Json)
              addVapiSecret(outboundCall)
              setBody(outboundCall.postArgs)
            }
          } else {
            httpClient.get(url) {
              contentType(Application.Json)
              addVapiSecret(outboundCall)
            }
          }
        }.onFailure { e -> logger.error { "Failed to fetch assistant from vapi4k server: ${e.errorMsg}" } }
          .getOrThrow()

      val assistantJson = assistantResponse.bodyAsText().toJsonElement()

      val outboundDto = (outboundCall as OutboundCallImpl).outboundCallRequestDto
      val outboundJson = outboundDto.toJsonElement()

      val requestJson =
        buildJsonObject {
          outboundJson.keys.forEach { key -> put(key, outboundJson[key]) }
          assistantJson.keys.forEach { key -> put(key, assistantJson[key]) }
        }

      val vapiResponse =
        runCatching {
          httpClient.post("$vapiBaseUrl/call/phone") {
            configCall(authString)
            setBody(requestJson)
          }
        }.onFailure { e -> logger.error { "Failed calling $vapiBaseUrl: ${e.errorMsg}" } }
          .getOrThrow()

      if (vapiResponse.status.value != HttpStatusCode.Created.value) {
        val msg = "Failed calling $vapiBaseUrl: ${vapiResponse.status} - ${vapiResponse.bodyAsText()}"
        logger.error { msg }
        error(msg)
      }

      // swapSessionCacheIds(vapiResponse, phone)

      vapiResponse
    }

//  private suspend fun swapSessionCacheIds(
//    vapiResponse: HttpResponse,
//    phone: Phone,
//  ) {
//    val jsonElement = vapiResponse.bodyAsText().toJsonElement()
//    if (jsonElement.containsKey("id")) {
//      logger.info { "Swapping call ID: ${jsonElement.id}" }
//
//      val swapObj = SessionCacheIdSwap(phone.sessionCacheId, jsonElement.id.toSessionCacheId())
//
//      val swapUrl = "$vapi4kBaseUrl/${SWAP_CACHE_IDS.removeEnds("/")}"
//      val swapResponse =
//        httpClient.post(swapUrl) {
//          contentType(Application.Json)
//          setBody(swapObj)
//        }
//
//      if (swapResponse.status.value != HttpStatusCode.OK.value) {
//        val msg = "Failed swapping cache IDs: ${swapResponse.status} - ${swapResponse.bodyAsText()}"
//        logger.error { msg }
//        error(msg)
//      }
//    } else {
//      logger.error { "No call ID found in outbound call response: ${jsonElement.toJsonString()}" }
//    }
//  }

  private fun HttpRequestBuilder.addVapiSecret(call: OutboundCall) {
    if (call.serverSecret.isNotBlank()) {
      headers.append(VAPI_SECRET_HEADER, call.serverSecret)
    }
  }
}
