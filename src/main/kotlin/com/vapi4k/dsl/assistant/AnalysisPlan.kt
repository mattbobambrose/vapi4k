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

package com.vapi4k.dsl.assistant

import com.vapi4k.dsl.assistant.enums.StructureDataSchemeType
import com.vapi4k.dsl.assistant.enums.SuccessEvaluationRubricType
import com.vapi4k.dtos.AnalysisPlanDto
import com.vapi4k.dtos.StructuredDataSchemaDto

interface AnalysisPlanProperties {
  var summaryPrompt: String
  var summaryRequestTimeoutSeconds: Double
  var structuredDataRequestTimeoutSeconds: Double
  var successEvaluationPrompt: String
  var successEvaluationRubric: SuccessEvaluationRubricType
  var successEvaluationRequestTimeoutSeconds: Double
  var structuredDataPrompt: String
  val structuredDataSchema: StructuredDataSchemaDto
}

@AssistantDslMarker
data class AnalysisPlan internal constructor(
  internal val dto: AnalysisPlanDto,
) : AnalysisPlanProperties by dto {
  fun structuredDataSchema(block: StructuredDataSchema.() -> Unit) {
    StructuredDataSchema(dto.structuredDataSchema).apply(block)
  }
}

interface StructuredDataSchemaProperties {
  var type: StructureDataSchemeType
  var description: String
  val items: MutableMap<String, String>
  val properties: MutableMap<String, String>
  val required: MutableList<String>
}

@AssistantDslMarker
data class StructuredDataSchema internal constructor(
  internal val dto: StructuredDataSchemaDto,
) : StructuredDataSchemaProperties by dto
