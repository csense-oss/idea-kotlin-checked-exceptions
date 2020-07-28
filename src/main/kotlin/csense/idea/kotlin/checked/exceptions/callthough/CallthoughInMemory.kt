package csense.idea.kotlin.checked.exceptions.callthough

import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*

object CallthoughInMemory {
    
    private val isEnabled: Boolean
        get() = Settings.useCallThoughFile
    
    fun isArgumentMarkedAsCallthough(main: KtFunction, name: String): Boolean {
        if (!isEnabled) {
            return false
        }
        val lookingFor = main.fqName?.asString()
                ?: return false
        return CallthoughStorage.getEntries(main.project).any {
            it.fullName == lookingFor && it.parameterName == name
        }
    }
    
    fun isArgumentNotMarkedAsCallthough(main: KtFunction, name: String): Boolean {
        return !isArgumentMarkedAsCallthough(main, name)
    }
}