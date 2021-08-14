package csense.idea.kotlin.checked.exceptions.cache

import csense.idea.base.bll.kotlin.resolveMainReference
import csense.idea.kotlin.checked.exceptions.bll.throwsTypesIfFunction
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.uast.UClass

object SharedMethodThrowingCache {

    fun throwsTypes(exp: KtCallExpression): List<UClass> {
        return resolveThrows(exp)

    }

    private fun resolveThrows(exp: KtCallExpression): List<UClass> {
        val functionResolved = exp.resolveMainReference() ?: return listOf()
        //Does it throw ? (if not just break)
        return functionResolved.throwsTypesIfFunction(exp) ?: return listOf()
    }
}
