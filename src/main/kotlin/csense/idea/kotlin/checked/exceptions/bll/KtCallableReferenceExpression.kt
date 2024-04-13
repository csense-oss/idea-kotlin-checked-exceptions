package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import org.jetbrains.kotlin.psi.*

fun KtCallableReferenceExpression.throwsTypesForSettings(): List<KtPsiClass> {
    return resolveKtPsiFunctionOrNull()?.throwsTypesForSettings() ?: emptyList()
}