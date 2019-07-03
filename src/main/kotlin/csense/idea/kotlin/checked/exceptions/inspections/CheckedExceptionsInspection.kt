package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInspection.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.idea.inspections.*
import org.jetbrains.kotlin.psi.*


class CheckedExceptionsInspection : AbstractKotlinInspection() {

    private val ignoreInMemory by lazy {
        IgnoreInMemory()
    }

    override fun getDisplayName(): String {
        return "Checked exceptions in kotlin"
    }

    override fun getStaticDescription(): String? {
        return "some desc"
    }

    override fun getDescriptionFileName(): String? {
        return "more desc ? "
    }

    override fun getShortName(): String {
        return "CheckedExceptionsKotlin"
    }

    override fun getGroupDisplayName(): String {
        return GroupNames.ERROR_HANDLING_GROUP_NAME
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean): KtVisitorVoid {

        return callExpressionVisitor { namedFunction: KtCallExpression ->
            //useful in debugging.
            //val callCode = namedFunction.text
            val functionResolved = namedFunction.resolveMainReference() ?: return@callExpressionVisitor
            //Does it throw ? (if not just break)
            val throwsTypes = functionResolved.throwsTypesIfFunction() ?: return@callExpressionVisitor
            //is there any try catch and if not, is the container marked as throws ? if not then its an error.
            if (namedFunction.isNotWrappedInTryCatch()
                    && !namedFunction.isContainingFunctionMarkedAsThrows()
                    && !namedFunction.isContainedInFunctionCatchingOrIgnored(ignoreInMemory)
            ) {
                holder.registerProblem(
                        namedFunction,
                        "This call throws, so you should handle it with try catch, or declare that this method throws.\n${throwsTypes.joinToString(", ")}",
                        Settings.checkedExceptionSeverity,
                        *createQuickFixes(namedFunction, throwsTypes)
                )
            }
        }
    }

    private fun createQuickFixes(namedFunction: KtCallExpression, exceptionTypes: List<String>): Array<LocalQuickFix> {
        return arrayOf(
                WrapInTryCatchQuickFix(namedFunction, exceptionTypes),
                DeclareFunctionAsThrowsQuickFix(namedFunction, exceptionTypes)
        )
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }
}