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

package com.vapi4k.responses.assistant

import com.vapi4k.common.CacheId
import com.vapi4k.common.DuplicateChecker
import com.vapi4k.dsl.assistant.model.AnyscaleModel
import com.vapi4k.dsl.assistant.model.AnyscaleModelImpl
import com.vapi4k.dsl.assistant.transcriber.DeepgramTranscriber
import com.vapi4k.dsl.assistant.transcriber.GladiaTranscriber
import com.vapi4k.dsl.assistant.transcriber.TalkscriberTranscriber
import com.vapi4k.responses.assistant.model.AnyscaleModelDto
import com.vapi4k.responses.assistant.model.CommonModelDto
import com.vapi4k.responses.assistant.transcriber.CommonTranscriberDto
import com.vapi4k.responses.assistant.transcriber.DeepgramTranscriberDto
import com.vapi4k.responses.assistant.transcriber.GladiaTranscriberDto
import com.vapi4k.responses.assistant.transcriber.TalkscriberTranscriberDto
import com.vapi4k.responses.assistant.voice.CommonVoiceDto
import kotlinx.serialization.json.JsonElement

internal object AssistantUtils {
  interface AssistantBridge {
    var transcriberDto: CommonTranscriberDto?
    var modelDto: CommonModelDto?
    var voiceDto: CommonVoiceDto?
  }

  // Transcribers
  fun deepgramTranscriber(
    dto: AssistantBridge,
    transcriberChecker: DuplicateChecker,
    block: DeepgramTranscriber.() -> Unit,
  ): DeepgramTranscriber {
    transcriberChecker.check("deepGramTranscriber{} already called")
    val transcriberDto = DeepgramTranscriberDto().also { dto.transcriberDto = it }
    return DeepgramTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  fun gladiaTranscriber(
    dto: AssistantBridge,
    transcriberChecker: DuplicateChecker,
    block: GladiaTranscriber.() -> Unit,
  ): GladiaTranscriber {
    transcriberChecker.check("gladiaTranscriber{} already called")
    val transcriberDto = GladiaTranscriberDto().also { dto.transcriberDto = it }
    return GladiaTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  fun talkscriberTranscriber(
    dto: AssistantBridge,
    transcriberChecker: DuplicateChecker,
    block: TalkscriberTranscriber.() -> Unit,
  ): TalkscriberTranscriber {
    transcriberChecker.check("talkscriberTranscriber{} already called")
    val transcriberDto = TalkscriberTranscriberDto().also { dto.transcriberDto = it }
    return TalkscriberTranscriber(transcriberDto)
      .apply(block)
      .apply { transcriberDto.verifyValues() }
  }

  // Models
  fun anyscaleModel(
    request: JsonElement,
    cacheId: CacheId,
    dto: AssistantBridge,
    modelChecker: DuplicateChecker,
    block: AnyscaleModel.() -> Unit,
  ): AnyscaleModelImpl {
    modelChecker.check("anyscaleModel{} already called")
    val modelDto = AnyscaleModelDto().also { dto.modelDto = it }
    return AnyscaleModelImpl(request, cacheId, modelDto)
      .apply(block)
      .apply { modelDto.verifyValues() }
  }
}
