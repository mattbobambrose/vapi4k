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

import com.vapi4k.common.Serializers
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.TranscriberDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

interface AssistantOverridesUnion {
  var firstMessageMode: String
  var recordingEnabled: Boolean
  var hipaaEnabled: Boolean
  var silenceTimeoutSeconds: Int
  var responseDelaySeconds: Double
  var llmRequestDelaySeconds: Double
  var llmRequestNonPunctuatedDelaySeconds: Double
  var numWordsToInterruptAssistant: Int
  var maxDurationSeconds: Int
  var backgroundSound: String
  var backchannelingEnabled: Boolean
  var backgroundDenoisingEnabled: Boolean
  var modelOutputInMessagesEnabled: Boolean
  var name: String
  var firstMessage: String
  var voicemailMessage: String
  var endCallMessage: String
  var serverUrl: String
  var serverUrlSecret: String
  val clientMessages: MutableSet<String>
  val serverMessages: MutableSet<String>
}

@AssistantDslMarker
data class AssistantOverrides internal constructor(
  internal val request: JsonElement,
  internal val overridesDto: AssistantOverridesDto,
) : AssistantOverridesUnion by overridesDto {

  fun model(block: Model.() -> Unit) {
    Model(request, overridesDto.modelDto).apply(block)
  }

  fun voice(block: VoiceOverrides.() -> Unit) {
    VoiceOverrides(overridesDto.voiceDto).apply(block)
  }

  fun deepGramTranscriber(block: DeepgramTranscriber.() -> Unit) {
    DeepgramTranscriber(overridesDto.transcriberDto).apply(block)
  }
}

enum class TranscriberType(val desc: String) {
  DEEPGRAM("deepgram"),
  GLADIA("gladia"),
  TALK_SCRIBER("talkscriber");
}

// See: https://developers.deepgram.com/docs/models-languages-overview
@Serializable(with = Serializers.DeepgramModelTypeSerializer::class)
enum class DeepgramModelType(val desc: String) {
  NOVA_2("nova-2"),
  NOVA_2_GENERAL("nova-2-general"),
  NOVA_2_MEETING("nova-2-meeting"),
  NOVA_2_PHONECALL("nova-2-phonecall"),
  NOVA_2_FINANCE("nova-2-finance"),
  NOVA_2_CONVERSATIONAL_AI("nova-2-conversationalai"),
  NOVA_2_VOICEMAIL("nova-2-voicemail"),
  NOVA_2_VIDEO("nova-2-video"),
  NOVA_2_MEDICAL("nova-2-medical"),
  NOVA_2_DRIVETHRU("nova-2-drivethru"),
  NOVA_2_AUTOMOTIVE("nova-2-automotive"),

  NOVA("nova"),
  NOVA_GENERAL("nova-general"),
  NOVA_PHONECALL("nova-phonecall"),
  NOVA_MEDICAL("nova-medical"),

  ENHANCED("enhanced"),
  ENHANCED_GENERAL("enhanced-general"),
  ENHANCED_MEETING("enhanced-meeting"),
  ENHANCED_PHONECALL("enhanced-phonecall"),
  ENHANCED_FINANCE("enhanced-finance"),

  BASE("base"),
  BASE_GENERAL("base-general"),
  BASE_MEETING("base-meeting"),
  BASE_PHONECALL("base-phonecall"),
  BASE_FINANCE("base-finance"),
  BASE_CONVERSATIONAL_AI("base-conversationalai"),
  BASE_VOICEMAIL("base-voicemail"),
  BASE_VIDEO("base-video");
}

// See: https://developers.deepgram.com/docs/models-languages-overview
@Serializable(with = Serializers.DeepgramLanguageTypeSerializer::class)
enum class DeepgramLanguageType(val desc: String) {
  BULGARIAN("bg"),
  CATALAN("ca"),
  CHINESE_MANDARIN_SIMPLIFIED("zh"),
  CHINESE_MANDARIN_SIMPLIFIED_CN("zh-CN"),
  CHINESE_MANDARIN_SIMPLIFIED_HAN("zh-Hans"),
  CHINESE_MANDARIN_TRADITIONAL_TW("zh-TW"),
  CHINESE_MANDARIN_TRADITIONAL_HAN("zh-Hant"),
  CZECH("cs"),
  DANISH("da"),
  DANISH_DK("da-DK"),
  DUTCH("nl"),
  ENGLISH("en"),
  ENGLISH_AU("en-AU"),
  ENGLISH_GB("en-GB"),
  ENGLISH_IN("en-IN"),
  ENGLISH_NZ("en-NZ"),
  ENGLISH_US("en-US"),
  ESTONIAN("et"),
  FINNISH("fi"),
  FLEMISH("nl-BE"),
  FRENCH("fr"),
  FRENCH_CA("fr-CA"),
  GERMAN("de"),
  GERMAN_SWISS("de-CH"),
  GREEK("el"),
  HINDI("hi"),
  HUNGARIAN("hu"),
  INDONESIAN("id"),
  ITALIAN("it"),
  JAPANESE("ja"),
  KOREAN("ko"),
  KOREAN_KR("ko-KR"),
  LATVIAN("lv"),
  LITHUANIAN("lt"),
  MALAY("ms"),
  MULTILINGUAL_SPANISH_ENGLISH("multi"), // Missing from vapi docs
  NORWEGIAN("no"),
  POLISH("pl"),
  PORTUGUESE("pt"),
  PORTUGUESE_BR("pt-BR"),
  ROMANIAN("ro"),
  RUSSIAN("ru"),
  SLOVAK("sk"),
  SPANISH("es"),
  SPANISH_419("es-419"),
  SWEDISH("sv"),
  SWEDISH_SE("sv-SE"),
  THAI("th"),
  THAI_TH("th-TH"),
  TURKISH("tr"),
  UKRAINIAN("uk"),
  VIETNAMESE("vi"),

  // Work with enhanced models
  TAMASHEQ("taq"),
  TAMIL("ta"),

  // Work with nova models
  HINDI_LATN("hi-Latn"),

  // Work with enhanced and base models
  SPANISH_LATAM("es-LATAM");
}

interface DeepgramTranscriberUnion {
  var smartFormat: Boolean
  val keywords: MutableList<String>
}

class DeepgramTranscriber internal constructor(val transcriberDto: TranscriberDto) :
  DeepgramTranscriberUnion by transcriberDto {

  init {
    transcriberDto.provider = TranscriberType.DEEPGRAM.desc
  }

  var model: DeepgramModelType
    get() = DeepgramModelType.values().firstOrNull() { it.desc == transcriberDto.transcriberModel }
      ?: error("Unknown Deepgram model: ${transcriberDto.transcriberModel}")
    set(value) {
      transcriberDto.transcriberModel = value.desc
    }

  var language: DeepgramLanguageType
    get() = DeepgramLanguageType.values().firstOrNull() { it.desc == transcriberDto.transcriberModel }
      ?: error("Unknown Deepgram language: ${transcriberDto.transcriberLanguage}")
    set(value) {
      transcriberDto.transcriberLanguage = value.desc
    }
}
