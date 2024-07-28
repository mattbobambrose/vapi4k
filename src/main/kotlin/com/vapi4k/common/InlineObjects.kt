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

import kotlinx.serialization.Serializable

@JvmInline
value class SessionCacheId private constructor(
  val value: String,
) {
  fun isNotSpecified() = value == UNSPECIFIED_VALUE

  override fun toString() = value

  companion object {
    const val UNSPECIFIED_VALUE = "_unspecified_"
    val UNSPECIFIED_SESSION_CACHE_ID = SessionCacheId(UNSPECIFIED_VALUE)

    fun String.toSessionCacheId() = SessionCacheId(this)

    fun MessageCallId.toSessionCacheId() = SessionCacheId(this.value)
  }
}

@JvmInline
value class AssistantCacheId private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    fun String.toAssistantCacheId() = AssistantCacheId(this)
  }
}

@JvmInline
value class MessageCallId private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    fun String.toMessageCallId() = MessageCallId(this)
  }
}

@Serializable
@JvmInline
value class Email private constructor(
  val value: String,
) {
  fun isNotBlank() = value.isNotBlank()

  fun isBlank() = value.isBlank()

  override fun toString() = value

  companion object {
    val EMPTY_EMAIL = "".toEmail()

    // Force all emails to be lowercase
    fun String.toEmail() = Email(this.lowercase().trim())
  }
}
