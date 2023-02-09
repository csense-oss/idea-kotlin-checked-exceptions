package csense.idea.kotlin.checked.exceptions.builtin

data class BuiltInThrowingFunction(
    val exceptionFqNames: List<String>
) {
    constructor(vararg exceptionFqNames: String) : this(exceptionFqNames.toList())
}