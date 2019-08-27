package csense.idea.kotlin.checked.exceptions.cache

import csense.idea.kotlin.checked.exceptions.bll.*
import csense.kotlin.ds.cache.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.js.resolve.diagnostics.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*

object SharedMethodThrowingCache {
    private val inMemoryCallCache = SimpleLRUCache<String, CachedFunctionLookup>(500)
    //todo provide KtFunction...

    fun throwsTypes(exp: KtCallExpression): List<UClass> {
        val ktPsi = exp.resolveToCall()?.resultingDescriptor?.findPsi() as? KtElement ?: return listOf()
        val lastModified = ktPsi.getModificationStamp()
        val fullName = ktPsi.getKotlinFqName()?.asString() ?: "-1"
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
            if (fullName != "") {
                inMemoryCallCache.put(fullName, CachedFunctionLookup(lastModified, throws))
            }
            throws
        }
    }

    private fun resolveThrows(exp: KtCallExpression): List<UClass> {
        val functionResolved = exp.resolveMainReference() ?: return listOf()
        //Does it throw ? (if not just break)
        return functionResolved.throwsTypesIfFunction() ?: return listOf()
    }

    data class CachedFunctionLookup(
            val lastModifiedTimeStamp: Long,
            val throwsTypes: List<UClass>

    )
}