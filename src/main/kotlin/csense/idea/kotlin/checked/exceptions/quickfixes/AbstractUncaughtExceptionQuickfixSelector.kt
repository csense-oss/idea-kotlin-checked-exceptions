package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

abstract class AbstractUncaughtExceptionQuickfixSelector(
    element: KtElement,
    val uncaughtExceptions: List<KtPsiClass>
) {
    val kotlinThrowable: KtPsiClass? by lazy {
        ProjectClassResolutionInterface.getOrCreate(element.project).kotlinOrJavaThrowable
    }

    val haveMultipleUncaughtTypes: Boolean = uncaughtExceptions.size > 1

    fun computeQuickfixes(
        state: IncrementalExceptionCheckerState
    ): Array<LocalQuickFix> {
        val results: MutableList<LocalQuickFix> = mutableListOf()
        appendQuickFixesFor(state = state, results)
        return results.toTypedArray()
    }

    abstract fun appendQuickFixesFor(
        state: IncrementalExceptionCheckerState,
        result: MutableList<LocalQuickFix>
    )

}