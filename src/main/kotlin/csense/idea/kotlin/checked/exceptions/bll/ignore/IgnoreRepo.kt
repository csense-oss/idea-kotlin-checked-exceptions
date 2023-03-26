package csense.idea.kotlin.checked.exceptions.bll.ignore

import com.intellij.openapi.project.*
import csense.idea.base.bll.kotlin.*
import csense.idea.kotlin.checked.exceptions.bll.files.*
import org.jetbrains.kotlin.psi.*

class IgnoreRepo(
    project: Project
) {

    private val storage: CachedFqNameFunctionParameterStorage? by lazy {
        CachedFqNameFunctionParameterStorage.forProjectOrNull(project = project, fileName = ignoreFileName)
    }

    fun isLambdaIgnoreExceptions(lambda: KtLambdaExpression): Boolean {
        val lookup: LambdaArgumentLookup = lambda.toLamdaArgumentLookup() ?: return false

        if (IgnoreInMemory.isInKotlinStdLib(lookup)) {
            return true
        }

        if (lookup.isCallInPlace()) {
            return false
        }

        return isLambdaIgnoreInStorage(lookup = lookup)
    }

    private fun isLambdaIgnoreInStorage(
        lookup: LambdaArgumentLookup
    ): Boolean {
        val entry: CachedFqNameFunctionParameter = lookup.toEntryOrNull() ?: return false
        return storage?.contains(entry) ?: false
    }

    fun addEntry(fqName: String, parameterName: String) {
        storage?.addEntry(CachedFqNameFunctionParameter(fqName = fqName, parameterName = parameterName))
    }

    companion object {
        const val ignoreFileName: String = ".ignore.throws"
    }
}

