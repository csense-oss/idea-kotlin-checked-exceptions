package csense.idea.kotlin.checked.exceptions.quickfixes.selectors

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.quickfixes.add.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import csense.kotlin.extensions.collections.list.*
import org.jetbrains.kotlin.psi.*

class KtThrowExpressionQuickFixSelector(
    throwExpression: KtThrowExpression,
    uncaughtExceptions: List<KtPsiClass>
) : AbstractUncaughtExceptionQuickfixSelector(
    element = throwExpression,
    uncaughtExceptions = uncaughtExceptions
) {
    override fun appendQuickFixesFor(
        state: IncrementalExceptionCheckerState,
        result: MutableList<LocalQuickFix>
    ) {
        val parent: KtElement? = state.parentScope
        AddThrowsTypeToSelector.tryAddThrowsTypesAnnotations(
            parent = parent,
            result = result,
            uncaughtExceptions = uncaughtExceptions,
            kotlinThrowable = kotlinThrowable
        )
    }


}

object AddThrowsTypeToSelector {

    fun tryAddThrowsTypesAnnotations(
        parent: KtElement?,
        result: MutableList<LocalQuickFix>,
        uncaughtExceptions: List<KtPsiClass>,
        kotlinThrowable: KtPsiClass?
    ) {
        if (parent is KtAnnotated) {
            addThrowsTypesAnnotations(
                parentScope = parent,
                result = result,
                uncaughtExceptions = uncaughtExceptions,
                kotlinThrowable
            )
        }
    }

    fun addThrowsTypesAnnotations(
        parentScope: KtAnnotated,
        result: MutableList<LocalQuickFix>,
        uncaughtExceptions: List<KtPsiClass>,
        kotlinThrowable: KtPsiClass?
    ) {
        addAllThrowsTypesTo(parentScope = parentScope, result = result, uncaughtExceptions)
        if (uncaughtExceptions.doesNotContain(kotlinThrowable)) {
            addKotlinThrowTypeTo(parentScope = parentScope, result = result, kotlinThrowable = kotlinThrowable)
        }
    }

    private fun addAllThrowsTypesTo(
        parentScope: KtAnnotated,
        result: MutableList<LocalQuickFix>,
        uncaughtExceptions: List<KtPsiClass>
    ) {
        result += AddThrowsTypesQuickFix(
            toExpression = parentScope,
            missingThrowsTypes = uncaughtExceptions
        )
    }

    private fun addKotlinThrowTypeTo(
        parentScope: KtAnnotated,
        result: MutableList<LocalQuickFix>,
        kotlinThrowable: KtPsiClass?
    ) {
        val kotlinThrowable: KtPsiClass = kotlinThrowable ?: return
        result += AddThrowsTypesQuickFix(
            toExpression = parentScope,
            missingThrowsTypes = listOf(kotlinThrowable)
        )
    }
}