//package csense.idea.kotlin.checked.exceptions.ignore
//
//import com.intellij.psi.*
//import csense.idea.base.bll.kotlin.*
//import csense.idea.kotlin.checked.exceptions.bll.*
//import org.jetbrains.kotlin.psi.*
//import org.jetbrains.uast.*
//
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
//fun LambdaParameterData.isIgnored(ignoreInMemory: IgnoreInMemory): Boolean{
//    return ignoreInMemory.isArgumentMarkedAsIgnore(main, parameterName)
//}
//
//
//
//fun LambdaParameterData.isContainedInLambdaCatchingOrIgnored(
//        ignoreInMemory: IgnoreInMemory,
//        throwsTypes: List<UClass>
//): Boolean {
//    if (isIgnored(ignoreInMemory)) {
//        return true
//    }
//    return main.findInvocationOfName(parameterName)?.findParentTryCatch()?.catchesAll(throwsTypes) ?: return false
//}
//
//fun KtElement.getPotentialContainingLambda(): LambdaParameterData? {
//    var current: PsiElement = this
//    while (true) {
//        val isPotential = current.asPotentialContainingLambda()
//        if (isPotential != null) {
//            return isPotential
//        }
//        current = current.parent ?: return null
//    }
//}
//
//fun PsiElement.asPotentialContainingLambda(): LambdaParameterData? {
//    if (this is KtLambdaExpression &&
//            (this.parent?.parent is KtCallExpression ||
//                    this.parent?.parent?.parent is KtCallExpression)) {
//        val parent = this.parent?.parent as? KtCallExpression
//                ?: this.parent?.parent?.parent as? KtCallExpression
//        val main = parent?.resolveMainReference() as? KtFunction
//
//        val index = this.resolveParameterIndex()
//        if (main != null && index != null && index >= 0) {
//            val nameToFindInCode = main.valueParameters[index].name
//            if (nameToFindInCode != null) {
//                return LambdaParameterData(main, index, nameToFindInCode, this)
//            }
//        }
//    }
//    return null
//}
//
//fun KtLambdaExpression.asPotentialContainingLambda(): LambdaParameterData? {
//    if (this.parent?.parent is KtCallExpression ||
//            this.parent?.parent?.parent is KtCallExpression) {
//        val parent = this.parent?.parent as? KtCallExpression
//                ?: this.parent?.parent?.parent as? KtCallExpression
//        val main = parent?.resolveMainReference() as? KtFunction
//
//        val index = this.resolveParameterIndex()
//        if (main != null && index != null && index >= 0) {
//            val nameToFindInCode = main.valueParameters[index].name
//            if (nameToFindInCode != null) {
//                return LambdaParameterData(main, index, nameToFindInCode, this)
//            }
//        }
//    }
//    return null
//}
//
//data class LambdaParameterData(
//        val main: KtFunction,
//        val index: Int,
//        val parameterName: String,
//        val lambdaExpression: KtLambdaExpression
//)