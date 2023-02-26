package csense.idea.kotlin.checked.exceptions.quickfixes.selectors

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import csense.kotlin.extensions.collections.list.*
import org.jetbrains.kotlin.psi.*

class KtThrowExpressionQuickFixSelector(
    private val throwExpression: KtThrowExpression,
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
        if (parent is KtAnnotated) {
            addThrowsTypesAnnotations(parentScope = parent, result = result)
        }
    }

    private fun addThrowsTypesAnnotations(
        parentScope: KtAnnotated,
        result: MutableList<LocalQuickFix>
    ) {
        addAllThrowsTypesTo(parentScope = parentScope, result = result)
        if (uncaughtExceptions.doesNotContain(kotlinThrowable)) {
            addKotlinThrowTypeTo(parentScope, result)
        }
    }

    private fun addAllThrowsTypesTo(
        parentScope: KtAnnotated,
        result: MutableList<LocalQuickFix>
    ) {
        result += AddThrowsTypesQuickFix(
            toExpression = parentScope,
            missingThrowsTypes = uncaughtExceptions
        )
    }

    private fun addKotlinThrowTypeTo(
        parentScope: KtAnnotated,
        result: MutableList<LocalQuickFix>
    ) {
        val kotlinThrowable: KtPsiClass = kotlinThrowable ?: return
        result += AddThrowsTypesQuickFix(
            toExpression = parentScope,
            missingThrowsTypes = listOf(kotlinThrowable)
        )
    }

}