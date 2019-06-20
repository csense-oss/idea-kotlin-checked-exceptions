package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.InspectionToolProvider

class CheckedExceptionsInspectionProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> {
        return arrayOf(CheckedExceptionsInspection::class.java)
    }
}
