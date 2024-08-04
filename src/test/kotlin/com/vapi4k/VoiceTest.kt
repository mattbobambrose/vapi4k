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

package com.vapi4k

import com.vapi4k.AssistantTest.Companion.REQUEST_CONTEXT
import com.vapi4k.dsl.assistant.AssistantDsl.squad
import com.vapi4k.dsl.model.enums.GroqModelType
import com.vapi4k.dsl.voice.enums.CartesiaVoiceLanguageType
import com.vapi4k.dsl.voice.enums.CartesiaVoiceModelType
import com.vapi4k.dsl.voice.enums.PlayHTVoiceEmotionType
import com.vapi4k.dsl.voice.enums.PlayHTVoiceIdType
import com.vapi4k.utils.get
import com.vapi4k.utils.stringValue
import com.vapi4k.utils.toJsonElement
import kotlinx.serialization.json.jsonArray
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertEquals

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

class VoiceTest {
  @Test
  fun `playHt voice basic test`() {
    val squad = squad(REQUEST_CONTEXT) {
      members {
        member {
          assistant {
            name = "Receptionist"
            firstMessage = "Hi there!"

            groqModel {
              modelType = GroqModelType.MIXTRAL_8X7B
            }

            playHTVoice {
              voiceIdType = PlayHTVoiceIdType.MATT
              emotion = PlayHTVoiceEmotionType.MALE_SAD
            }
          }
        }
      }
    }
    val jsonElement = squad.toJsonElement()
    val members = jsonElement["squad.members"].jsonArray.toList()
    assertEquals(1, members.size)
    assertEquals("Receptionist", members[0]["assistant.name"].stringValue)
    assertEquals("Hi there!", members[0]["assistant.firstMessage"].stringValue)
    assertEquals("mixtral-8x7b-32768", members[0]["assistant.model.model"].stringValue)
    assertEquals("groq", members[0]["assistant.model.provider"].stringValue)
    assertEquals("matt", members[0]["assistant.voice.voiceId"].stringValue)
    assertEquals("male_sad", members[0]["assistant.voice.emotion"].stringValue)
  }

  @Test
  fun `playHt voice two or no voiceId error test`() {
    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              playHTVoice {
                voiceIdType = PlayHTVoiceIdType.MATT
                emotion = PlayHTVoiceEmotionType.MALE_SAD
                customVoiceId = "jeff"
              }
            }
          }
        }
      }
    }.also {
      assertEquals("playHTVoice{} cannot have both voiceIdType and customVoiceId values", it.message)
    }

    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              playHTVoice {
                emotion = PlayHTVoiceEmotionType.MALE_SAD
              }
            }
          }
        }
      }
    }.also {
      assertEquals("playHTVoice{} requires a voiceIdType or customVoiceId value", it.message)
    }
  }

  @Test
  fun `cartesia voice two or no models error test`() {
    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              cartesiaVoice {
                voiceId = "matt"
                modelType = CartesiaVoiceModelType.SONIC_ENGLISH
                customModel = "specialModel"
              }
            }
          }
        }
      }
    }.also {
      assertEquals("cartesiaVoice{} cannot have both modelType and customModel values", it.message)
    }

    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              cartesiaVoice {
                voiceId = "matt"
              }
            }
          }
        }
      }
    }.also {
      assertEquals("cartesiaVoice{} requires a modelType or customModel value", it.message)
    }
  }

  @Test
  fun `cartesia voice two languages error test`() {
    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              cartesiaVoice {
                voiceId = "matt"
                modelType = CartesiaVoiceModelType.SONIC_ENGLISH
                languageType = CartesiaVoiceLanguageType.FRENCH
                customLanguage = "specialLanguage"
              }
            }
          }
        }
      }
    }.also {
      assertEquals("cartesiaVoice{} cannot have both languageType and customLanguage values", it.message)
    }
  }

  @Test
  fun `cartesia voice double model error test`() {
    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              cartesiaVoice {
                voiceId = "matt"
                modelType = CartesiaVoiceModelType.SONIC_ENGLISH
                customModel = "specialModel"
              }
            }
          }
        }
      }
    }.also {
      assertEquals("groqModel{} already called", it.message)
    }
  }

  @Test
  fun `cartesia voice double voice error test`() {
    assertThrows(IllegalStateException::class.java) {
      squad(REQUEST_CONTEXT) {
        members {
          member {
            assistant {
              name = "Receptionist"
              firstMessage = "Hi there!"

              groqModel {
                modelType = GroqModelType.MIXTRAL_8X7B
              }

              cartesiaVoice {
                voiceId = "matt"
                modelType = CartesiaVoiceModelType.SONIC_ENGLISH
              }

              cartesiaVoice {
                voiceId = "matt"
                modelType = CartesiaVoiceModelType.SONIC_ENGLISH
              }
            }
          }
        }
      }
    }.also {
      assertEquals("cartesiaVoice{} already called", it.message)
    }
  }

  @Test
  fun `double values test`() {
    val squad = squad(REQUEST_CONTEXT) {
      members {
        member {
          assistant {
            name = "Receptionist 1"
            name = "Receptionist"
            firstMessage = "Hi there!"
            firstMessage = "Hello!"

            groqModel {
              modelType = GroqModelType.MIXTRAL_8X7B
              modelType = GroqModelType.LLAMA3_8B
            }

            playHTVoice {
              voiceIdType = PlayHTVoiceIdType.MATT
              voiceIdType = PlayHTVoiceIdType.JACK
              emotion = PlayHTVoiceEmotionType.MALE_SAD
              emotion = PlayHTVoiceEmotionType.MALE_ANGRY
              temperature = 5.0
              temperature = 10.0
            }
          }
        }
      }
    }
    val jsonElement = squad.toJsonElement()
    val members = jsonElement["squad.members"].jsonArray.toList()
    assertEquals("Hello!", members[0]["assistant.firstMessage"].stringValue)
    assertEquals("llama3-8b-8192", members[0]["assistant.model.model"].stringValue)
    assertEquals("jack", members[0]["assistant.voice.voiceId"].stringValue)
    assertEquals("male_angry", members[0]["assistant.voice.emotion"].stringValue)
    assertEquals("10.0", members[0]["assistant.voice.temperature"].get().stringValue)
  }
}
