package csense.idea.kotlin.checked.exceptions.cache

import com.intellij.psi.PsiMethod
import csense.idea.base.bll.kotlin.resolveMainReference
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.kotlin.datastructures.collections.SimpleLRUCache
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*

object SharedMethodThrowingCache {
    private val inMemoryCallCache = SimpleLRUCache<String, CachedFunctionLookup>(500)
    //todo provide KtFunction...

    fun clear() {
        inMemoryCallCache.clear()
    }

    fun throwsTypes(exp: KtCallExpression): List<UClass> {
        val (fullName, lastModified) = handleExp(exp) ?: return emptyList()
        val cached = inMemoryCallCache.getOrRemove(
                fullName
        ) { _: String, lookup: CachedFunctionLookup ->
            lastModified == lookup.lastModifiedTimeStamp
        }
        return if (cached != null) {
            cached.throwsTypes
        } else {
            //lets find out if it throws.
            val throws = resolveThrows(exp)
            val hasAnyGeneric = exp.hasAnyGenerics()
            if (fullName != "" && !hasAnyGeneric) {
//                inMemoryCallCache.put(fullName, CachedFunctionLookup(lastModified, throws))
            }
            throws
        }
    }

    private fun handleExp(exp: KtCallExpression): ResolvedExp? = when (
        val funcCalled = exp.resolveMainReference()) {
        is PsiMethod -> {
            ResolvedExp(
                    funcCalled.getKotlinFqName()?.asString() ?: "-1",
                    0
            )
        }
        is KtFunction -> {
            ResolvedExp(
                    funcCalled.getKotlinFqName()?.asString() ?: "-1",
                    funcCalled.getModificationStamp()

            )
        }
        else -> null
    }

    private data class ResolvedExp(val fullName: String, val lastModifiedTimeStamp: Long)

    private fun resolveThrows(exp: KtCallExpression): List<UClass> {
        val functionResolved = exp.resolveMainReference() ?: return listOf()
        //Does it throw ? (if not just break)
        return functionResolved.throwsTypesIfFunction(exp) ?: return listOf()
    }

    data class CachedFunctionLookup(
            val lastModifiedTimeStamp: Long,
            val throwsTypes: List<UClass>

    )
}

fun KtCallExpression.hasAnyGenerics(): Boolean {
    return this.typeArguments.isNotEmpty()
}