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

package com.vapi4k.responses.assistant.voice

import com.vapi4k.common.Constants.UNSPECIFIED_DEFAULT
import com.vapi4k.dsl.assistant.enums.PunctuationType
import com.vapi4k.dsl.assistant.enums.VoiceProviderType
import com.vapi4k.dsl.assistant.voice.RimeAIVoiceUnion
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class RimeAIVoiceDto(
  @EncodeDefault
  val provider: VoiceProviderType = VoiceProviderType.RIMEAI,

  override var inputPreprocessingEnabled: Boolean? = null,
  override var inputReformattingEnabled: Boolean? = null,
  override var inputMinCharacters: Int = -1,
  override var inputPunctuationBoundaries: MutableList<PunctuationType> = mutableListOf(),
  override var fillerInjectionEnabled: Boolean? = null,
  override var voiceId: RimeAIVoiceId = RimeAIVoiceId.UNSPECIFIED,
  override var model: RimeAIVoiceModel = RimeAIVoiceModel.UNSPECIFIED,
  override var speed: Double = 0.0,
) : RimeAIVoiceUnion, AbstractVoiceDto()

@Serializable(with = RimeAIVoiceIdSerializer::class)
enum class RimeAIVoiceId(val desc: String) {
  MARSH("marsh"),
  BAYOU("bayou"),
  CREEK("creek"),
  BROOK("brook"),
  FLOWER("flower"),
  SPORE("spore"),
  GLACIER("glacier"),
  GULCH("gulch"),
  ALPINE("alpine"),
  COVE("cove"),
  LAGOON("lagoon"),
  TUNDRA("tundra"),
  STEPPE("steppe"),
  MESA("mesa"),
  GROVE("grove"),
  RAINFOREST("rainforest"),
  MORAINE("moraine"),
  WILDFLOWER("wildflower"),
  PEAK("peak"),
  BOULDER("boulder"),
  ABBIE("abbie"),
  ALLISON("allison"),
  ALLY("ally"),
  ALONA("alona"),
  AMBER("amber"),
  ANA("ana"),
  ANTOINE("antoine"),
  ARMON("armon"),
  BRENDA("brenda"),
  BRITTANY("brittany"),
  CAROL("carol"),
  COLIN("colin"),
  COURTNEY("courtney"),
  ELENA("elena"),
  ELLIOT("elliot"),
  EVA("eva"),
  GEOFF("geoff"),
  GERALD("gerald"),
  HANK("hank"),
  HELEN("helen"),
  HERA("hera"),
  JEN("jen"),
  JOE("joe"),
  JOY("joy"),
  JUAN("juan"),
  KENDRA("kendra"),
  KENDRICK("kendrick"),
  KENNETH("kenneth"),
  KEVIN("kevin"),
  KRIS("kris"),
  LINDA("linda"),
  MADISON("madison"),
  MARGE("marge"),
  MARINA("marina"),
  MARISSA("marissa"),
  MARTA("marta"),
  MAYA("maya"),
  NICHOLAS("nicholas"),
  NYLES("nyles"),
  PHIL("phil"),
  REBA("reba"),
  REX("rex"),
  RICK("rick"),
  RITU("ritu"),
  ROB("rob"),
  RODNEY("rodney"),
  ROHAN("rohan"),
  ROSCO("rosco"),
  SAMANTHA("samantha"),
  SANDY("sandy"),
  SELENA("selena"),
  SETH("seth"),
  SHARON("sharon"),
  STAN("stan"),
  TAMRA("tamra"),
  TANYA("tanya"),
  TIBUR("tibur"),
  TJ("tj"),
  TYLER("tyler"),
  VIV("viv"),
  YADIRA("yadira"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object RimeAIVoiceIdSerializer : KSerializer<RimeAIVoiceId> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: RimeAIVoiceId,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = RimeAIVoiceId.entries.first { it.desc == decoder.decodeString() }
}

@Serializable(with = RimeAIVoiceModelSerializer::class)
enum class RimeAIVoiceModel(val desc: String) {
  V1("v1"),
  MIST("mist"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT),
}

private object RimeAIVoiceModelSerializer : KSerializer<RimeAIVoiceModel> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProviderType", STRING)

  override fun serialize(
    encoder: Encoder,
    value: RimeAIVoiceModel,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = RimeAIVoiceModel.entries.first { it.desc == decoder.decodeString() }
}
