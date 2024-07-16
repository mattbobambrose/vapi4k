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


import kotlinx.serialization.Serializable

@Serializable
data class Squad(
  val name: String = "",
  val members: List<Member> = listOf(),
  val membersOverrides: MembersOverrides = MembersOverrides(),
  val id: String = "",
  val orgId: String = "",
  val createdAt: String = "",
  val updatedAt: String = ""
) {
  @Serializable
  data class Member(
    val assistantId: String = "",
    val assistant: AssistantDto = AssistantDto(),
    val assistantOverrides: AssistantOverrides = AssistantOverrides(),
    val assistantDestinations: List<AssistantDestination> = listOf()
  ) {
    @Serializable
    data class AssistantDto(
      val transportConfigurations: List<TransportConfiguration> = listOf(),
    ) {
      @Serializable
      data class Transcriber(
        val provider: String = "",
        val model: String = "",
        val language: String = "",
        val smartFormat: Boolean = false,
        val keywords: List<String> = listOf()
      )

      @Serializable
      data class Model(
        val messages: List<Message> = listOf(),
        val tools: List<Tool> = listOf(),
        val toolIds: List<String> = listOf(),
        val provider: String = "",
        val model: String = "",
        val temperature: Int = 0,
        val knowledgeBase: KnowledgeBase = KnowledgeBase(),
        val maxTokens: Int = 0,
        val emotionRecognitionEnabled: Boolean = false
      ) {
        @Serializable
        data class Message(
          val content: String = "",
          val role: String = ""
        )

        @Serializable
        data class Tool(
          val async: Boolean = false,
          val messages: List<Message> = listOf(),
          val type: String = "",
          val function: Function = Function(),
          val server: Server = Server()
        ) {
          @Serializable
          data class Message(
            val type: String = "",
            val content: String = "",
            val conditions: List<Condition> = listOf()
          ) {
            @Serializable
            data class Condition(
              val `param`: String = "",
              val value: String = "",
              val `operator`: String = ""
            )
          }

          @Serializable
          data class Function(
            val name: String = "",
            val description: String = "",
            val parameters: Parameters = Parameters()
          ) {
            @Serializable
            data class Parameters(
              val type: String = "",
              val properties: Properties = Properties(),
              val required: List<String> = listOf()
            ) {
              @Serializable
              class Properties
            }
          }

          @Serializable
          data class Server(
            val timeoutSeconds: Int = 0,
            val url: String = "",
            val secret: String = ""
          )
        }

        @Serializable
        data class KnowledgeBase(
          val provider: String = "",
          val topK: Double = 0.0,
          val fileIds: List<String> = listOf()
        )
      }

      @Serializable
      data class Voice(
        val inputPreprocessingEnabled: Boolean = false,
        val inputReformattingEnabled: Boolean = false,
        val inputMinCharacters: Int = 0,
        val inputPunctuationBoundaries: List<String> = listOf(),
        val fillerInjectionEnabled: Boolean = false,
        val provider: String = "",
        val voiceId: String = "",
        val speed: Double = 0.0
      )

      @Serializable
      data class TransportConfiguration(
        val provider: String = "",
        val timeout: Int = 0,
        val record: Boolean = false,
        val recordingChannels: String = ""
      )

      @Serializable
      data class VoicemailDetection(
        val provider: String = "",
        val voicemailDetectionTypes: List<String> = listOf(),
        val enabled: Boolean = false,
        val machineDetectionTimeout: Int = 0,
        val machineDetectionSpeechThreshold: Int = 0,
        val machineDetectionSpeechEndThreshold: Int = 0,
        val machineDetectionSilenceTimeout: Int = 0
      )

      @Serializable
      class Metadata

      @Serializable
      data class AnalysisPlan(
        val summaryPrompt: String = "",
        val summaryRequestTimeoutSeconds: Double = 0.0,
        val structuredDataRequestTimeoutSeconds: Double = 0.0,
        val successEvaluationPrompt: String = "",
        val successEvaluationRubric: String = "",
        val successEvaluationRequestTimeoutSeconds: Double = 0.0,
        val structuredDataPrompt: String = "",
        val structuredDataSchema: StructuredDataSchema = StructuredDataSchema()
      ) {
        @Serializable
        data class StructuredDataSchema(
          val type: String = "",
          val items: Items = Items(),
          val properties: Properties = Properties(),
          val description: String = "",
          val required: List<String> = listOf()
        ) {
          @Serializable
          class Items

          @Serializable
          class Properties
        }
      }

      @Serializable
      data class ArtifactPlan(
        val videoRecordingEnabled: Boolean = false
      )

      @Serializable
      data class MessagePlan(
        val idleMessages: List<String> = listOf(),
        val idleMessageMaxSpokenCount: Double = 0.0,
        val idleTimeoutSeconds: Double = 0.0
      )
    }

    @Serializable
    data class AssistantOverrides(
      val transcriber: Transcriber = Transcriber(),
      val model: Model = Model(),
      val voice: Voice = Voice(),
      val firstMessageMode: String = "",
      val recordingEnabled: Boolean = false,
      val hipaaEnabled: Boolean = false,
      val clientMessages: List<String> = listOf(),
      val serverMessages: List<String> = listOf(),
      val silenceTimeoutSeconds: Int = 0,
      val responseDelaySeconds: Double = 0.0,
      val llmRequestDelaySeconds: Double = 0.0,
      val llmRequestNonPunctuatedDelaySeconds: Double = 0.0,
      val numWordsToInterruptAssistant: Int = 0,
      val maxDurationSeconds: Int = 0,
      val backgroundSound: String = "",
      val backchannelingEnabled: Boolean = false,
      val backgroundDenoisingEnabled: Boolean = false,
      val modelOutputInMessagesEnabled: Boolean = false,
      val transportConfigurations: List<TransportConfiguration> = listOf(),
      val variableValues: VariableValues = VariableValues(),
      val name: String = "",
      val firstMessage: String = "",
      val voicemailDetection: VoicemailDetection = VoicemailDetection(),
      val voicemailMessage: String = "",
      val endCallMessage: String = "",
      val endCallPhrases: List<String> = listOf(),
      val metadata: Metadata = Metadata(),
      val serverUrl: String = "",
      val serverUrlSecret: String = "",
      val analysisPlan: AnalysisPlan = AnalysisPlan(),
      val artifactPlan: ArtifactPlan = ArtifactPlan(),
      val messagePlan: MessagePlan = MessagePlan()
    ) {
      @Serializable
      data class Transcriber(
        val provider: String = "",
        val model: String = "",
        val language: String = "",
        val smartFormat: Boolean = false,
        val keywords: List<String> = listOf()
      )

      @Serializable
      data class Model(
        val messages: List<Message> = listOf(),
        val tools: List<Tool> = listOf(),
        val toolIds: List<String> = listOf(),
        val provider: String = "",
        val model: String = "",
        val temperature: Int = 0,
        val knowledgeBase: KnowledgeBase = KnowledgeBase(),
        val maxTokens: Int = 0,
        val emotionRecognitionEnabled: Boolean = false
      ) {
        @Serializable
        data class Message(
          val content: String = "",
          val role: String = ""
        )

        @Serializable
        data class Tool(
          val async: Boolean = false,
          val messages: List<Message> = listOf(),
          val type: String = "",
          val function: Function = Function(),
          val server: Server = Server()
        ) {
          @Serializable
          data class Message(
            val type: String = "",
            val content: String = "",
            val conditions: List<Condition> = listOf()
          ) {
            @Serializable
            data class Condition(
              val `param`: String = "",
              val value: String = "",
              val `operator`: String = ""
            )
          }

          @Serializable
          data class Function(
            val name: String = "",
            val description: String = "",
            val parameters: Parameters = Parameters()
          ) {
            @Serializable
            data class Parameters(
              val type: String = "",
              val properties: Properties = Properties(),
              val required: List<String> = listOf()
            ) {
              @Serializable
              class Properties
            }
          }

          @Serializable
          data class Server(
            val timeoutSeconds: Int = 0,
            val url: String = "",
            val secret: String = ""
          )
        }

        @Serializable
        data class KnowledgeBase(
          val provider: String = "",
          val topK: Double = 0.0,
          val fileIds: List<String> = listOf()
        )
      }

      @Serializable
      data class Voice(
        val inputPreprocessingEnabled: Boolean = false,
        val inputReformattingEnabled: Boolean = false,
        val inputMinCharacters: Int = 0,
        val inputPunctuationBoundaries: List<String> = listOf(),
        val fillerInjectionEnabled: Boolean = false,
        val provider: String = "",
        val voiceId: String = "",
        val speed: Double = 0.0
      )

      @Serializable
      data class TransportConfiguration(
        val provider: String = "",
        val timeout: Int = 0,
        val record: Boolean = false,
        val recordingChannels: String = ""
      )

      @Serializable
      class VariableValues

      @Serializable
      data class VoicemailDetection(
        val provider: String = "",
        val voicemailDetectionTypes: List<String> = listOf(),
        val enabled: Boolean = false,
        val machineDetectionTimeout: Int = 0,
        val machineDetectionSpeechThreshold: Int = 0,
        val machineDetectionSpeechEndThreshold: Int = 0,
        val machineDetectionSilenceTimeout: Int = 0
      )

      @Serializable
      class Metadata

      @Serializable
      data class AnalysisPlan(
        val summaryPrompt: String = "",
        val summaryRequestTimeoutSeconds: Double = 0.0,
        val structuredDataRequestTimeoutSeconds: Double = 0.0,
        val successEvaluationPrompt: String = "",
        val successEvaluationRubric: String = "",
        val successEvaluationRequestTimeoutSeconds: Double = 0.0,
        val structuredDataPrompt: String = "",
        val structuredDataSchema: StructuredDataSchema = StructuredDataSchema()
      ) {
        @Serializable
        data class StructuredDataSchema(
          val type: String = "",
          val items: Items = Items(),
          val properties: Properties = Properties(),
          val description: String = "",
          val required: List<String> = listOf()
        ) {
          @Serializable
          class Items

          @Serializable
          class Properties
        }
      }

      @Serializable
      data class ArtifactPlan(
        val videoRecordingEnabled: Boolean = false
      )

      @Serializable
      data class MessagePlan(
        val idleMessages: List<String> = listOf(),
        val idleMessageMaxSpokenCount: Double = 0.0,
        val idleTimeoutSeconds: Double = 0.0
      )
    }

    @Serializable
    data class AssistantDestination(
      val type: String = "",
      val assistantName: String = "",
      val message: String = "",
      val description: String = ""
    )
  }

  @Serializable
  data class MembersOverrides(
    val transcriber: Transcriber = Transcriber(),
    val model: Model = Model(),
    val voice: Voice = Voice(),
    val firstMessageMode: String = "",
    val recordingEnabled: Boolean = false,
    val hipaaEnabled: Boolean = false,
    val clientMessages: List<String> = listOf(),
    val serverMessages: List<String> = listOf(),
    val silenceTimeoutSeconds: Int = 0,
    val responseDelaySeconds: Double = 0.0,
    val llmRequestDelaySeconds: Double = 0.0,
    val llmRequestNonPunctuatedDelaySeconds: Double = 0.0,
    val numWordsToInterruptAssistant: Int = 0,
    val maxDurationSeconds: Int = 0,
    val backgroundSound: String = "",
    val backchannelingEnabled: Boolean = false,
    val backgroundDenoisingEnabled: Boolean = false,
    val modelOutputInMessagesEnabled: Boolean = false,
    val transportConfigurations: List<TransportConfiguration> = listOf(),
    val variableValues: VariableValues = VariableValues(),
    val name: String = "",
    val firstMessage: String = "",
    val voicemailDetection: VoicemailDetection = VoicemailDetection(),
    val voicemailMessage: String = "",
    val endCallMessage: String = "",
    val endCallPhrases: List<String> = listOf(),
    val metadata: Metadata = Metadata(),
    val serverUrl: String = "",
    val serverUrlSecret: String = "",
    val analysisPlan: AnalysisPlan = AnalysisPlan(),
    val artifactPlan: ArtifactPlan = ArtifactPlan(),
    val messagePlan: MessagePlan = MessagePlan()
  ) {
    @Serializable
    data class Transcriber(
      val provider: String = "",
      val model: String = "",
      val language: String = "",
      val smartFormat: Boolean = false,
      val keywords: List<String> = listOf()
    )

    @Serializable
    data class Model(
      val messages: List<Message> = listOf(),
      val tools: List<Tool> = listOf(),
      val toolIds: List<String> = listOf(),
      val provider: String = "",
      val model: String = "",
      val temperature: Int = 0,
      val knowledgeBase: KnowledgeBase = KnowledgeBase(),
      val maxTokens: Int = 0,
      val emotionRecognitionEnabled: Boolean = false
    ) {
      @Serializable
      data class Message(
        val content: String = "",
        val role: String = ""
      )

      @Serializable
      data class Tool(
        val async: Boolean = false,
        val messages: List<Message> = listOf(),
        val type: String = "",
        val function: Function = Function(),
        val server: Server = Server()
      ) {
        @Serializable
        data class Message(
          val type: String = "",
          val content: String = "",
          val conditions: List<Condition> = listOf()
        ) {
          @Serializable
          data class Condition(
            val `param`: String = "",
            val value: String = "",
            val `operator`: String = ""
          )
        }

        @Serializable
        data class Function(
          val name: String = "",
          val description: String = "",
          val parameters: Parameters = Parameters()
        ) {
          @Serializable
          data class Parameters(
            val type: String = "",
            val properties: Properties = Properties(),
            val required: List<String> = listOf()
          ) {
            @Serializable
            class Properties
          }
        }

        @Serializable
        data class Server(
          val timeoutSeconds: Int = 0,
          val url: String = "",
          val secret: String = ""
        )
      }

      @Serializable
      data class KnowledgeBase(
        val provider: String = "",
        val topK: Double = 0.0,
        val fileIds: List<String> = listOf()
      )
    }

    @Serializable
    data class Voice(
      val inputPreprocessingEnabled: Boolean = false,
      val inputReformattingEnabled: Boolean = false,
      val inputMinCharacters: Int = 0,
      val inputPunctuationBoundaries: List<String> = listOf(),
      val fillerInjectionEnabled: Boolean = false,
      val provider: String = "",
      val voiceId: String = "",
      val speed: Double = 0.0
    )

    @Serializable
    data class TransportConfiguration(
      val provider: String = "",
      val timeout: Int = 0,
      val record: Boolean = false,
      val recordingChannels: String = ""
    )

    @Serializable
    class VariableValues

    @Serializable
    data class VoicemailDetection(
      val provider: String = "",
      val voicemailDetectionTypes: List<String> = listOf(),
      val enabled: Boolean = false,
      val machineDetectionTimeout: Int = 0,
      val machineDetectionSpeechThreshold: Int = 0,
      val machineDetectionSpeechEndThreshold: Int = 0,
      val machineDetectionSilenceTimeout: Int = 0
    )

    @Serializable
    class Metadata

    @Serializable
    data class AnalysisPlan(
      val summaryPrompt: String = "",
      val summaryRequestTimeoutSeconds: Double = 0.0,
      val structuredDataRequestTimeoutSeconds: Double = 0.0,
      val successEvaluationPrompt: String = "",
      val successEvaluationRubric: String = "",
      val successEvaluationRequestTimeoutSeconds: Double = 0.0,
      val structuredDataPrompt: String = "",
      val structuredDataSchema: StructuredDataSchema = StructuredDataSchema()
    ) {
      @Serializable
      data class StructuredDataSchema(
        val type: String = "",
        val items: Items = Items(),
        val properties: Properties = Properties(),
        val description: String = "",
        val required: List<String> = listOf()
      ) {
        @Serializable
        class Items

        @Serializable
        class Properties
      }
    }

    @Serializable
    data class ArtifactPlan(
      val videoRecordingEnabled: Boolean = false
    )

    @Serializable
    data class MessagePlan(
      val idleMessages: List<String> = listOf(),
      val idleMessageMaxSpokenCount: Double = 0.0,
      val idleTimeoutSeconds: Double = 0.0
    )
  }
}
