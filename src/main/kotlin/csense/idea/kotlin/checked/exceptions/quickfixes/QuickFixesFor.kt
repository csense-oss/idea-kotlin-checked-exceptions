package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import org.jetbrains.kotlin.psi.*

fun KtElement.quickFixesFor(
    state: IncrementalExceptionCheckerState,
    nonCaughtExceptions: List<KtPsiClass>
): Array<LocalQuickFix> = when (this) {
    is KtThrowExpression -> quickFixesFor(state, nonCaughtExceptions)
    is KtCallExpression -> quickFixesFor(state, nonCaughtExceptions)
    else -> arrayOf()
}

fun KtThrowExpression.quickFixesFor(
    state: IncrementalExceptionCheckerState,
    nonCaughtExceptions: List<KtPsiClass>
): Array<LocalQuickFix> {

    return arrayOf()
}

fun KtCallExpression.quickFixesFor(
    state: IncrementalExceptionCheckerState,
    nonCaughtExceptions: List<KtPsiClass>
): Array<LocalQuickFix> {

    return arrayOf()

}

