# Assistants and Squads

The `onAssistantRequest{}` function is used to define the behavior of an application.
it is present in all the application descriptions.

## DSL

### Models

<tabs>
  <tab title="Anthropic">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="anthropicExample"/>
  </tab>
  <tab title="Anyscale">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="anyscaleExample"/>
  </tab>
  <tab title="CustomLLM">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="customLLMExample"/>
  </tab>
  <tab title="DeepInfra">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="deepInfraExample"/>
  </tab>
  <tab title="Groq">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="groqExample"/>
  </tab>
  <tab title="OpenAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="openAIExample"/>
  </tab>
  <tab title="OpenRouter">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="openRouterExample"/>
  </tab>
  <tab title="PerplexityAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="perplexityAIExample"/>
  </tab>
  <tab title="TogetherAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="togetherAIExample"/>
  </tab>
  <tab title="Vapi">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="vapiExample"/>
  </tab>
</tabs>

### Voices

<tabs>
  <tab title="Azure">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="azureExample"/>
  </tab>
  <tab title="Cartesia">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="cartesiaExample"/>
  </tab>
  <tab title="Deepgram">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="deepgramExample"/>
  </tab>
  <tab title="ElevenLabs">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="elevenLabsExample"/>
  </tab>
  <tab title="LMNT">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="lmntExample"/>
  </tab>
  <tab title="Neets">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="neetsExample"/>
  </tab>
  <tab title="OpenAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="openAIExample"/>
  </tab>
  <tab title="PlayHT">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="playHTExample"/>
  </tab>
  <tab title="RimeAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Voices.kt" include-symbol="rimeAIExample"/>
  </tab>
</tabs>

### Transcribers

<tabs>
  <tab title="Deepgram">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Transcribers.kt" include-symbol="deepgramExample"/>
  </tab>
  <tab title="Gladia">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Transcribers.kt" include-symbol="gladiaExample"/>
  </tab>
  <tab title="Talkscriber">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Transcribers.kt" include-symbol="talkscriberExample"/>
  </tab>
</tabs>

### start/complete/delayed/failed messages and conditions

### TransferDestinationRequests

### Destinations

* AssistantDestination
* NumberDestination
* SipDestination
