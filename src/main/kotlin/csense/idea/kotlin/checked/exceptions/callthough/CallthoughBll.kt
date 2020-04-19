package csense.idea.kotlin.checked.exceptions.callthough

import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*


//fun KtElement.isContainedInLambdaCatchingOrIgnoredRecursive(
//        ignoreInMemory: IgnoreInMemory,
//        maxDepth: Int,
//        throwsTypes: List<UClass>
//): Boolean {
//    var currentElement = this
//    //if we reach max depth, just eject.
//    for (i in 0 until maxDepth) {
//        val potential = currentElement.getPotentialContainingLambda() ?: return false
//        if (potential.isContainedInLambdaCatchingOrIgnored(ignoreInMemory, throwsTypes)) {
//            return true
//        } else {
//            currentElement = potential.lambdaExpression.parent as? KtElement ?: return false
//        }
//    }
//    return false
//}
//
//fun LambdaParameterData.isContainedInLambdaCatchingOrIgnored(
//        ignoreInMemory: IgnoreInMemory,
//        throwsTypes: List<UClass>
//): Boolean {
//    if (ignoreInMemory.isArgumentMarkedAsIgnore(main, parameterName)) {
//        return true
//    }
//    return main.findInvocationOfName(parameterName)?.findParentTryCatch()?.catchesAll(throwsTypes) ?: return false
//}