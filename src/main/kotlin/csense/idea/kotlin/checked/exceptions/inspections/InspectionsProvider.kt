package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*

class InspectionsProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<out Class<out LocalInspectionTool>> {
        return arrayOf(
                IncrementalCheckedExceptionInspection::class.java
        )
    }
}
