# Vapi4k Applications

The vapi4k ktor plugin allows you to define three types of applications:

* InboundCall
* OutboundCall
* Web

## InboundCall Applications

InboundCall applications are used to handle requests from the Vapi platform resulting from incoming phone calls.
They are defined using the `inboundCallApplication` function.


<chapter title="InboundCall Application Ktor Config" id="inboundApp" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/IncomingCall.kt" include-symbol="module"/>
</chapter>

## OutboundCall Applications

OutboundCall applications are used to make outgoing calls from the Vapi platform.

<chapter title="OutboundCall Application Ktor Config" id="outboundApp" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/OutgoingCall.kt" include-symbol="module"/>
</chapter>

## Web Applications

Web applications are used to create webpage-based conversations.

<chapter title="Web Application Ktor Config" id="webAppKtor" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/applications/WebCall.kt" include-symbol="module"/>
</chapter>
