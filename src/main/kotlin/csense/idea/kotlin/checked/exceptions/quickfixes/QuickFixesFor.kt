package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.visitors.*
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

    state.onParentScopeAnnotateAble { parentScope: KtAnnotated ->
        return@quickFixesFor arrayOf(
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
    val result: MutableList<LocalQuickFix> = mutableListOf()


    //TODO in lambda?! hmm..
    if (state.containingLambdas.isNotEmpty()) {
        //TODO YA........ call though, ignores etc.
    }

    val containingTry: KtTryExpression? = state.containingTryExpression

    result += when (containingTry) {
        null -> WrapInTryCatchQuickFix(namedFunction = this, uncaughtExceptions = uncaughtExceptions)
        else -> AddCatchClausesQuickFix(tryExpression = containingTry, uncaughtExceptions = uncaughtExceptions)
    }

    return result.toTypedArray()
}

inline fun IncrementalExceptionCheckerState.onParentScopeAnnotateAble(
    action: (KtAnnotated) -> Unit
) {
    val parentScope: KtElement = parentScope ?: return
    if (parentScope is KtAnnotated) {
        action(parentScope)
    }
}
