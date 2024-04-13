package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import org.jetbrains.kotlin.psi.*

fun KtCallExpression.throwsTypesForSettings(): List<KtPsiClass> {
    val forFunction: List<KtPsiClass> = resolveMainReferenceAsFunction()
        ?.throwsTypesForSettings()
        ?: listOf()

    return forFunction + resolveAllThrowingValueArguments()
}

fun KtCallExpression.resolveAllThrowingValueArguments(): List<KtPsiClass> {
    return valueArguments.flatMap { it: KtValueArgument? ->
        val argExpression: KtExpression = it?.getArgumentExpression() ?: return@flatMap emptyList()
        when (argExpression) {
            is KtCallableReferenceExpression -> argExpression.throwsTypesForSettings()
            is KtCallExpression -> argExpression.throwsTypesForSettings()
            else -> emptyList()
        }
    }
}