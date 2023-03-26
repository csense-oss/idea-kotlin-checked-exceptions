package csense.idea.kotlin.checked.exceptions.bll.ignore

import csense.idea.base.bll.kotlin.*

object IgnoreInMemory {
    fun isInKotlinStdLib(lookup: LambdaArgumentLookup): Boolean {
        val fqName: String = lookup.parentFunctionFqName ?: return false
        val paramName: String = lookup.parameterName ?: return false
        return knownKotlinFunctions[fqName] == paramName
    }

    val knownKotlinFunctions: HashMap<String, String> = hashMapOf(
        Pair("kotlin.runCatching", "block")
    )
}