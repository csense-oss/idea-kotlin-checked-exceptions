package csense.idea.kotlin.checked.exceptions.ignore

import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*

fun KtElement.isContainedInLambdaCatchingOrIgnoredRecursive(
        ignoreInMemory: IgnoreInMemory,
        maxDepth: Int,
        throwsTypes: List<UClass>
): Boolean {
    var currentElement = this
    //if we reach max depth, just eject.
    for (i in 0 until maxDepth) {
        val potential = currentElement.getPotentialContainingLambda() ?: return false
        if (potential.isContainedInLambdaCatchingOrIgnored(ignoreInMemory, throwsTypes)) {
            return true
        } else {
            currentElement = potential.lambdaExpression.parent as? KtElement ?: return false
        }
    }
    return false
}

fun LambdaParameterData.isContainedInLambdaCatchingOrIgnored(
        ignoreInMemory: IgnoreInMemory,
        throwsTypes: List<UClass>
): Boolean {
    if (ignoreInMemory.isArgumentMarkedAsIgnore(main, parameterName)) {
        return true
    }
    return main.findInvocationOfName(parameterName)?.findParentTryCatch()?.let {
        return it.catchesAll(throwsTypes)
    } ?: return false
}

fun KtElement.getPotentialContainingLambda(): LambdaParameterData? {
    var current: PsiElement = this
    while (true) {
        if (current is KtLambdaExpression &&
                (current.parent?.parent is KtCallExpression ||
                        current.parent?.parent?.parent is KtCallExpression)) {
            val parent = current.parent?.parent as? KtCallExpression
                    ?: current.parent?.parent?.parent as? KtCallExpression
            val main = parent?.resolveMainReference() as? KtFunction

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