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
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="customLLMModel"/>
  </tab>
  <tab title="DeepInfra">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="deepInfraModel"/>
  </tab>
  <tab title="Groq">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="groqModel"/>
  </tab>
  <tab title="OpenAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="openAIModel"/>
  </tab>
  <tab title="OpenRouter">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="openRouterModel"/>
  </tab>
  <tab title="PerplexityAI">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="perplexityAIModel"/>
  </tab>
  <tab title="Vapi">
    <code-block lang="kotlin" src="src/main/kotlin/assistants/Models.kt" include-symbol="vapiModel"/>
  </tab>
</tabs>

### Transcribers

### Voices

### start/complete/delayed/failed messages and conditions

### TransferDestinationRequests

#### Destinations

* AssistantDestination
* NumberDestination
* SipDestination
