package csense.idea.kotlin.checked.exceptions.callthough

import csense.idea.kotlin.checked.exceptions.ignore.*

fun LambdaParameterData.isCallThough(): Boolean{
    return CallthoughInMemory.isArgumentMarkedAsCallthough(main, parameterName)
}