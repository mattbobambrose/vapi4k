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

import com.vapi4k.dsl.assistant.AssistantDsl.assistantResponse
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType.ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.dsl.model.enums.DeepgramModelType
import com.vapi4k.dsl.model.enums.GladiaModelType
import com.vapi4k.dsl.model.enums.OpenAIModelType
import com.vapi4k.dsl.tools.ToolCache.Companion.clearToolCache
import com.vapi4k.dsl.tools.enums.ToolMessageType
import com.vapi4k.dsl.transcriber.enums.DeepgramLanguageType
import com.vapi4k.dsl.transcriber.enums.TalkscriberModelType
import com.vapi4k.dsl.vapi4k.RequestContext
import com.vapi4k.dsl.vapi4k.Vapi4kApplication
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.utils.DslUtils.getRandomSecret
import com.vapi4k.utils.JsonElementUtils.assistantClientMessages
import com.vapi4k.utils.JsonElementUtils.assistantServerMessages
import com.vapi4k.utils.JsonFilenames.JSON_ASSISTANT_REQUEST
import com.vapi4k.utils.firstMessageOfType
import com.vapi4k.utils.get
import com.vapi4k.utils.intValue
import com.vapi4k.utils.stringValue
import com.vapi4k.utils.toJsonElement
import com.vapi4k.utils.toJsonString
import com.vapi4k.utils.withTestApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.Test

class AssistantTest {
  init {
    Vapi4kConfig()
//      .apply {
//        configure {
//          serverUrl = "HelloWorld"
//          serverUrlSecret = "12345"
//        }
//      }
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

  @Test
  fun testRegular() {
    clearToolCache()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
        assistantResponse(requestContext) {
          assistant {
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
      }

//    println(jsonElement.toJsonString())
//    println("This is the first message: ${jsonElement.firstToolMessages().toJsonString()}")
    assertEquals(
      "This is the test request start message",
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START).stringValue("content"),
    )
  }

  @Test
  fun `multiple application{} decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          firstMessage = "Something"
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
        assistant {
          firstMessage = "Something"
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
      }
    }.also {
      assertEquals("An assistant{} is already declared", it.message)
    }
  }

  @Test
  fun `application{} and squad{} decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          firstMessage = "Something"
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
        assistantId {
          id = "12345"
        }
      }
    }.also {
      assertEquals("An assistant{} is already declared", it.message)
    }
  }

  @Test
  fun `Missing application{} decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
      }
    }.also {
      assertEquals(
        "assistantResponse{} is missing an assistant{}, assistantId{}, squad{}, or squadId{} declaration",
        it.message
      )
    }
  }


  @Test
  fun `test reverse delay order`() {
    clearToolCache()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
        assistantResponse(requestContext) {
          assistant {
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
      }
    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(delayedMessage, stringValue("content"))
      assertEquals(2000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `test message with no millis`() {
    clearToolCache()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
        assistantResponse(requestContext) {
          assistant {
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
    clearToolCache()
    val (response, jsonElement) =
      withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
        assistantResponse(requestContext) {
          assistant {
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
      }

    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(secondDelayedMessage, stringValue("content"))
      assertEquals(2000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `multiple delay time`() {
    clearToolCache()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
      assistantResponse(requestContext) {
        assistant {
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
    }

    println(jsonElement.toJsonString())

    assertEquals(
      1000,
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED).intValue("timingMilliseconds"),
    )
  }

  @Test
  fun `multiple message multiple delay time`() {
    clearToolCache()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
      assistantResponse(requestContext) {
        assistant {
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
    }
    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(secondDelayedMessage, stringValue("content"))
      assertEquals(1000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `chicago illinois message`() {
    clearToolCache()
    val (response, jsonElement) = withTestApplication(JSON_ASSISTANT_REQUEST) { requestContext ->
      assistantResponse(requestContext) {
        assistant {
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
    }

    val chicagoCity = "city" eq "Chicago"
    val illinoisState = "state" eq "Illinois"

    val chicagoStartMessage =
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START, chicagoCity, illinoisState)

    val chicagoCompleteMessage =
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_COMPLETE, chicagoCity, illinoisState)

    val chicagoFailedMessage =
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_FAILED, chicagoCity, illinoisState)

    val chicagoDelayedMessage =
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED, chicagoCity, illinoisState)

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
  fun `Missing message`() {
    clearToolCache()
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                condition("city" eq "Chicago", "state" eq "Illinois") {
                }
              }
            }
          }
        }
      }
    }.also {
      assert(it.message.orEmpty().contains("must have at least one message"))
    }
  }

  @Test
  fun `error on duplicate reverse conditions`() {
    clearToolCache()
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
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
    }.also {
      assert(it.message.orEmpty().contains("duplicates an existing condition{}"))
    }
  }

  @Test
  fun `check non-default FirstMessageModeType values`() {
    clearToolCache()
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          firstMessageMode = ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
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
    clearToolCache()
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          clientMessages -= AssistantClientMessageType.HANG
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(9, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant client messages 2`() {
    clearToolCache()
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          clientMessages -= setOf(AssistantClientMessageType.HANG, AssistantClientMessageType.STATUS_UPDATE)
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant server messages 1`() {
    clearToolCache()
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          firstMessage = "Something"
          serverMessages -= AssistantServerMessageType.HANG
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantServerMessages.size)
  }

  @Test
  fun `check assistant server messages 2`() {
    clearToolCache()
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          serverMessages -= setOf(AssistantServerMessageType.HANG, AssistantServerMessageType.SPEECH_UPDATE)
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(7, element.assistantServerMessages.size)
  }

  @Test
  fun `multiple deepgram transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          deepgramTranscriber {
            transcriberModel = DeepgramModelType.BASE
          }

          deepgramTranscriber {
            transcriberModel = DeepgramModelType.BASE
          }
        }
      }
    }.also {
      assertEquals("deepgramTranscriber{} requires a transcriberLanguage or customLanguagevalue", it.message)
    }
  }

  @Test
  fun `multiple gladia transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          gladiaTranscriber {
            transcriberModel = GladiaModelType.FAST
          }

          gladiaTranscriber {
            transcriberModel = GladiaModelType.FAST
          }
        }
      }
    }.also {
      assertEquals("gladiaTranscriber{} requires a transcriberLanguage or customLanguage value", it.message)
    }
  }

  @Test
  fun `multiple talkscriber transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          talkscriberTranscriber {
            transcriberModel = TalkscriberModelType.WHISPER
          }

          talkscriberTranscriber {
            transcriberModel = TalkscriberModelType.WHISPER
          }
        }
      }
    }.also {
      assertEquals("talkscriberTranscriber{} requires a transcriberLanguage or customLanguage value", it.message)
    }
  }

  @Test
  fun `multiple transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          talkscriberTranscriber {
            transcriberModel = TalkscriberModelType.WHISPER
          }

          gladiaTranscriber {
            transcriberModel = GladiaModelType.FAST
          }
        }
      }
    }.also {
      assertEquals("talkscriberTranscriber{} requires a transcriberLanguage or customLanguage value", it.message)
    }
  }

  @Test
  fun `deepgram transcriber enum value`() {
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }
          deepgramTranscriber {
            transcriberModel = DeepgramModelType.BASE
            transcriberLanguage = DeepgramLanguageType.GERMAN
          }
        }
      }
    val je = assistant.toJsonElement()
    assertEquals(
      DeepgramLanguageType.GERMAN.desc,
      je.stringValue("assistant.transcriber.language"),
    )
  }

  @Test
  fun `deepgram transcriber custom value`() {
    val assistant =
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }

          deepgramTranscriber {
            transcriberModel = DeepgramModelType.BASE
            customLanguage = "zzz"
          }
        }
      }
    val jsonElement = assistant.toJsonElement()
    assertEquals(
      "zzz",
      jsonElement.stringValue("assistant.transcriber.language"),
    )
  }

  @Test
  fun `deepgram transcriber conflicting values`() {
    assertThrows(IllegalStateException::class.java) {
      val assistant =
        assistantResponse(REQUEST_CONTEXT) {
          assistant {
            openAIModel {
              modelType = OpenAIModelType.GPT_3_5_TURBO
            }

            deepgramTranscriber {
              transcriberModel = DeepgramModelType.BASE
              transcriberLanguage = DeepgramLanguageType.GERMAN
              customLanguage = "zzz"
            }
          }
        }
      val je = assistant.toJsonElement()
      assertEquals(
        "zzz",
        je["assistant.transcriber.language"].stringValue,
      )
    }.also {
      assertEquals(
        "deepgramTranscriber{} cannot have both transcriberLanguage and customLanguage values",
        it.message,
      )
    }
  }

  @Test
  fun `missing model decl`() {
    assertThrows(IllegalStateException::class.java) {
      assistantResponse(REQUEST_CONTEXT) {
        assistant {
          firstMessage = "Something"
        }
      }
    }.also {
      assertEquals("An assistant{} requires a model{} decl", it.message)
    }
  }

  @Test
  fun `new getRandomSecret`() {
    println(getRandomSecret(8, 4, 4, 7))
    println(getRandomSecret(8, 4, 4, 7))
    println(getRandomSecret(8, 4, 4, 7))
    println(getRandomSecret(8, 4, 4, 7))
    println(getRandomSecret(8, 4, 4, 7))
    println(getRandomSecret(8, 4, 4, 7))
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
    val REQUEST_CONTEXT = RequestContext(Vapi4kApplication(), ASSISTANT_REQUEST.toJsonElement())
  }
}
