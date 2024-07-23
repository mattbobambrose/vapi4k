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


@JvmInline
value class CacheId private constructor(val value: String) {

  fun isValid() = value.isNotEmpty()
  fun isNotValid() = value.isEmpty()

  override fun toString() = value

  companion object {
    val EMPTY_CACHE_ID = CacheId("")

    fun String.toCacheId(): CacheId = CacheId(this)
  }
}
