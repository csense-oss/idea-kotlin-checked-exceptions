package csense.idea.kotlin.checked.exceptions.quickfixes.selectors

import com.intellij.codeInspection.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.csense.*
import csense.idea.kotlin.checked.exceptions.quickfixes.add.*
import csense.idea.kotlin.checked.exceptions.quickfixes.wrap.*
import csense.idea.kotlin.checked.exceptions.repo.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import csense.kotlin.extensions.*
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
        appendQuickFixesForExpression(state, result)

        val firstBlockingLambda: LambdaArgumentLookup? = state.containingLambdas.selectLastOrNull { it: KtLambdaExpression ->
            val lookup: LambdaArgumentLookup = it.toLamdaArgumentLookup() ?: return@selectLastOrNull null
            val isBlocking: Boolean = !callThoughRepo.isLambdaCallThough(lookup)
            return@selectLastOrNull isBlocking.map(ifTrue = lookup, ifFalse = null)
        }

        if (firstBlockingLambda != null) {
            appendQuickFixesForContainingLambdas(result, firstBlockingLambda)
            return
        }

        AddThrowsTypeToSelector.tryAddThrowsTypesAnnotations(
            parent = state.parentScope,
            result = result, uncaughtExceptions = uncaughtExceptions,
            kotlinThrowable = kotlinThrowable
        )
    }

    private fun appendQuickFixesForContainingLambdas(
        result: MutableList<LocalQuickFix>,
        blockingLambda: LambdaArgumentLookup
    ) {
        if (!ignoreRepo.isLambdaIgnoreExceptions(blockingLambda)) {
            result += AddLambdaToIgnoreQuickFix(blockingLambda)
        }
        if(!callThoughRepo.isLambdaCallThough(blockingLambda)) {
            result += AddLambdaToCallthoughQuickFix(blockingLambda)
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