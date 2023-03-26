package csense.idea.kotlin.checked.exceptions.bll.callthough

import com.intellij.openapi.project.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
import csense.idea.kotlin.checked.exceptions.builtin.callthough.*
import org.jetbrains.kotlin.psi.*

class CallThoughRepo(
    project: Project
) {
    private val storage: CallthoughStorage? by lazy {
        CallthoughStorage.forProjectOrNull(project)
    }

    fun isLambdaCallThough(lambda: KtLambdaExpression): Boolean {
        val lookup: LambdaArgumentLookup = lambda.toLamdaArgumentLookup() ?: return false
        val isBuiltInCallThough: Boolean = KotlinCallThough.contains(lookup)
        if (isBuiltInCallThough) {
            return true
        }
        if (lookup.isCallInPlace()) {
            return true
        }
        return isLambdaCallThoughInStorage(lookup = lookup)
    }

    private fun isLambdaCallThoughInStorage(
        lookup: LambdaArgumentLookup
    ): Boolean {
        val entry: CallthoughEntry = entryFrom(lookup) ?: return false
        val storage: CallthoughStorage = storage ?: return false
        return storage.contains(entry)
    }

    private fun entryFrom(
        lookup: LambdaArgumentLookup
    ): CallthoughEntry? {
        val fqName: String = lookup.parentFunction.getKotlinFqNameString() ?: return null
        val parameterName: String = lookup.parameterToValueExpression.parameter.name ?: return null
        return CallthoughEntry(fqName, parameterName)
    }
}