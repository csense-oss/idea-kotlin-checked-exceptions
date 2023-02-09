package csense.idea.kotlin.checked.exceptions.builtin

import csense.idea.kotlin.checked.exceptions.builtin.map.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.common.collections.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.common.math.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.common.random.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.common.ranges.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.common.text.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.common.time.*

object KotlinThrowingFunctions {
    //in short, the kotlin team Documented throws but did not annotate methods with the @throws...
    //so that is a bummer. :( ...
    val allBuiltIn: Map<FqNameReceiver, BuiltInThrowingFunction> =
        ThrowingCommonMath + ThrowingCommonText + ThrowingCommonUStrings +
                ThrowingCommonCollections + ThrowingCommonOpenEndRange + ThrowingCommonRandom +
                ThrowingCommonTextFunctions + ThrowingCommonMatchNamedGroupCollection + ThrowingCommonDuration
}


