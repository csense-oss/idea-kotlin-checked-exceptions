package csense.idea.kotlin.checked.exceptions

import com.intellij.codeInspection.InspectionToolProvider

class CheckedExceptionsInspectionProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> {
        return arrayOf(CheckedExceptionsInspection::class.java)
    }
}
