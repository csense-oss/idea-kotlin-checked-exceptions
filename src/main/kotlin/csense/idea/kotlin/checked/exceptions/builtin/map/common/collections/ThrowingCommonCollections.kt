package csense.idea.kotlin.checked.exceptions.builtin.map.common.collections

import csense.idea.kotlin.checked.exceptions.builtin.*

val ThrowingCommonCollections: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(
    FqNameReceiver(
        fqName = "kotlin.collections.buildList"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),

    FqNameReceiver(
        fqName = "kotlin.collections.getValue",
        receiverFqName = "kotlin.collections.Map"
    ) to BuiltInThrowingFunction(
        "kotlin.NoSuchElementException"
    ),
    FqNameReceiver(
        fqName = "kotlin.collections.getValue",
        receiverFqName = "kotlin.collections.MutableMap"
    ) to BuiltInThrowingFunction(
        "kotlin.NoSuchElementException"
    ),

    FqNameReceiver(
        fqName = "kotlin.collections.buildMap"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.collections.getValue",
        receiverFqName = "kotlin.collections.Map"
    ) to BuiltInThrowingFunction(
        "kotlin.NoSuchElementException"
    ),

    FqNameReceiver(
        fqName = "kotlin.collections.getOrImplicitDefault",
        receiverFqName = "kotlin.collections.Map"
    ) to BuiltInThrowingFunction(
        "kotlin.NoSuchElementException"
    ),
    FqNameReceiver(
        fqName = "kotlin.collections.buildSet",
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
)