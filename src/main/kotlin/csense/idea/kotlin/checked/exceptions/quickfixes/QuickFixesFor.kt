package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import csense.idea.kotlin.checked.exceptions.inspections.*
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
    val result: Array<LocalQuickFix> = arrayOf()
    if (state.isParentAFunction(from = this) || state.isParentAProperty(from = this) || state.isParentADelegation(from = this)) {
        result += addThrowsTypeQuickFix(this, uncaughtExceptions, state)
    }

    if (state.lastLambda != null) {
//        if(isNotInIgnore()){
//            result += AddLambdaToIgnoreQuickFix()
//        }
//        if(isNotInCallThough()){
//            result += AddLambdaToCallthoughQuickFix()
//        }
    }
    return result
}

fun KtCallExpression.quickFixesFor(
    uncaughtExceptions: List<KtPsiClass>,
    state: IncrementalExceptionCheckerState
): Array<LocalQuickFix> {

    return arrayOf()

}


fun IncrementalExceptionCheckerState.isParentAFunction(from: KtElement): Boolean {

}


fun IncrementalExceptionCheckerState.isParentAProperty(from: KtElement): Boolean {

}


fun IncrementalExceptionCheckerState.isParentADelegation(from: KtElement): Boolean {
    if (lastLambda != null) {
        //
        return false
    }
}

fun KtLambdaExpression.isParent(): Boolean {
    val isCallThough: Boolean = CallThoughRepo(project).isLambdaCallThough(this)
    return !isCallThough
}