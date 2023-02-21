package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.psi.*

fun KtElement.quickFixesFor(
    uncaughtExceptions: List<KtPsiClass>,
    state: IncrementalExceptionCheckerState
): Array<LocalQuickFix> = when (this) {
    is KtThrowExpression -> quickFixesFor(uncaughtExceptions = uncaughtExceptions, state = state)
    is KtCallExpression -> quickFixesFor(uncaughtExceptions = uncaughtExceptions, state = state)
    else -> arrayOf()
}

fun KtThrowExpression.quickFixesFor(
    uncaughtExceptions: List<KtPsiClass>,
    state: IncrementalExceptionCheckerState
): Array<LocalQuickFix> {
    val parentScope: KtElement = state.findParentScope(from = this) ?: return emptyArray()

    if (parentScope.isNot<KtLambdaExpression>() && parentScope is KtAnnotated) {
        return arrayOf(
            AddThrowsTypesQuickFix(
                toExpression = parentScope,
                missingThrowsTypes = uncaughtExceptions
            )
        )
    }

    val result: MutableList<LocalQuickFix> = mutableListOf()
//        if(isNotInIgnore()){
//            result += AddLambdaToIgnoreQuickFix()
//        }
//        if(isNotInCallThough()){
//            result += AddLambdaToCallthoughQuickFix()
//        }
    return result.toTypedArray()
}

fun KtCallExpression.quickFixesFor(
    uncaughtExceptions: List<KtPsiClass>,
    state: IncrementalExceptionCheckerState
): Array<LocalQuickFix> {

    return arrayOf()

}

fun IncrementalExceptionCheckerState.findParentScope(from: KtElement): KtElement? {
    //TODO not sure this is accurate enough.. since there might be some more complex cases
    //Eg: functions in functions with lambdas in between......
    val lambdaScopes: KtLambdaExpression? = containingLambdas.firstOrNull { it: KtLambdaExpression ->
        it.isParent()
    }
    if (lambdaScopes != null) {
        return lambdaScopes
    }
    return from.firstParentByOrNull { it: KtElement ->
        it is KtFunction || it is KtProperty || it is KtPropertyDelegate
    }
}

fun KtLambdaExpression.isParent(): Boolean {
    val isCallThough: Boolean = CallThoughRepo(project).isLambdaCallThough(this)
    return !isCallThough
}
