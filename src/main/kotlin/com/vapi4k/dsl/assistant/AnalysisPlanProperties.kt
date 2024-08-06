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

import com.vapi4k.api.assistant.enums.SuccessEvaluationRubricType

interface AnalysisPlanProperties {
  /**
  <p>This is the prompt that's used to extract structured data from the call. The output is stored in `call.analysis.structuredData`.
  <br>Disabled by default.
  <br>You can use this standalone or in combination with `structuredDataSchema`. If both are provided, they are concatenated into appropriate instructions.</p>
   */
  var structuredDataPrompt: String

  /**
  <p>This is how long the request is tried before giving up. When request times out, `call.analysis.structuredData` will be empty. Increasing this timeout will delay the end of call report.
  <br>Default is 5 seconds.</p>
   */
  var structuredDataRequestTimeoutSeconds: Double

  /**
  <p>This is the prompt that's used to evaluate if the call was successful. The output is stored in `call.analysis.successEvaluation`.
  <br>Default is "You are an expert call evaluator. You will be given a transcript of a call and the system prompt of the AI participant.
  Determine if the call was successful based on the objectives inferred from the system prompt. DO NOT return anything except the result.".
  <br>Set to '' or 'off' to disable.
  <br>You can use this standalone or in combination with `successEvaluationRubric`. If both are provided, they areconcatenated into appropriate instructions.
  </p>
   */
  var successEvaluationPrompt: String

  /**
  <p>This is how long the request is tried before giving up. When request times out, call.analysis.successEvaluation will be empty. Increasing this timeout will delay the end of call report.
  <br>Default is 5 seconds.</p>
   */
  var successEvaluationRequestTimeoutSeconds: Double
  var successEvaluationRubric: SuccessEvaluationRubricType
  var summaryPrompt: String
  var summaryRequestTimeoutSeconds: Double
}
