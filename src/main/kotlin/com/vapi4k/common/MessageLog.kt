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

package com.vapi4k.common

import com.vapi4k.dbms.Messages
import com.vapi4k.dsl.vapi4k.ServerRequestType
import com.vapi4k.plugin.RequestResponseType.REQUEST
import com.vapi4k.plugin.RequestResponseType.RESPONSE
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

object MessageLog {
  fun insertRequest(type: ServerRequestType, json: JsonElement) {
    transaction {
      Messages.insert { rec ->
        val str = Json.encodeToString(json)
        rec[messageType] = REQUEST.name
        rec[requestType] = type.desc
        rec[messageJsonb] = str
        rec[messageJson] = str
        rec[elapsedTime] = Duration.ZERO
      }
    }
  }

  fun insertResponse(type: ServerRequestType, json: JsonElement, elapsed: Duration) {
    transaction {
      Messages.insert { rec ->
        val str = Json.encodeToString(json)
        rec[messageType] = RESPONSE.name
        rec[requestType] = type.desc
        rec[messageJsonb] = str
        rec[messageJson] = str
        rec[elapsedTime] = elapsed
      }
    }
  }
}
