package csense.idea.kotlin.checked.exceptions.ignore

import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*

object IgnoreInMemory {
    
    private val isEnabled: Boolean
        get() = Settings.useIgnoreFile
    
    fun isArgumentMarkedAsIgnore(main: KtFunction, name: String): Boolean {
        if (!isEnabled) {
            return false
        }
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