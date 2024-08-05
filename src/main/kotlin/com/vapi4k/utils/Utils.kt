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
import com.vapi4k.utils.ReflectionUtils.functions
import io.github.oshai.kotlinlogging.KLogger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object Utils {
  internal val Throwable.errorMsg get() = "${this::class.simpleName} - $message"

  internal fun nextSessionCacheId() = "Outbound-${System.currentTimeMillis()}".toSessionCacheId()

  fun String.ensureStartsWith(s: String) = if (startsWith(s)) this else s + this

  fun String.ensureEndsWith(s: String) = if (endsWith(s)) this else this + s

  fun String.dropLeading(s: String = "/") = if (startsWith(s)) this.drop(s.length) else this

  fun String.dropEnding(s: String = "/") = if (endsWith(s)) this.dropLast(s.length) else this

  fun String.trimLeadingSpaces() = lines().joinToString(separator = "\n") { it.trimStart() }

  fun <T> lambda(block: T) = block

  fun Any.findFunction(methodName: String) =
    functions.singleOrNull { it.name == methodName } ?: error("Method $methodName not found")

  fun resourceFile(filename: String): String =
    this::class.java.getResource(filename)?.readText() ?: error("File not found: $filename")
  // this.javaClass.classLoader.getResource(filename)?.readText() ?: error("File not found: $filename")

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

  internal fun String.obfuscate(freq: Int = 2) = mapIndexed { i, v -> if (i % freq == 0) '*' else v }.joinToString("")

  internal fun Throwable.toErrorString() =
    "${
      stackTraceToString()
        .lines()
        .filterNot { it.trimStart().startsWith("at io.ktor") }
        .filterNot { it.trimStart().startsWith("at kotlin") }
        .joinToString("\n")
    }\t..."

  @OptIn(ExperimentalContracts::class)
  fun Any?.isNotNull(): Boolean {
    contract {
      returns(true) implies (this@isNotNull != null)
    }
    return this != null
  }

  @OptIn(ExperimentalContracts::class)
  fun Any?.isNull(): Boolean {
    contract {
      returns(false) implies (this@isNull != null)
    }
    return this == null
  }

  internal fun getBanner(
    filename: String,
    logger: KLogger,
  ): String {
    val banner = logger.javaClass.classLoader.getResource(filename)?.readText()
      ?: throw IllegalArgumentException("Banner not found: \"$filename\"")

    val lines = banner.lines()

    // Trim initial and trailing blank lines, but preserve blank lines in middle;
    var first = -1
    var last = -1
    var lineNum = 0
    lines.forEach { arg1 ->
      if (arg1.trim { arg2 -> arg2 <= ' ' }.isNotEmpty()) {
        if (first == -1)
          first = lineNum
        last = lineNum
      }
      lineNum++
    }

    lineNum = 0

    val vals =
      lines
        .filter {
          val currLine = lineNum++
          currLine in first..last
        }
        .map { arg -> "     $arg" }

    return "\n\n${vals.joinToString("\n")}\n\n"
  }
}
