package csense.idea.kotlin.checked.exceptions.repo

import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import csense.idea.base.bll.kotlin.*
import csense.idea.kotlin.checked.exceptions.bll.files.*
import csense.idea.kotlin.checked.exceptions.bll.ignore.*
import csense.kotlin.extensions.*

class IgnoreRepo(
    project: Project
) {

    private val storage: CachedFqNameFunctionParameterStorage? by lazy {
        CachedFqNameFunctionParameterStorage.forProjectOrNull(project = project, fileName = ignoreFileName)
    }

    fun isLambdaIgnoreExceptions(lambda: LambdaArgumentLookup): Boolean = when {
        IgnoreInMemory.isInKotlinStdLib(lambda) -> true
        lambda.isCallInPlace() -> false
        else -> isLambdaIgnoreInStorage(lookup = lambda)
    }

    private fun isLambdaIgnoreInStorage(
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
        const val ignoreFileName: String = ".ignore.throws"
    }
}