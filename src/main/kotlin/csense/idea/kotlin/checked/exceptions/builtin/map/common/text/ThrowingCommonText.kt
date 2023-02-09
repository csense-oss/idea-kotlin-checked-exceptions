package csense.idea.kotlin.checked.exceptions.builtin.map.common.text

import csense.idea.kotlin.checked.exceptions.builtin.*

val ThrowingCommonText: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(
    FqNameReceiver(
        fqName = "kotlin.text.concatToString",
        receiverFqName = "kotlin.CharArray"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.decodeToString",
        receiverFqName = "kotlin.ByteArray"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException",
        "kotlin.text.CharacterCodingException",
    ),
    FqNameReceiver(
        fqName = "kotlin.text.encodeToByteArray",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException",
        "kotlin.text.CharacterCodingException",
    ),
//regex
    FqNameReceiver(
        fqName = "kotlin.text.Regex.find",
        receiverFqName = null
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.Regex.findAll",
        receiverFqName = null
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.Regex.matchAt",
        receiverFqName = null
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.Regex.matchesAt",
        receiverFqName = null
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),
// end regex
    FqNameReceiver(
        fqName = "kotlin.text.repeat",
        receiverFqName = "kotlin.CharSequence"
    ) to BuiltInThrowingFunction(
        "kotlin.IllegalArgumentException"
    ),
    //@DeprecatedSinceKotlin(warningSince = "1.4", errorSince = "1.5")
    FqNameReceiver(
        fqName = "kotlin.text.String",
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.toByte",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.NumberFormatException",
        "kotlin.IllegalArgumentException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.toCharArray",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.toDouble",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.NumberFormatException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.toFloat",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.NumberFormatException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.toInt",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.NumberFormatException",
//radix:        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.toLong",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.NumberFormatException",
//radix:        "kotlin.IllegalArgumentException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.toShort",
        receiverFqName = "kotlin.String"
    ) to BuiltInThrowingFunction(
        "kotlin.NumberFormatException",
//radix:        "kotlin.IllegalArgumentException"
    ),
)
