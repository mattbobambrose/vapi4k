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

@Serializable
@JvmInline
value class ApplicationId private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    fun String.toApplicationId() = ApplicationId(this)
  }
}

@Serializable
@JvmInline
value class SessionId private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    val EMPTY_SESSION_ID = "".toSessionId()

    fun String.toSessionId() = SessionId(this)
  }
}

@Serializable
@JvmInline
value class AssistantId private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    val EMPTY_ASSISTANT_ID = "".toAssistantId()

    fun String.toAssistantId() = AssistantId(this)
  }
}

@Serializable
@JvmInline
value class CacheKey private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    fun String.toCacheKey() = CacheKey(this)

    fun cacheKeyValue(
      sessionId: SessionId,
      assistantId: AssistantId,
    ) = "${sessionId.value}--${assistantId.value}".toCacheKey()
  }
}

@Serializable
@JvmInline
value class FunctionName private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    // Force all emails to be lowercase
    fun String.toFunctionName() = FunctionName(this)
  }
}

@Serializable
@JvmInline
value class ApplicationName private constructor(
  val value: String,
) {
  override fun toString() = value

  companion object {
    // Force all emails to be lowercase
    fun String.toApplicationName() = ApplicationName(this)
  }
}
