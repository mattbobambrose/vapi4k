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

package com.vapi4k.api.vapi4k

import com.vapi4k.dsl.vapi4k.ToolServerProperties
import com.vapi4k.dsl.vapi4k.Vapi4KDslMarker
import com.vapi4k.dtos.vapi4k.ToolServerDto
import com.vapi4k.utils.Utils.ensureStartsWith
import java.net.URI

@Vapi4KDslMarker
class ToolServer internal constructor(private val dto: ToolServerDto) : ToolServerProperties by dto {
  internal val path get() = URI(serverUrl).toURL().path.ensureStartsWith("/")
}
