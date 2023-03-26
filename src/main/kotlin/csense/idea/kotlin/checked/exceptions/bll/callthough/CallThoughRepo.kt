package csense.idea.kotlin.checked.exceptions.bll.callthough

import com.intellij.openapi.project.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
import csense.idea.kotlin.checked.exceptions.bll.files.*
import csense.idea.kotlin.checked.exceptions.builtin.callthough.*
import org.jetbrains.kotlin.psi.*

class CallThoughRepo(
    project: Project
) {
    private val storage: CachedFqNameFunctionParameterStorage? by lazy {
        CachedFqNameFunctionParameterStorage.forProjectOrNull(project = project, fileName = callthoughProjectFileName)
    }

    fun isLambdaCallThough(lambda: KtLambdaExpression): Boolean {
        val lookup: LambdaArgumentLookup = lambda.toLamdaArgumentLookup() ?: return false

        if (isBuiltInCallThough(lookup)) {
            return true
        }

        if (lookup.isCallInPlace()) {
            return true
        }

        if (hasRethrowsExceptionAnnotationOnParameter(lookup)) {
            return true
        }

        return isLambdaCallThoughInStorage(lookup = lookup)
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
    it.getKotlinFqNameString() == "csense.kotlin.annotations.exceptions.RethrowsExceptions"
}