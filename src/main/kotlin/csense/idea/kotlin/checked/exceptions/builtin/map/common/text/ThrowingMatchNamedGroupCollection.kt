package csense.idea.kotlin.checked.exceptions.builtin.map.common.text

import csense.idea.kotlin.checked.exceptions.builtin.*

val ThrowingCommonMatchNamedGroupCollection: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(
    //TODO INTERFACE
    FqNameReceiver(
        fqName = "kotlin.text.MatchNamedGroupCollection.get",
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException",
        "kotlin.UnsupportedOperationException"
    ),
)