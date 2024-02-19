package csense.idea.kotlin.checked.exceptions.repo

import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import csense.idea.base.bll.kotlin.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import csense.idea.kotlin.checked.exceptions.bll.files.*
import csense.idea.kotlin.checked.exceptions.builtin.callthough.*
import csense.kotlin.extensions.*

class CallThoughRepo(
    project: Project
) {
    private val storage: CachedFqNameFunctionParameterStorage? by lazy {
        CachedFqNameFunctionParameterStorage.forProjectOrNull(project = project, fileName = callthoughProjectFileName)
    }

    fun isLambdaCallThough(lambda: LambdaArgumentLookup): Boolean = when {
        isBuiltInCallThough(lambda) -> true
        lambda.isCallInPlace() -> true
        hasRethrowsExceptionAnnotationOnParameter(lambda) -> true
        else -> isLambdaCallThoughInStorage(lookup = lambda)
    }

    private fun hasRethrowsExceptionAnnotationOnParameter(
        lookup: LambdaArgumentLookup
    ): Boolean {
        return AnnotationsRepo.isAnyRethrowsExceptions(lookup)
    }

    private fun isBuiltInCallThough(lookup: LambdaArgumentLookup): Boolean {
        //TODO combine these...
        return KotlinCallThough.contains(lookup) ||
                CallthoughInMemory.isKnownKotlinFunction(lookup)
    }

    private fun isLambdaCallThoughInStorage(
        lookup: LambdaArgumentLookup
    ): Boolean {
        val entry: CachedFqNameFunctionParameter = lookup.toEntryOrNull() ?: return false
        return storage?.contains(entry) ?: false
    }


    fun addEntry(fqName: String, parameterName: String) {
        storage?.addEntry(CachedFqNameFunctionParameter(fqName = fqName, parameterName = parameterName))
        tryAndLog {
            VirtualFileManager.getInstance().asyncRefresh(null)
        }
    }

    @Throws(Throwable::class)
    fun reload() {
        storage?.reload()
    }

    companion object {
        const val callthoughProjectFileName: String = ".callthough.throws"
    }
}