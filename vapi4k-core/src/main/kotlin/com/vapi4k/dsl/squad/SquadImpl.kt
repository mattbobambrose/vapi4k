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

package com.vapi4k.dsl.squad

import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.api.squad.Members
import com.vapi4k.api.squad.Squad
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.assistant.AssistantOverridesImpl
import com.vapi4k.dsl.vapi4k.AssistantRequestContext
import com.vapi4k.dtos.squad.SquadDto
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.json.JsonElementUtils.toJsonString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

interface SquadProperties {
  /**
  This is the name of the squad.
   */
  var name: String
}

data class SquadImpl internal constructor(
  internal val assistantRequestContext: AssistantRequestContext,
  internal val sessionCacheId: SessionCacheId,
  internal val assistantCacheIdSource: AssistantCacheIdSource,
  internal val dto: SquadDto,
) : SquadProperties by dto,
  Squad {
  override fun members(block: Members.() -> Unit): Members = MembersImpl(this).apply(block)

  override fun memberOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(assistantRequestContext, sessionCacheId, assistantCacheIdSource, dto.membersOverrides)
      .apply(block)
}

@Serializable(with = ChildSerializer::class)
sealed interface Child

private object ChildSerializer : KSerializer<Child> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Child")

  override fun serialize(
    encoder: Encoder,
    value: Child,
  ) {
    encoder.encodeSerializableValue(DataChild.serializer(), value as DataChild)
  }

  override fun deserialize(decoder: Decoder): Child =
    throw NotImplementedError("Deserialization is not supported")
}

val module = SerializersModule {
  polymorphic(Parent::class) {
    subclass(DataChild::class)
    subclass(NonDataChild::class)
  }
}

@Serializable
abstract class Parent(
  var name: String = "",
)

@Serializable
data class DataChild(
  var street: String = "",
) : Parent()

@Serializable
class NonDataChild(
  var street: String = "",
) : Parent()

@Serializable
class Family(
  val dataChild: DataChild = DataChild(),
  val nonDataChild: NonDataChild = NonDataChild(),
)

val format = Json { serializersModule = module }
inline fun <reified T> T.toJsonElement2() = format.encodeToJsonElement(this)

fun main() {
  val c = Family().apply {
    dataChild.name = "Bill"
    nonDataChild.name = "Bob"
    dataChild.street = "Bill Street"
    nonDataChild.street = "Bob Street"
  }

  println(c.toJsonElement2().toJsonString())

  val d = DataChild().apply {
    name = "Bill"
  }

  println(d.toJsonElement2().toJsonString())

  val e = NonDataChild().apply {
    name = "Bob"
  }

  println(e.toJsonElement2().toJsonString())
}
