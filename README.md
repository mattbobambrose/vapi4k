# Vapi4k: A Kotlin DSL for creating [vapi.ai](https://vapi.ai) applications

## SessionIds and AssistantIds

* Every assistant (AssistantOverrides and AssistantOverrides) is assigned a unique assistantId via
  `assistantIdSource.nextAssistantId()`

## serverUrl assignments

ServerUrls are assigned in:

* Phone.outboundCall{} -- sessionId assigned a random value
* AbstractAssistantResponseImpl.assistant{}
* MemberImpl.assistant{}
