package csense.idea.kotlin.checked.exceptions.builtin.throws.common.time

import csense.idea.kotlin.checked.exceptions.builtin.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.*

val ThrowingCommonDuration: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(

    //TODO argument
    FqNameReceiver(
        "kotlin.time.times",
        receiverFqName = "kotlin.Double"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        "kotlin.time.toDuration",
        receiverFqName = "kotlin.Double"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    //TODO argument
    FqNameReceiver(
        "kotlin.time.Duration.div"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    //TODO argument

    FqNameReceiver(
        "kotlin.time.Duration.minus"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    //TODO argument

    FqNameReceiver(
        "kotlin.time.Duration.times"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    //TODO argument
//    FqNameReceiver(
//        "kotlin.time.Duration.toString"
//    ) to BuiltInThrowingFunction(
//        "kotlin.IllegalArgumentException"
//    ),

)