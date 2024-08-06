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

package com.vapi4k.dsl.model

import com.vapi4k.api.model.CustomLLMModel
import com.vapi4k.api.model.enums.MetaDataSendModeType
import com.vapi4k.dtos.model.CustomLLMModelDto

interface CustomLLMModelProperties {
  /**
  This is the name of the model.
   */
  var model: String

  /**
  This determines whether we detect user's emotion while they speak and send it as an additional info to model.
  Default `false` because the model is usually good at understanding the user's emotion from text.
   */
  val emotionRecognitionEnabled: Boolean?

  /**
  This is the max number of tokens that the assistant will be allowed to generate in each turn of the conversation. Default is 250.
   */
  var maxTokens: Int

  /**
  <p>This determines whether metadata is sent in requests to the custom provider.
  <li>`off` will not send any metadata. Payload will look like `{ messages }`</li>
  <li>`variable` will send `assistant.metadata` as a variable on the payload. Payload will look like `{ messages, metadata }`</li>
  <li>`destructured` will send `assistant.metadata` fields directly on the payload. Payload will look like `{ messages, ...metadata }`</li>
  Further, `variable` and `destructured` will send `call`, `phoneNumber`, and `customer` objects in the payload.
  <br>Default is `variable`.</p>
   */
  var metadataSendMode: MetaDataSendModeType

  /**
  This sets how many turns at the start of the conversation to use a smaller, faster model from the same provider
  before switching to the primary model. Example, gpt-3.5-turbo if provider is openai.
  Default is 0.
   */
  var numFastTurns: Int

  /**
  This is the temperature that will be used for calls. Default is 0 to leverage caching for lower latency.
   */
  var temperature: Int

  /**
  These are the tools that the assistant can use during the call. To use transient tools, use `tools`.
  Both `tools` and `toolIds` can be used together.
   */
  val toolIds: MutableSet<String>

  /**
  This is the URL we'll use for the OpenAI client's `baseURL`.
   */
  var url: String
}

class CustomLLMModelImpl(
  modelUnion: ModelUnion,
  modelDto: CustomLLMModelDto,
) : AbstractModel(modelUnion, modelDto),
  CustomLLMModelProperties by modelDto,
  CustomLLMModel
