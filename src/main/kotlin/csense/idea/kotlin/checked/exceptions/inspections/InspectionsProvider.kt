package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*

class InspectionsProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> {
        return arrayOf(
                CheckedExceptionsInspection::class.java)
    }
}
