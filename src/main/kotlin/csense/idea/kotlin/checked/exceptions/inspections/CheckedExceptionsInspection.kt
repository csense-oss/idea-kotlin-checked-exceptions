package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInspection.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.idea.inspections.*
import org.jetbrains.kotlin.psi.*
import kotlin.system.*


class CheckedExceptionsInspection : AbstractKotlinInspection() {

    private val ignoreInMemory by lazy {
        IgnoreInMemory()
    }

    private fun getMaxDepth(): Int {
        return Settings.maxDepth
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
            val time = measureTimeMillis {
                val functionResolved = namedFunction.resolveMainReference() ?: return@callExpressionVisitor
                //Does it throw ? (if not just break)
                val throwsTypes = functionResolved.throwsTypesIfFunction() ?: return@callExpressionVisitor
                //is there any try catch and if not, is the container marked as throws ? if not then its an error.
                val lambdaContext = namedFunction.getPotentialContainingLambda()
                if (namedFunction.isNotWrappedInTryCatch()
                        && !namedFunction.isContainingFunctionMarkedAsThrows()
                        && !namedFunction.isContainedInFunctionCatchingOrIgnored(ignoreInMemory, getMaxDepth())
                ) {
                    holder.registerProblem(
                            namedFunction,
                            "This call throws, so you should handle it with try catch, or declare that this method throws.\n${throwsTypes.joinToString(", ")}",
                            Settings.checkedExceptionSeverity,
                            *(createQuickFixes(namedFunction, throwsTypes)
                                    + getIgnoreQuickFixes(lambdaContext))
                    )
                }
            }
        }
    }

    private fun getIgnoreQuickFixes(lambdaContext: LambdaParameterData?): List<LocalQuickFix> {
        if (lambdaContext == null || ignoreInMemory.isArgumentMarkedAsIgnore(lambdaContext.main, lambdaContext.parameterName)) {
            return listOf()
        }
        //we either have it ignored or no lambda, or "not", thus we can add it.
        return listOf(AddLambdaToIgnoreQuickFix(lambdaContext.main, lambdaContext.parameterName))
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