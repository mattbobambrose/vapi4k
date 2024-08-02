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

package com.vapi4k.dsl.vapi4k

import com.vapi4k.common.Endpoints.DEFAULT_SERVER_PATH
import com.vapi4k.utils.Utils.ensureStartsWith
import java.net.URI

@Vapi4KDslMarker
interface Vapi4kConfigProperties {
  var serverUrl: String
  var serverUrlSecret: String
  var eocrCacheRemovalEnabled: Boolean
}

class Vapi4kConfigPropertiesImpl internal constructor() : Vapi4kConfigProperties {
  internal val serverUrlPath
    get() = (if (serverUrl.isEmpty()) DEFAULT_SERVER_PATH else URI(serverUrl).toURL().path).ensureStartsWith("/")

  internal val serverUrlPathSegments
    get() = serverUrlPath.split("/").filter { it.isNotEmpty() }

  override var serverUrl = ""
  override var serverUrlSecret = ""
  override var eocrCacheRemovalEnabled = true
}
