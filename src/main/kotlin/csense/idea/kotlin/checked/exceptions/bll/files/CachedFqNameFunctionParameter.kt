package csense.idea.kotlin.checked.exceptions.bll.files

import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*

data class CachedFqNameFunctionParameter(
    val fqName: String,
    val parameterName: String
) {
    companion object
}

fun CachedFqNameFunctionParameter.Companion.fromLineOrNull(line: String): CachedFqNameFunctionParameter? {
    val raw: List<String> = line.split(delimiters = arrayOf(" "))
    if (raw.size != 2) {
        return null
    }
    val (fqName: String, name: String) = raw
    return CachedFqNameFunctionParameter(fqName, name)
}

fun CachedFqNameFunctionParameter.serializeToString(): String {
    return "$fqName $parameterName"
}

fun LambdaArgumentLookup.toEntryOrNull(): CachedFqNameFunctionParameter? {
    val fqName: String = parentFunction.getKotlinFqNameString() ?: return null
    val parameterName: String = parameterToValueExpression.parameter.name ?: return null
    return CachedFqNameFunctionParameter(fqName = fqName, parameterName = parameterName)
}