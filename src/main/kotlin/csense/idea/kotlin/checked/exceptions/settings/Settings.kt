package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.ide.util.*
import com.intellij.lang.annotation.*

object Settings {
    
    
    private const val settingsPrefixed = "CsenseCheckedExceptionKotlin"
    
    private val backend by lazy {
        PropertiesComponent.getInstance()
    }
    
    
    private const val shouldHighlightCheckedExceptionsName = settingsPrefixed + "shouldHighlightCheckedExceptions"
    var shouldHighlightCheckedExceptions: Boolean
        get() = backend.getBoolean(shouldHighlightCheckedExceptionsName, true)
        set(value) = backend.setValue(shouldHighlightCheckedExceptionsName, value, true)
    
    
    private const val shouldHighlightThrowsExceptionsName = settingsPrefixed + "shouldHighlightThrowsExceptions"
    var shouldHighlightThrowsExceptions: Boolean
        get() = backend.getBoolean(shouldHighlightThrowsExceptionsName, true)
        set(value) = backend.setValue(shouldHighlightThrowsExceptionsName, value, true)
    
    
    private const val maxDepthName = settingsPrefixed + "maxDepthName"
    var maxDepth: Int
        get() = backend.getInt(maxDepthName, 10)
        set(value) = backend.setValue(maxDepthName, maxOf(value, 1), 10)
    
    private const val throwsInsideOfFunctionSeverityName = settingsPrefixed + "throwsInsideOfFunctionSeverity"
    var throwsInsideOfFunctionSeverity: HighlightSeverity
        get() = backend.getValue(throwsInsideOfFunctionSeverityName)?.let { name ->
            HighlightSeverity.DEFAULT_SEVERITIES.firstOrNull {
                it.name == name
            }
        } ?: HighlightSeverity.WARNING
        set(newValue) {
            val valueToSave = newValue.name
            backend.setValue(throwsInsideOfFunctionSeverityName, valueToSave)
        }
}