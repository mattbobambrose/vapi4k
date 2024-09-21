# Vapi4k Applications

The vapi4k ktor plugin allows you to define three types of applications:

* InboundCall
* OutboundCall
* Web

A single vapi4k configuration can include multiple application decls. The default `serverPath` value is `/vapi4k`.
If you include more than one application of a given type, you will need to specify a unique `serverPath` for each
application.

The `serverSecret` property is optional.

All applications require a `onAssistantRequest{}` be defined. Its argument will define
the desired assistant, assistantId, squad or squadId.

All applications also allow you define callbacks for requests and responses using the `onAllRequest{}`,
`onRequest{}`, `onAllResponse{}` and `onResponse{}` functions. These callbacks are also available globally
in the `Vapi4kConfig` context.

## InboundCall Applications

InboundCall applications are used to handle requests from the Vapi platform resulting from calls to Vapi.
Responses are specified using the [`inboundCallApplication{}`](%base_url%/-inbound-call-application/index.html)
function.

<chapter title="InboundCall Application Ktor Config" id="inboundApp" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/IncomingCall.kt" include-symbol="module"/>
</chapter>

## OutboundCall Applications

OutboundCall applications are used to make outgoing calls from the Vapi platform.
Responses are specified using the [`outboundCallApplication{}`](%base_url%/-outbound-call-application/index.html)
function.

<chapter title="OutboundCall Application Ktor Config" id="outboundApp" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/OutgoingCall.kt" include-symbol="module"/>
</chapter>

## Web Applications

Web applications are used to create webpage-based conversations.
Responses are specified using the [`webCallApplication{}`](%base_url%/-web-application/index.html) function.

<chapter title="Web Application Ktor Config" id="webAppKtor" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/WebCall.kt" include-symbol="module"/>
</chapter>

