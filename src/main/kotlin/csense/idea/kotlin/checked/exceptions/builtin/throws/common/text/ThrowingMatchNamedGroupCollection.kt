package csense.idea.kotlin.checked.exceptions.builtin.throws.common.text

import csense.idea.kotlin.checked.exceptions.builtin.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.*

val ThrowingCommonMatchNamedGroupCollection: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(
    //TODO INTERFACE
    FqNameReceiver(
        fqName = "kotlin.text.MatchNamedGroupCollection.get",
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException",
        "kotlin.UnsupportedOperationException"
    ),
)