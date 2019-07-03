package csense.idea.kotlin.checked.exceptions.ignore

import org.jetbrains.kotlin.psi.*

class IgnoreInMemory {

    fun isArgumentMarkedAsIgnore(main: KtFunction, name: String): Boolean {
        val lookingFor = main.fqName?.asString()
                ?: return false
        return IgnoreStorage.getEntries(main.project).any {
            it.fullName == lookingFor && it.parameterName == name
        }
    }

    fun isArgumentNotMarkedAsIgnore(main: KtFunction, name: String): Boolean {
        return !isArgumentMarkedAsIgnore(main, name)
    }
}