package csense.idea.kotlin.checked.exceptions.bll.callthough

import com.intellij.openapi.project.*
import csense.idea.base.bll.kotlin.*
import csense.idea.kotlin.checked.exceptions.bll.files.*
import csense.idea.kotlin.checked.exceptions.builtin.callthough.*
import org.jetbrains.kotlin.psi.*

class CallThoughRepo(
    project: Project
) {
    private val storage: CachedFqNameFunctionParameterStorage? by lazy {
        CachedFqNameFunctionParameterStorage.forProjectOrNull(project = project, fileName = callthoughProjectFileName)
    }

    fun isLambdaCallThough(lambda: LambdaArgumentLookup): Boolean {
        if (isBuiltInCallThough(lambda)) {
            return true
        }

        if (lambda.isCallInPlace()) {
            return true
        }

        if (hasRethrowsExceptionAnnotationOnParameter(lambda)) {
            return true
        }

        return isLambdaCallThoughInStorage(lookup = lambda)
    }

    private fun hasRethrowsExceptionAnnotationOnParameter(
        lookup: LambdaArgumentLookup
    ): Boolean {

        return lookup.parameterToValueExpression.parameterValueAnnotations.isAnyRethrowsExceptions()
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
    }

    companion object {
        const val callthoughProjectFileName: String = ".callthough.throws"
    }
}

fun List<KtAnnotationEntry>.isAnyRethrowsExceptions(): Boolean = any { it: KtAnnotationEntry ->
    it.fqName() == "csense.kotlin.annotations.exceptions.RethrowsExceptions"
}