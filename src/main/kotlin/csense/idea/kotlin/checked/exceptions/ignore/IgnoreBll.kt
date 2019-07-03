package csense.idea.kotlin.checked.exceptions.ignore

import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*

fun KtElement.isContainedInFunctionCatchingOrIgnored(ignoreInMemory: IgnoreInMemory): Boolean {
    val potential = getPotentialContainingLambda() ?: return false
    return potential.isContainedInFunctionCatchingOrIgnored(ignoreInMemory)
}

fun LambdaParameterData.isContainedInFunctionCatchingOrIgnored(ignoreInMemory: IgnoreInMemory): Boolean {
    if (ignoreInMemory.isArgumentMarkedAsIgnore(main, parameterName)) {
        return true
    }
    return main.findInvocationOfName(parameterName)?.isWrappedInTryCatch()?.let {
        return it
    } ?: return false
}

fun LambdaParameterData.isNotContainedInFunctionCatchingOrIgnored(ignoreInMemory: IgnoreInMemory): Boolean {
    return !isContainedInFunctionCatchingOrIgnored(ignoreInMemory)
}

fun KtElement.getPotentialContainingLambda(): LambdaParameterData? {
    var current: PsiElement = this
    while (true) {
        if (current is KtLambdaExpression &&
                (current.parent?.parent is KtCallExpression ||
                        current.parent?.parent?.parent is KtCallExpression)) {
            val parent = current.parent?.parent as? KtCallExpression
                    ?: current.parent?.parent?.parent as KtCallExpression
            val main = parent.resolveMainReference() as? KtFunction

            val index = current.resolveParameterIndex()
            if (main != null && index != null && index >= 0) {
                val nameToFindInCode = main.valueParameters[index].name
                if (nameToFindInCode != null) {
                    return LambdaParameterData(main, index, nameToFindInCode, current)
                }
            }
        }
        current = current.parent ?: return null
    }
}

data class LambdaParameterData(
        val main: KtFunction,
        val index: Int,
        val parameterName: String,
        val lambdaExpression: KtLambdaExpression
)