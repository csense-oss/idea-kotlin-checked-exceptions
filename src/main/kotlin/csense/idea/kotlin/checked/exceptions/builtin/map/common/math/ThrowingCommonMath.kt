package csense.idea.kotlin.checked.exceptions.builtin.map

import csense.idea.kotlin.checked.exceptions.builtin.*

val ThrowingCommonMath: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(
    FqNameReceiver(
        fqName = "kotlin.math.roundToInt",
        receiverFqName = "kotlin.Float"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.math.roundToLong",
        receiverFqName = "kotlin.Float"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.math.roundToInt",
        receiverFqName = "kotlin.Double"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.math.roundToLong",
        receiverFqName = "kotlin.Double"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException",
    )
)
