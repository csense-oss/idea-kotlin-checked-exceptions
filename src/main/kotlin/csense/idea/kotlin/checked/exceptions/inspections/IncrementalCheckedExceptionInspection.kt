package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import csense.idea.base.visitors.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class IncrementalCheckedExceptionInspection : LocalInspectionTool() {

    override fun getDisplayName(): String {
        return "Checked exceptions in kotlin"
    }

    override fun getShortName(): String {
        return "CheckedExceptionsKotlin"
    }

    override fun getGroupDisplayName(): String {
        return Constants.groupName
    }

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): KtVisitorVoid {
        val visitor = IncrementalExceptionCheckerVisitor(
            holder = holder,
            project = holder.project
        )
        return NamedFunctionOrDelegationVisitor(
            onFunctionNamed = { it: KtNamedFunction ->
                it.accept(
                    /* visitor = */ visitor,
                    /* data = */ IncrementalExceptionCheckerState.empty
                )
            },
            onPropertyDelegate = { it: KtPropertyDelegate ->
                it.accept(
                    /* visitor = */ visitor,
                    /* data = */ IncrementalExceptionCheckerState.empty
                )
            }
        )
    }
}
