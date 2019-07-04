package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.codeInspection.*
import com.intellij.ide.util.*
import csense.kotlin.extensions.*;

object Settings {


    private const val settingsPrefixed = "CsenseCheckedExceptionKotlin"

    private val backend by lazy {
        PropertiesComponent.getInstance()
    }

    private const val checkedExceptionSeverityName = settingsPrefixed + "checkedExceptionSeverity"
    var checkedExceptionSeverity: ProblemHighlightType
        get() = enumFromOr(backend.getInt(
                checkedExceptionSeverityName,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING.ordinal),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
        set(value) {
            backend.setValue(checkedExceptionSeverityName, value.ordinal, -1)
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
}