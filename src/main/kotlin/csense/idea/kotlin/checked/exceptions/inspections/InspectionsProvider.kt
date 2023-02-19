package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*

class InspectionsProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<out LocalInspectionTool>> = arrayOf(
        IncrementalCheckedExceptionInspection::class.java
    )
}
