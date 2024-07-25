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

package com.vapi4k.dtos.assistant


import kotlinx.serialization.Serializable

@Serializable
data class AnalysisPlanDto(
  var summaryPrompt: String = "",
  var summaryRequestTimeoutSeconds: Double = -1.0,
  var structuredDataRequestTimeoutSeconds: Double = -1.0,
  var successEvaluationPrompt: String = "",
  var successEvaluationRubric: String = "",
  var successEvaluationRequestTimeoutSeconds: Double = -1.0,
  var structuredDataPrompt: String = "",
  val structuredDataSchema: com.vapi4k.dtos.assistant.StructuredDataSchemaDto = com.vapi4k.dtos.assistant.StructuredDataSchemaDto(),
)
