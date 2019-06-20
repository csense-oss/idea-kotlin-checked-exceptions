package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.codeInspection.*
import com.intellij.ide.util.*

object Settings {

    private const val settingsPrefixed = "CsenseCheckedExceptionKotlin"

    private val backend by lazy {
        PropertiesComponent.getInstance()
    }

    private const val checkedExceptionSeverityName = settingsPrefixed + "checkedExceptionSeverity"
    var checkedExceptionSeverity: ProblemHighlightType
        get() = csense.idea.kotlin.checked.exceptions.bll.enumFromOr(backend.getInt(
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

}