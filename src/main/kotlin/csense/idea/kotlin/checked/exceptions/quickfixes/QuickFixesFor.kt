package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.quickfixes.selectors.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

fun KtElement.quickFixesFor(
    uncaughtExceptions: List<KtPsiClass>,
    state: IncrementalExceptionCheckerState
): Array<LocalQuickFix> = when (this) {

    is KtThrowExpression -> KtThrowExpressionQuickFixSelector(
        throwExpression = this,
        uncaughtExceptions = uncaughtExceptions
    ).computeQuickfixes(state = state)

    is KtCallExpression -> KtCallExpressionQuickFixSelector(
        callExpression = this,
        uncaughtExceptions = uncaughtExceptions
    ).computeQuickfixes(state = state)

    else -> arrayOf()
}