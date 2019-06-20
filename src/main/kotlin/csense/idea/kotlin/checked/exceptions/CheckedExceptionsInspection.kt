package csense.idea.kotlin.checked.exceptions

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInspection.*
import org.jetbrains.kotlin.idea.inspections.*
import org.jetbrains.kotlin.psi.*


class CheckedExceptionsInspection : AbstractKotlinInspection() {
    override fun getDisplayName(): String {
        return "Checked exceptions in kotlin"
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
            //usefull in debugging.
            //val callCode = namedFunction.text
            val functionResolved = namedFunction.resolveMainReference()
                    ?: return@callExpressionVisitor
            //Does it throw ?
            if (functionResolved.throwsIfFunction() == true) {
                //is there any try catch and if not, is the container marked as throws ? if not then its an error.
                if (namedFunction.isNotWrappedInTryCatch()
                        && !namedFunction.isContainingFunctionMarkedAsThrows()
                        && !namedFunction.isContainedInFunctionCatching()
                ) {

                    holder.registerProblem(
                            namedFunction,
                            "This call throws, so you should handle it with try catch. or declare that this method throws.",
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                }
            }
        }
    }


    override fun isEnabledByDefault(): Boolean {
        return true
    }


}