package csense.idea.kotlin.checked.exceptions.builtin

import csense.idea.kotlin.checked.exceptions.builtin.map.*
import csense.idea.kotlin.checked.exceptions.builtin.map.common.collections.*
import csense.idea.kotlin.checked.exceptions.builtin.map.common.random.*
import csense.idea.kotlin.checked.exceptions.builtin.map.common.ranges.*
import csense.idea.kotlin.checked.exceptions.builtin.map.common.text.*
import csense.idea.kotlin.checked.exceptions.builtin.map.common.time.*

object KotlinThrowingFunctions {
    //in short, the kotlin team Documented throws but did not annotate methods with the @throws...
    //so that is a bummer. :( ...
    val allBuiltIn: Map<FqNameReceiver, BuiltInThrowingFunction> =
        ThrowingCommonMath + ThrowingCommonText + ThrowingCommonUStrings +
                ThrowingCommonCollections + ThrowingCommonOpenEndRange + ThrowingCommonRandom +
                ThrowingCommonTextFunctions + ThrowingCommonMatchNamedGroupCollection + ThrowingCommonDuration
}


