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

package com.vapi4k.dsl.tools

import com.vapi4k.api.tools.ExternalTool
import com.vapi4k.dsl.functions.FunctionUtils
import com.vapi4k.dsl.functions.FunctionUtils.llmType
import com.vapi4k.dtos.functions.FunctionPropertyDescDto
import com.vapi4k.dtos.tools.ToolDto
import kotlin.reflect.KClass

open class ExternalToolImpl internal constructor(
  toolDto: ToolDto,
) : ToolWithServerImpl(toolDto),
  ExternalTool {
  override var name
    get() = toolDto.functionDto.name
    set(value) = run { toolDto.functionDto.name = value }

  override var description
    get() = toolDto.functionDto.description
    set(value) = run { toolDto.functionDto.description = value }

  override var async
    get() = toolDto.async
    set(value) = run { toolDto.async = value }

  override fun addParameter(
    name: String,
    description: String,
    type: KClass<*>,
    required: Boolean,
  ) {
    require(name.isNotBlank()) { "externalTool{} parameter name must not be blank" }
    require(type in FunctionUtils.allowedParamTypes) {
      "externalTool{} parameter type must be one of these: String::class, Int::class, Double::class, Boolean::class"
    }

    with(toolDto.functionDto.parametersDto) {
      if (properties.containsKey(name)) error("externalTool{} parameter $name already exists")
      properties[name] = FunctionPropertyDescDto(type.llmType, description)
      if (required) this.required.add(name)
    }
  }
}
