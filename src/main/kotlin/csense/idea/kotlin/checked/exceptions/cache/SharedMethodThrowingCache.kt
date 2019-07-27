package csense.idea.kotlin.checked.exceptions.cache

import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.psi.*

object SharedMethodThrowingCache {
    private val inMemoryCallCache = SimpleLRUCache<String, CachedFunctionLookup>(500)

    fun throwsTypes(exp: KtCallExpression, fullName: String, lastModified: Long): List<String> {

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
            inMemoryCallCache.put(fullName, CachedFunctionLookup(lastModified, throws))
            throws
        }
    }

    private fun resolveThrows(exp: KtCallExpression): List<String> {
        val functionResolved = exp.resolveMainReference() ?: return listOf()
        //Does it throw ? (if not just break)
        return functionResolved.throwsTypesIfFunction() ?: return listOf()
    }

    fun throwsTypes(exp: KtCallExpression): List<String> =
            throwsTypes(exp, exp.getKotlinFqName()?.toString() ?: "", exp.getModificationStamp())

    data class CachedFunctionLookup(
            val lastModifiedTimeStamp: Long,
            val throwsTypes: List<String>

    )
}