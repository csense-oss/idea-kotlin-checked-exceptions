package csense.idea.kotlin.checked.exceptions.cache

import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*

object SharedMethodThrowingCache {
    private val inMemoryCallCache = SimpleLRUCache<String, CachedFunctionLookup>(500)
//todo provide KtFunction...
    fun throwsTypes(exp: KtCallExpression, fullName: String, lastModified: Long): List<UClass> {

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
//            if (fullName != "") {
//                inMemoryCallCache.put(fullName, CachedFunctionLookup(lastModified, throws))
//            }
            throws
        }
    }

    private fun resolveThrows(exp: KtCallExpression): List<UClass> {
        val functionResolved = exp.resolveMainReference() ?: return listOf()
        //Does it throw ? (if not just break)
        return functionResolved.throwsTypesIfFunction() ?: return listOf()
    }

    fun throwsTypes(exp: KtCallExpression): List<UClass> =
            throwsTypes(exp, (exp.resolveMainReference() as? KtNamedFunction)?.fqName?.asString()
                    ?: "", (exp.resolveMainReference() as? KtFunction)?.getModificationStamp() ?: -1)

    data class CachedFunctionLookup(
            val lastModifiedTimeStamp: Long,
            val throwsTypes: List<UClass>

    )
}