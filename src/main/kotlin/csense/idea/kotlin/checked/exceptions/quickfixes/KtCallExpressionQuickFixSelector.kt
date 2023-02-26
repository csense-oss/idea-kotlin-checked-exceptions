package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class KtCallExpressionQuickFixSelector(
    private val callExpression: KtCallExpression,
    uncaughtExceptions: List<KtPsiClass>
) : AbstractUncaughtExceptionQuickfixSelector(
    element = callExpression,
    uncaughtExceptions = uncaughtExceptions
) {

    override fun appendQuickFixesFor(
        state: IncrementalExceptionCheckerState,
        result: MutableList<LocalQuickFix>
    ) {
        //TODO in lambda?! hmm..
        if (state.containingLambdas.isNotEmpty()) {
            //TODO YA........ call though, ignores etc.
        }

        val containingTry: KtTryExpression? = state.containingTryExpression
        if (containingTry == null) {
            addWrapInTryCatchFixes(result = result)
        } else {
            addCatchClauseFixes(
                tryExpression = containingTry,
                result = result
            )
        }
    }


    private fun addCatchClauseFixes(
        tryExpression: KtTryExpression,
        result: MutableList<LocalQuickFix>
    ) {
        addCatchClausForAllTypes(tryExpression = tryExpression, result = result)
        if (haveMultipleUncaughtTypes) {
            addCatchClausForKotlinThrowable(tryExpression = tryExpression, result = result)
        }
    }

    private fun addCatchClausForAllTypes(
        tryExpression: KtTryExpression,
        result: MutableList<LocalQuickFix>
    ) {
        result += AddCatchClausesQuickFix(tryExpression = tryExpression, uncaughtExceptions = uncaughtExceptions)
    }

    private fun addCatchClausForKotlinThrowable(
        tryExpression: KtTryExpression,
        result: MutableList<LocalQuickFix>
    ) {
        val kotlinThrowable: KtPsiClass = kotlinThrowable ?: return
        result += AddCatchClausesQuickFix(tryExpression = tryExpression, uncaughtExceptions = listOf(kotlinThrowable))
    }


    private fun addWrapInTryCatchFixes(
        result: MutableList<LocalQuickFix>
    ) {
        addWrapInTryCatchForAllTypes(result = result)
        if (haveMultipleUncaughtTypes) {
            addWrapInTryCatchForKotlinThrowable(result = result)
        }
    }

    private fun addWrapInTryCatchForAllTypes(
        result: MutableList<LocalQuickFix>
    ) {
        result += WrapInTryCatchQuickFix(namedFunction = callExpression, uncaughtExceptions = uncaughtExceptions)
    }

    private fun addWrapInTryCatchForKotlinThrowable(
        result: MutableList<LocalQuickFix>
    ) {
        val kotlinThrowable: KtPsiClass = kotlinThrowable ?: return
        result += WrapInTryCatchQuickFix(namedFunction = callExpression, uncaughtExceptions = listOf(kotlinThrowable))
    }

}