package csense.idea.kotlin.checked.exceptions.quickfixes.selectors

import com.intellij.codeInspection.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import csense.idea.kotlin.checked.exceptions.bll.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.add.*
import csense.idea.kotlin.checked.exceptions.quickfixes.wrap.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class KtCallExpressionQuickFixSelector(
    private val callExpression: KtCallExpression,
    uncaughtExceptions: List<KtPsiClass>
) : AbstractUncaughtExceptionQuickfixSelector(
    element = callExpression,
    uncaughtExceptions = uncaughtExceptions
) {

    private val ignoreRepo: IgnoreRepo by lazy {
        IgnoreRepo(callExpression.project)
    }

    private val callThoughRepo: CallThoughRepo by lazy {
        CallThoughRepo(callExpression.project)
    }

    override fun appendQuickFixesFor(
        state: IncrementalExceptionCheckerState,
        result: MutableList<LocalQuickFix>
    ) {
        if (state.containingLambdas.isNotEmpty()) {
            appendQuickFixesForContainingLambdas(state, result)
        }
        appendQuickFixesForExpression(state, result)
    }

    private fun appendQuickFixesForContainingLambdas(
        state: IncrementalExceptionCheckerState,
        result: MutableList<LocalQuickFix>
    ) {
        val lambda: LambdaArgumentLookup = state
            .containingLambdas
            .lastOrNull()
            ?.toLamdaArgumentLookup()
            ?: return

        if (!ignoreRepo.isLambdaIgnoreExceptions(lambda)) {
            addLambdaToIgnoreQuickFix(lambda = lambda, result = result)
        }
        if (!callThoughRepo.isLambdaCallThough(lambda)) {
            addLambdaToCallThoughQuickFix(lambda, result)
        }
    }

    private fun appendQuickFixesForExpression(
        state: IncrementalExceptionCheckerState,
        result: MutableList<LocalQuickFix>
    ) {
        val containingTry: KtTryExpression? = state.containingTryExpression
        if (containingTry != null) {
            addCatchClauseFixes(
                tryExpression = containingTry,
                result = result
            )
            return
        }
        addWrapInTryCatchFixes(result = result)
    }

    private fun addLambdaToIgnoreQuickFix(
        lambda: LambdaArgumentLookup,
        result: MutableList<LocalQuickFix>
    ) {
        result += AddLambdaToIgnoreQuickFix(lambda)
    }

    private fun addLambdaToCallThoughQuickFix(
        lambda: LambdaArgumentLookup,
        result: MutableList<LocalQuickFix>
    ) {
        result += AddLambdaToCallthoughQuickFix(lambda)
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