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

package com.vapi4k.dbms

import com.vapi4k.utils.common.Utils.obfuscate
import com.vapi4k.utils.envvar.EnvVar

object DbmsEnvVars {
  val DBMS_DRIVER_CLASSNAME =
    EnvVar("DBMS_DRIVER_CLASSNAME", { System.getenv(name) ?: "com.impossibl.postgres.jdbc.PGDriver" })
  val DBMS_URL = EnvVar("DBMS_URL", { System.getenv(name) ?: "jdbc:pgsql://localhost:5432/postgres" })
  val DBMS_USERNAME = EnvVar("DBMS_USERNAME", { System.getenv(name) ?: "postgres" })
  val DBMS_PASSWORD = EnvVar("DBMS_PASSWORD", { System.getenv(name) ?: "docker" }, { it.obfuscate(1) })
  val DBMS_MAX_POOL_SIZE = EnvVar("DBMS_MAX_POOL_SIZE", { System.getenv(name) ?: "10" })
  val DBMS_MAX_LIFETIME_MINS = EnvVar("DBMS_MAX_LIFETIME_MINS", { System.getenv(name) ?: "30" })
}
