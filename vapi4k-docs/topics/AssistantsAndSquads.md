# Assistants and Squads

The `onAssistantRequest{}` function is used to define the behavior of an application.
it is present in all the application descriptions.

## DSL

### Models

<tabs>
  <tab title="Anthropic">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="anthropicModel"/>
  </tab>
  <tab title="Anyscale">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="anyscaleModel"/>
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
  <tab title="Anthropic">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="anthropicModel"/>
  </tab>
</tabs>

### Transcribers

### start/complete/delayed/failed messages and conditions

### TransferDestinationRequests

#### Destinations

* AssistantDestination
* NumberDestination
* SipDestination
