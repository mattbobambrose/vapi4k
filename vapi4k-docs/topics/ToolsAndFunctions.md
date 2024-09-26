# Tools and Functions

Tools and functions allow an LLM to perform tasks and calculations.
The vapi4k DSL allows you to assign tools and functions to assistants and implement them in Kotlin code.

## @ToolCall and @Param annotations

<chapter title="Non-annotated Function" id="addTwoNumbers" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ToolCalls.kt" include-symbol="AddTwoNumbers"/>
</chapter>

<chapter title="Annotated Function" id="multiplyTwoNumbers" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ToolCalls.kt" include-symbol="MultiplyTwoNumbers"/>
</chapter>

<chapter title="Object Function" id="absoluteVale" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ToolCalls.kt" include-symbol="AbsoluteValue"/>
</chapter>

## Functions

vapi4k functions are implemented with Kotlin functions.

<chapter title="Function Declaration" id="function" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/Functions.kt" include-symbol="functionExample"/>
</chapter>

## Tools

### Service Tools

<chapter title="Function Declaration" id="serviceTool" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ServiceTools.kt" include-symbol="functionExample"/>
</chapter>

### ToolService

### Manual Tools

### External Tool

### TransferTool


