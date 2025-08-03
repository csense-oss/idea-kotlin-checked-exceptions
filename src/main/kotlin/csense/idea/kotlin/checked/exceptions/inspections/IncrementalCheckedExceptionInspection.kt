package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeHighlighting.*
import com.intellij.codeInspection.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
import csense.idea.base.module.*
import csense.idea.base.visitors.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.settings.*
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

    override fun getDefaultLevel(): HighlightDisplayLevel {
        return HighlightDisplayLevel.WARNING
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): KtVisitorVoid {

        val visitor = IncrementalExceptionCheckerVisitor(
            holder = holder,
            project = holder.project
        )
        val callVisitor: (KtElement) -> Unit = { it: KtElement ->
            it.accept(
                /* visitor = */ visitor,
                /* data = */ IncrementalExceptionCheckerState.empty
            )
        }

        return SettingsBasedNamedFunctionOrCustomPropertyCodeVisitor(
            onFunctionNamed = callVisitor,
            onPropertyWithInnerCode = callVisitor
        )
    }
}

class SettingsBasedNamedFunctionOrCustomPropertyCodeVisitor(
    onFunctionNamed: (KtNamedFunction) -> Unit,
    onPropertyWithInnerCode: (KtProperty) -> Unit
) : NamedFunctionOrCustomPropertyCodeVisitor(
    onFunctionNamed,
    onPropertyWithInnerCode
) {
    override fun visitNamedFunction(function: KtNamedFunction) {
        if (shouldIgnoreFile(function.containingFile)) {
            return
        }
        if (shouldIgnoreFunction(function)) {
            return
        }
        super.visitNamedFunction(function)
    }

    private fun shouldIgnoreFunction(function: KtNamedFunction): Boolean {
        if (!Settings.ignoreDeprecated) {
            return false
        }
        return function.isAnnotatedDeprecated()
    }

    override fun visitProperty(property: KtProperty) {
        if (shouldIgnoreFile(property.containingFile)) {
            return
        }
        super.visitProperty(property)
    }

    private fun shouldIgnoreFile(file: PsiFile): Boolean {
        return (file.isInTestModule() && Settings.ignoreTestExceptions) || file.isKtFileStubbed()
    }
}