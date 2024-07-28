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

package com.vapi4k.utils

import com.vapi4k.common.SessionCacheId.Companion.toSessionCacheId

object Utils {
  internal val Throwable.errorMsg get() = "${this::class.simpleName} - $message"

  internal fun nextSessionCacheId() = "Outbound-${System.currentTimeMillis()}".toSessionCacheId()

  fun resourceFile(filename: String): String =
    this::class.java.getResource(filename)?.readText() ?: error("File not found: $filename")
  //this.javaClass.classLoader.getResource(filename)?.readText() ?: error("File not found: $filename")

  internal fun Int.lpad(
    width: Int,
    padChar: Char = '0',
  ): String = toString().padStart(width, padChar)

  internal fun Int.rpad(
    width: Int,
    padChar: Char = '0',
  ): String = toString().padEnd(width, padChar)

  internal fun String.capitalizeFirstChar(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
