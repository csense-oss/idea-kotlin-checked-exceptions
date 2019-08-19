package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.cache.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.idea.inspections.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*


class CheckedExceptionsInspection : AbstractKotlinInspection() {

    private val ignoreInMemory = IgnoreInMemory()

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
        return Constants.groupName
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean): KtVisitorVoid {
        return callExpressionVisitor { namedFunction: KtCallExpression ->
            val lastModified = namedFunction.getModificationStamp()
            val fullName = namedFunction.getKotlinFqName()?.toString() ?: ""

            val throwsCached = SharedMethodThrowingCache.throwsTypes(
                    namedFunction,
                    fullName,
                    lastModified)
            //Does it throw ? (if not just break)
            if (throwsCached.isEmpty()) {//break if empty as it does not throw.
                return@callExpressionVisitor
            }
            //is there any try catch and if not, is the container marked as throws ? if not then its an error.
            //The following code is VERY expensive.
            val lambdaContext = namedFunction.getPotentialContainingLambda()
            val tryCatchExpression = namedFunction.findParentTryCatch()
            val isAllCaught = tryCatchExpression != null && tryCatchExpression.catchesAll(throwsCached)

            if (!isAllCaught
                    && !namedFunction.isContainingFunctionMarkedAsThrows()
                    && (lambdaContext == null ||
                            !namedFunction.isContainedInLambdaCatchingOrIgnoredRecursive(
                                    ignoreInMemory,
                                    getMaxDepth(),
                                    throwsCached))
            ) {
                //it throws, we want to cache that.
                registerProblems(holder, namedFunction, throwsCached, lambdaContext)
            }
        }
    }


    private fun registerProblems(
            holder: ProblemsHolder,
            namedFunction: KtCallExpression,
            throwsTypes: List<UClass>,
            lambdaParameterData: LambdaParameterData?
    ) {
        holder.registerProblem(
                namedFunction,
                "This call throws, so you should handle it with try catch, or declare that this method throws.\n It throws the following types:" +
                        throwsTypes.joinToString(", ") { it.name ?: "" },
                Settings.checkedExceptionSeverity,
                *(createQuickFixes(namedFunction, throwsTypes.map {
                    it.name ?: ""
                }) + getIgnoreQuickFixes(lambdaParameterData))
        )
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


