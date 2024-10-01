<show-structure depth="2"/>
# Tools and Functions

Tools and functions allow an LLM to perform tasks and calculations.
The vapi4k DSL allows you to assign tools and functions to assistants and implement them in Kotlin code.

## Tools

### Service Tools

The `tools{}` lambda can contain `serviceTool()`, `manualTool()`, `externalTool()`, and `transferTool()` calls.

<chapter title="Function Declaration" id="serviceTool" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ServiceTools.kt" include-symbol="functionExample"/>
</chapter>

### ToolService

### ManualTool

A `manualTool()` is defined within the vapi4k DSL. The parameters are manually extracted from the `args` JsonElement,
which is the JSON object included in the LLM request. A `manualTool()` is not associated with a specific Kotlin object.

<chapter title="" id="manualTool" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ManualTools.kt" include-symbol="manualToolExample"/>
</chapter>

### ExternalTool

An `externalTool()` is .

<chapter title="ManualTool Example" id="externalTool" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ManualTools.kt" include-symbol="manualToolExample"/>
</chapter>

### TransferTool

## Functions

vapi4k functions declarations map LLM function calls to Kotlin function invocations.

The `functions{}` lambda contains `function()` declarations.
The `function()` argument can be either an object instance or a reference to a Kotlin singleton object.

<chapter title="Function Declaration" id="function" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/Functions.kt" include-symbol="functionExample"/>
</chapter>

## Kotlin Function and Service Tool implementations

The `@ToolCall` and `@Param` annotations are used to express intent to the LLM.
The `@ToolCall` annotation describes when a function or serviceTool is called, and the `@Param` annotation
describes the parameters of the function or serviceTool. If a function or serviceTool is not annotated, the LLM will
infer when to invoke the function based on the function name, and the arguments from the parameter names.


<chapter title="Non-annotated Function" id="addTwoNumbers" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ToolCalls.kt" include-symbol="AddTwoNumbers"/>
</chapter>

<chapter title="Annotated Function" id="multiplyTwoNumbers" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ToolCalls.kt" include-symbol="MultiplyTwoNumbers"/>
</chapter>

<chapter title="Object Function" id="absoluteVale" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/tools/ToolCalls.kt" include-symbol="AbsoluteValue"/>
</chapter>


