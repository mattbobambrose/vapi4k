# Applications

The Vapi4k Ktor plugin allows you to define three types of applications:

* InboundCall
* OutboundCall
* Web

## InboundCall Applications

InboundCall applications are used to handle requests from the Vapi platform resulting from calls to Vapi.
Responses are specified using the [`inboundCallApplication{}`](%core_url%.vapi4k/-inbound-call-application/index.html)
function.

<chapter title="InboundCall Application Ktor Config" id="inboundApp" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/IncomingCall.kt" include-symbol="module"/>
</chapter>

## OutboundCall Applications

OutboundCall applications are used to make outgoing calls from the Vapi platform.
Responses are specified using the [`outboundCallApplication{}`](%core_url%.vapi4k/-outbound-call-application/index.html)
function.

<chapter title="OutboundCall Application Ktor Config" id="outboundApp" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/OutgoingCall.kt" include-symbol="module"/>
</chapter>

## Web Applications

Web applications are used to create webpage-based conversations.
Responses are specified using the [`webCallApplication{}`](%core_url%.vapi4k/-web-application/index.html) function.

<chapter title="Web Application Ktor Config" id="webAppKtor" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/WebCall.kt" include-symbol="module"/>
</chapter>

## Application Properties

A Vapi4k configuration can include multiple application decls. The default `serverPath` value is `/vapi4k`.
If there is more than one application of a given type, you will need to specify a unique `serverPath` property value
for each application.

The `serverSecret` property is optional.

## Application Functions

All applications require a call to `onAssistantRequest{}`. Its contents will define
the desired assistant, assistantId, squad or squadId for the request.

All applications allow you define callbacks for requests and responses using the `onAllRequest{}`,
`onRequest{}`, `onAllResponse{}` and `onResponse{}` functions. These functions are also available globally
within the [`Vapi4kConfig` context](%core_url%.vapi4k/-vapi4k-config/index.html).

The arguments for `onRequest{}` and `onResponse{}` are of type
[ServerRequestType](%utils_url%.vapi4k.enums/-server-request-type/index.html).
