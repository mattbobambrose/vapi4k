# JSON Element Usage

The `JsonElement` class is the base class for all JSON elements in Kotlin. It represents a JSON value, which can
be a JSON object, a JSON array, a JSON string, a JSON number, a JSON boolean, or a JSON null.

The [JsonElementUtils](%utils_url%.json/-json-element-utils/index.html)
class provides utility functions for working with `JsonElement` objects.

Many of the `JsonElement` functions have a `vararg String` path argument.
These arguments are used to navigate the JSON object hierarchy. The arguments can be either comma-separated
strings or a single string with a dot-separated path.


<chapter title="Simple Example" id="squadId" collapsible="false">
<code-block lang="kotlin" src="src/main/kotlin/utils/JsonElements.kt" include-symbol="jsonElementExample"/>
</chapter>



