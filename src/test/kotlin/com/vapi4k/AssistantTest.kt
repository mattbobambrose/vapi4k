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

import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType.ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.dsl.model.enums.DeepgramModelType
import com.vapi4k.dsl.model.enums.GladiaModelType
import com.vapi4k.dsl.model.enums.OpenAIModelType
import com.vapi4k.dsl.tools.ToolCache.Companion.resetCaches
import com.vapi4k.dsl.tools.enums.ToolMessageType
import com.vapi4k.dsl.transcriber.enums.DeepgramLanguageType
import com.vapi4k.dsl.transcriber.enums.TalkscriberModelType
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.dtos.model.ToolMessageConditionDto
import com.vapi4k.utils.JsonElementUtils.assistantClientMessages
import com.vapi4k.utils.JsonElementUtils.assistantServerMessages
import com.vapi4k.utils.JsonFilenames.JSON_ASSISTANT_REQUEST
import com.vapi4k.utils.TestUtils.withTestApplication
import com.vapi4k.utils.containsKey
import com.vapi4k.utils.get
import com.vapi4k.utils.getToJsonElements
import com.vapi4k.utils.intValue
import com.vapi4k.utils.stringValue
import com.vapi4k.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.Test

class AssistantTest {
  init {
    Vapi4kConfig().apply {
      configure {
        serverUrl = "HelloWorld"
        serverUrlSecret = "12345"
      }
    }
  }

  val messageOne = "Hi there test"
  val sysMessage = "You are the test systemMessage voice"
  val startMessage = "This is the test request start message"
  val completeMessage = "This is the test request complete message"
  val failedMessage = "This is the test request failed message"
  val delayedMessage = "This is the test request delayed message"
  val secondStartMessage = "This is the second test request start message"
  val secondCompleteMessage = "This is the second test request complete message"
  val secondFailedMessage = "This is the second test request failed message"
  val secondDelayedMessage = "This is the second test request delayed message"
  val chicagoIllinoisStartMessage = "This is the Chicago Illinois request start message"
  val chicagoIllinoisCompleteMessage = "This is the Chicago Illinois request complete message"
  val chicagoIllinoisFailedMessage = "This is the Chicago Illinois request failed message"
  val chicagoIllinoisDelayedMessage = "This is the Chicago Illinois request delayed message"

  fun JsonElement.tools() = get("assistant.model.tools").getToJsonElements()

  fun JsonElement.firstTool() = tools().first()

  fun JsonElement.firstToolMessages() = firstTool()["messages"].getToJsonElements()

  fun JsonElement.firstMessageOfType(
    type: ToolMessageType,
    vararg conditions: ToolMessageConditionDto,
  ) = if (conditions.isEmpty())
    firstToolMessages().first { it.stringValue("type") == type.desc }
  else
    firstToolMessages()
      .filter { it.containsKey("conditions") }
      .filter {
        conditions.all { c -> it["conditions"].getToJsonElements().contains(c.toJsonElement()) }
      }
      .first { it.stringValue("type") == type.desc }

  @Test
  fun testRegular() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                requestStartMessage {
                  content = startMessage
                }
                requestCompleteMessage {
                  content = completeMessage
                }
                requestFailedMessage {
                  content = failedMessage
                }
                requestDelayedMessage {
                  content = delayedMessage
                  timingMilliseconds = 2000
                }
              }
            }
          }
        }
      }

//    println(jsonElement.toJsonString(true))
//    println("This is the first message: ${jsonElement.firstToolMessages().toJsonString(true)}")
    assertEquals(
      "This is the test request start message",
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START).stringValue("content"),
    )
  }

  @Test
  fun `test reverse delay order`() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                requestStartMessage {
                  content = startMessage
                }
                requestCompleteMessage {
                  content = completeMessage
                }
                requestFailedMessage {
                  content = failedMessage
                }
                requestDelayedMessage {
                  content = delayedMessage
                  timingMilliseconds = 2000
                }
              }
            }
          }
        }
      }
    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(delayedMessage, stringValue("content"))
      assertEquals(2000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `test message with no millis`() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                requestStartMessage {
                  content = startMessage
                }
                requestCompleteMessage {
                  content = completeMessage
                }
                requestFailedMessage {
                  content = failedMessage
                }
                requestDelayedMessage {
                  content = delayedMessage
                  timingMilliseconds = 99
                }
              }
            }
          }
        }
      }
    assertEquals(
      99,
      jsonElement
        .firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)
        .intValue("timingMilliseconds"),
    )
  }

  @Test
  fun `multiple message`() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                requestStartMessage {
                  content = startMessage
                }
                requestCompleteMessage {
                  content = completeMessage
                }
                requestFailedMessage {
                  content = failedMessage
                }
                requestDelayedMessage {
                  content = delayedMessage
                  content = secondDelayedMessage
                  timingMilliseconds = 2000
                }
              }
            }
          }
        }
      }

    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(secondDelayedMessage, stringValue("content"))
      assertEquals(2000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `multiple delay time`() {
    resetCaches()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
      assistant(request) {
        firstMessage = messageOne
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO

          systemMessage = sysMessage
          tools {
            tool(WeatherLookupService0()) {
              requestStartMessage {
                content = startMessage
              }
              requestCompleteMessage {
                content = completeMessage
              }
              requestFailedMessage {
                content = failedMessage
              }
              requestDelayedMessage {
                content = delayedMessage
                timingMilliseconds = 2000
                timingMilliseconds = 1000
              }
            }
          }
        }
      }
    }
    assertEquals(
      1000,
      jsonElement["assistant.model.tools"].getToJsonElements()[0].firstMessageOfType(
        ToolMessageType.REQUEST_RESPONSE_DELAYED,
      )
        .intValue("timingMilliseconds"),
    )
  }

  @Test
  fun `multiple message multiple delay time`() {
    resetCaches()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
      assistant(request) {
        firstMessage = messageOne
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
          systemMessage = sysMessage
          tools {
            tool(WeatherLookupService0()) {
              requestStartMessage {
                content = startMessage
              }
              requestCompleteMessage {
                content = completeMessage
              }
              requestFailedMessage {
                content = failedMessage
              }
              requestDelayedMessage {
                content = delayedMessage
                content = secondDelayedMessage
                timingMilliseconds = 2000
                timingMilliseconds = 1000
              }
            }
          }
        }
      }
    }
    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(secondDelayedMessage, this["content"])
      assertEquals(1000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `chicago illinois message`() {
    resetCaches()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) {
      assistant(it) {
        firstMessage = messageOne
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
          systemMessage = sysMessage
          tools {
            tool(WeatherLookupService0()) {
              condition("city" eq "Chicago", "state" eq "Illinois") {
                requestStartMessage {
                  content = chicagoIllinoisStartMessage
                }
                requestCompleteMessage {
                  content = chicagoIllinoisCompleteMessage
                }
                requestFailedMessage {
                  content = chicagoIllinoisFailedMessage
                }
                requestDelayedMessage {
                  content = chicagoIllinoisDelayedMessage
                  timingMilliseconds = 2000
                }
              }
              requestStartMessage {
                content = startMessage
              }
              requestCompleteMessage {
                content = completeMessage
              }
              requestFailedMessage {
                content = failedMessage
              }
              requestDelayedMessage {
                content = delayedMessage
                timingMilliseconds = 1000
              }
            }
          }
        }
      }
    }

    val chicagoCityConditionDto = ToolMessageConditionDto("city", "eq", "Chicago")
    val illinoisStateConditionDto = ToolMessageConditionDto("state", "eq", "Illinois")

    val chicagoStartMessage =
      jsonElement.firstMessageOfType(
        ToolMessageType.REQUEST_START,
        chicagoCityConditionDto,
        illinoisStateConditionDto,
      )

    val chicagoCompleteMessage =
      jsonElement.firstMessageOfType(
        ToolMessageType.REQUEST_COMPLETE,
        chicagoCityConditionDto,
        illinoisStateConditionDto,
      )

    val chicagoFailedMessage =
      jsonElement.firstMessageOfType(
        ToolMessageType.REQUEST_FAILED,
        chicagoCityConditionDto,
        illinoisStateConditionDto,
      )

    val chicagoDelayedMessage =
      jsonElement.firstMessageOfType(
        ToolMessageType.REQUEST_RESPONSE_DELAYED,
        chicagoCityConditionDto,
        illinoisStateConditionDto,
      )

    val defaultStartMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START)
    val defaultCompleteMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_COMPLETE)
    val defaultFailedMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_FAILED)
    val defaultDelayedMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)

    assertEquals(chicagoIllinoisStartMessage, chicagoStartMessage.stringValue("content"))
    assertEquals(chicagoIllinoisCompleteMessage, chicagoCompleteMessage.stringValue("content"))
    assertEquals(chicagoIllinoisFailedMessage, chicagoFailedMessage.stringValue("content"))
    assertEquals(chicagoIllinoisDelayedMessage, chicagoDelayedMessage.stringValue("content"))
    assertEquals(2000, chicagoDelayedMessage.intValue("timingMilliseconds"))
    assertEquals(startMessage, defaultStartMessage.stringValue("content"))
    assertEquals(completeMessage, defaultCompleteMessage.stringValue("content"))
    assertEquals(failedMessage, defaultFailedMessage.stringValue("content"))
    assertEquals(delayedMessage, defaultDelayedMessage.stringValue("content"))
    assertEquals(1000, defaultDelayedMessage.intValue("timingMilliseconds"))
  }

  @Test
  fun `chicago illinois message reverse conditions 1`() {
    resetCaches()
//    assertThrows(IllegalStateException::class.java) {
    assistant(ASSISTANT_REQUEST.toJsonElement()) {
      firstMessage = messageOne
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            condition("city" eq "Chicago", "state" eq "Illinois") {
//                requestStartMessage {
//                  content = chicagoIllinoisStartMessage
//                }
            }
            condition("state" eq "Illinois", "city" eq "Chicago") {
              requestStartMessage {
                content = chicagoIllinoisStartMessage + "2"
              }
            }
          }
        }
      }
//
//    val chicagoCityDto = ToolMessageConditionDto("city", "eq", "Chicago")
//    val illinoisStateDto = ToolMessageConditionDto("state", "eq", "Illinois")
//
//    val chicagoStartMessage =
//      jsonElement.firstMessageOfType(
//        ToolMessageType.REQUEST_START,
//        chicagoCityDto,
//        illinoisStateDto
//      )
//    val chicagoCompleteMessage =
//      jsonElement.firstMessageOfType(
//        ToolMessageType.REQUEST_COMPLETE,
//        chicagoCityDto,
//        illinoisStateDto
//      )
//    val chicagoFailedMessage =
//      jsonElement.firstMessageOfType(
//        ToolMessageType.REQUEST_FAILED,
//        chicagoCityDto,
//        illinoisStateDto
//      )
//    val chicagoDelayedMessage =
//      jsonElement.firstMessageOfType(
//        ToolMessageType.REQUEST_RESPONSE_DELAYED,
//        chicagoCityDto,
//        illinoisStateDto
//      )
//    val defaultStartMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START)
//    val defaultCompleteMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_COMPLETE)
//    val defaultFailedMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_FAILED)
//    val defaultDelayedMessage = jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)
//
//    assertEquals(chicagoIllinoisStartMessage, chicagoStartMessage.stringValue("content"))
//    assertEquals(chicagoIllinoisCompleteMessage, chicagoCompleteMessage.stringValue("content"))
//    assertEquals(chicagoIllinoisFailedMessage, chicagoFailedMessage.stringValue("content"))
//    assertEquals(chicagoIllinoisDelayedMessage, chicagoDelayedMessage.stringValue("content"))
//    assertEquals(3000, chicagoDelayedMessage.intValue("timingMilliseconds"))
//    assertEquals(chicagoIllinoisStartMessage + "2", defaultStartMessage.stringValue("content"))
//    assertEquals(chicagoIllinoisCompleteMessage + "2", defaultCompleteMessage.stringValue("content"))
//    assertEquals(chicagoIllinoisFailedMessage + "2", defaultFailedMessage.stringValue("content"))
//    assertEquals(chicagoIllinoisDelayedMessage + "2", defaultDelayedMessage.stringValue("content"))
//    assertEquals(1000, defaultDelayedMessage.intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `chicago illinois message reverse conditions 2`() {
    resetCaches()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) { request ->
      assistant(request) {
        firstMessage = messageOne
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO

          systemMessage = sysMessage
          tools {
            tool(WeatherLookupService0()) {
              condition("city" eq "Chicago", "state" eq "Illinois") {
                requestStartMessage {
                  content = chicagoIllinoisStartMessage
                }
                requestCompleteMessage {
                  content = chicagoIllinoisCompleteMessage
                }
                requestFailedMessage {
                  content = chicagoIllinoisFailedMessage
                }
                requestDelayedMessage {
                  content = chicagoIllinoisDelayedMessage
                  timingMilliseconds = 2000
                }
              }
              condition("state" eq "Illinois", "city" eq "Chicago") {
                requestStartMessage {
                  content = chicagoIllinoisStartMessage + "2"
                }
                requestCompleteMessage {
                  content = chicagoIllinoisCompleteMessage + "2"
                }
                requestFailedMessage {
                  content = chicagoIllinoisFailedMessage + "2"
                }
                requestDelayedMessage {
                  content = chicagoIllinoisDelayedMessage + "2"
                  timingMilliseconds = 3000
                }
              }
              requestStartMessage {
                content = startMessage
              }
              requestCompleteMessage {
                content = completeMessage
              }
              requestFailedMessage {
                content = failedMessage
              }
              requestDelayedMessage {
                content = delayedMessage
                timingMilliseconds = 1000
              }
            }
          }
        }
      }
    }
    val chicagoCityDto = ToolMessageConditionDto("city", "eq", "Chicago")
    val illinoisStateDto = ToolMessageConditionDto("state", "eq", "Illinois")

    assertEquals(
      chicagoIllinoisStartMessage,
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START, chicagoCityDto, illinoisStateDto)
        .stringValue("content"),
    )
    assertEquals(
      chicagoIllinoisCompleteMessage,
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_COMPLETE, chicagoCityDto, illinoisStateDto)
        .stringValue("content"),
    )
    assertEquals(
      chicagoIllinoisFailedMessage,
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_FAILED, chicagoCityDto, illinoisStateDto)
        .stringValue("content"),
    )
    assertEquals(
      chicagoIllinoisDelayedMessage,
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED, chicagoCityDto, illinoisStateDto)
        .stringValue("content"),
    )
    assertEquals(
      3000,
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED, chicagoCityDto, illinoisStateDto)
        .intValue("timingMilliseconds"),
    )
  }

  @Test
  fun `check non-default FirstMessageModeType values`() {
    resetCaches()
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        firstMessageMode = ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(
      ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL.desc,
      element.stringValue("assistant.firstMessageMode"),
    )
  }

  @Test
  fun `check assistant client messages 1`() {
    resetCaches()
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        clientMessages -= AssistantClientMessageType.HANG
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(9, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant client messages 2`() {
    resetCaches()
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        clientMessages -= setOf(AssistantClientMessageType.HANG, AssistantClientMessageType.STATUS_UPDATE)
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant server messages 1`() {
    resetCaches()
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        firstMessage = "Something"
        serverMessages -= AssistantServerMessageType.HANG
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantServerMessages.size)
  }

  @Test
  fun `check assistant server messages 2`() {
    resetCaches()
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        serverMessages -= setOf(AssistantServerMessageType.HANG, AssistantServerMessageType.SPEECH_UPDATE)
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(7, element.assistantServerMessages.size)
  }

  @Test
  fun `multiple deepgram transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = ASSISTANT_REQUEST.toJsonElement()
      assistant(request) {
        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
        }

        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
        }
      }
    }
  }

  @Test
  fun `multiple gladia transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = ASSISTANT_REQUEST.toJsonElement()
      assistant(request) {
        gladiaTranscriber {
          transcriberModel = GladiaModelType.FAST
        }

        gladiaTranscriber {
          transcriberModel = GladiaModelType.FAST
        }
      }
    }
  }

  @Test
  fun `multiple talkscriber transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = ASSISTANT_REQUEST.toJsonElement()
      assistant(request) {
        talkscriberTranscriber {
          transcriberModel = TalkscriberModelType.WHISPER
        }

        talkscriberTranscriber {
          transcriberModel = TalkscriberModelType.WHISPER
        }
      }
    }
  }

  @Test
  fun `multiple transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = ASSISTANT_REQUEST.toJsonElement()
      assistant(request) {
        talkscriberTranscriber {
          transcriberModel = TalkscriberModelType.WHISPER
        }

        gladiaTranscriber {
          transcriberModel = GladiaModelType.FAST
        }
      }
    }
  }

  @Test
  fun `deepgram transcriber enum value`() {
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
          transcriberLanguage = DeepgramLanguageType.GERMAN
        }
      }
    val je = assistant.toJsonElement()
    assertEquals(
      DeepgramLanguageType.GERMAN.desc,
      je["assistant.transcriber.language"].stringValue,
    )
  }

  @Test
  fun `deepgram transcriber custom value`() {
    val request = ASSISTANT_REQUEST.toJsonElement()
    val assistant =
      assistant(request) {
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }

        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
          customLanguage = "zzz"
        }
      }
    val jsonElement = assistant.toJsonElement()
    assertEquals(
      "zzz",
      jsonElement["assistant.transcriber.language"].stringValue,
    )
  }

  @Test
  fun `deepgram transcriber conflicting values`() {
    assertThrows(IllegalStateException::class.java) {
      val request = ASSISTANT_REQUEST.toJsonElement()
      val assistant =
        assistant(request) {
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }

          deepgramTranscriber {
            transcriberModel = DeepgramModelType.BASE
            transcriberLanguage = DeepgramLanguageType.GERMAN
            customLanguage = "zzz"
          }
        }
      val je = assistant.toJsonElement()
      assertEquals(
        "zzz",
        je["assistant.transcriber.language"].stringValue,
      )
    }
  }

  @Test
  fun `missing model decl`() {
    assertThrows(IllegalStateException::class.java) {
      val request = ASSISTANT_REQUEST.toJsonElement()
      assistant(request) {
        firstMessage = "Something"
      }
    }
  }

  companion object {
    const val ASSISTANT_REQUEST = """
    {
    "message": {
        "type": "assistant-request",
        "call": {
            "id": "00dbe917-37fd-4d3f-8cc0-ac6be0923f40",
            "orgId": "679a13ec-f40d-4055-8959-797c4ee1694b"
        },
        "timestamp": "2024-07-13T21:27:59.870Z"
      }
    }
    """
  }
}
