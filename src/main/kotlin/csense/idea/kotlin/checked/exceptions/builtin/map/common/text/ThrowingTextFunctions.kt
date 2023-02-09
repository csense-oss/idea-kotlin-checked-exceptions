package csense.idea.kotlin.checked.exceptions.builtin.map.common.text

import csense.idea.kotlin.checked.exceptions.builtin.*


val ThrowingCommonTextFunctions: Map<FqNameReceiver, BuiltInThrowingFunction> = mapOf(
    //TODO interface function(s) ... :( and with arguments...
//    FqNameReceiver(
//        fqName = "kotlin.text.Appendable.append"
//    ) to BuiltInThrowingFunction(
//        "kotlin.IndexOutOfBoundsException",
//        "kotlin.IllegalArgumentException"
//    )
//
    FqNameReceiver(
        fqName = "kotlin.text.appendRange",
        receiverFqName = "T: Appendable" //TODO!?!?!?
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),

    //TODO arguments....
    FqNameReceiver(
        fqName = "kotlin.text.appendRange",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.deleteAt",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.deleteRange",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.insertRange",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.set",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),
    FqNameReceiver(
        fqName = "kotlin.text.setRange",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),

    FqNameReceiver(
        fqName = "kotlin.text.StringBuilder.insert",
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),


    FqNameReceiver(
        fqName = "kotlin.text.StringBuilder.setLength",
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),
    //TODO arguments..
    FqNameReceiver(
        fqName = "kotlin.text.StringBuilder.substring",
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException"
    ),



    FqNameReceiver(
        fqName = "kotlin.text.toCharArray",
        receiverFqName = "StringBuilder"
    ) to BuiltInThrowingFunction(
        "kotlin.IndexOutOfBoundsException",
        "kotlin.IllegalArgumentException"
    ),


)
