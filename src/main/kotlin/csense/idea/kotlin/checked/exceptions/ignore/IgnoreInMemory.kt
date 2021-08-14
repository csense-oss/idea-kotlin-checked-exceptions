package csense.idea.kotlin.checked.exceptions.ignore

import csense.idea.base.UastKtPsi.getKotlinFqNameString
import csense.idea.kotlin.checked.exceptions.settings.Settings
import org.jetbrains.kotlin.psi.KtFunction

object IgnoreInMemory {

    private val isEnabled: Boolean
        get() = Settings.useIgnoreFile

    fun isArgumentMarkedAsIgnore(main: KtFunction, parameterName: String): Boolean {
        val mainFqName = main.getKotlinFqNameString() ?: return false
        if (isInKotlinStdLib(mainFqName, parameterName)) {
            return true
        }
        if (!isEnabled) {
            return false
        }
        return IgnoreStorage.contains(mainFqName, parameterName, main.project)
    }

    fun isInKotlinStdLib(fqName: String, paramName: String): Boolean {
        return knownKotlinFunctions[fqName] == paramName
    }

    val knownKotlinFunctions: HashMap<String, String> = hashMapOf(
        Pair("kotlin.runCatching","block")
    )
}