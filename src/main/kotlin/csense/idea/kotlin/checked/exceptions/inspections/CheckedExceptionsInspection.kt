package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.cache.*
import csense.idea.kotlin.checked.exceptions.callthough.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.settings.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.idea.inspections.*
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
            
            val realParent = if (namedFunction.parent is KtDotQualifiedExpression) {
                namedFunction.parent
            } else {
                namedFunction
            }
            
            val throwsCached = SharedMethodThrowingCache.throwsTypes(
                    namedFunction)
            //Does it throw ? (if not just break)
            if (throwsCached.isEmpty()) {//break if empty as it does not throw.
                return@callExpressionVisitor
            }
            //is there any try catch and if not, is the container marked as throws ? if not then its an error.
            //The following code is VERY expensive.
            val lambdaContext = namedFunction.getPotentialContainingLambda()
            val tryCatchExpression = namedFunction.findParentTryCatch()
            val isAllCaught = tryCatchExpression != null && tryCatchExpression.catchesAll(throwsCached)
            val markedThrows = namedFunction.containingFunctionMarkedAsThrowTypes()
            if (markedThrows.isNotEmpty()) {
                //test type, and report if not correct.
                if (!markedThrows.isAllThrowsHandledByTypes(markedThrows)) {
                    registerAnnotationProblem(holder, realParent, namedFunction, throwsCached, markedThrows)
                }
                
            } else if (!isAllCaught
                    && (lambdaContext == null ||
                            !namedFunction.isContainedInLambdaCatchingOrIgnoredRecursive(
                                    ignoreInMemory,
                                    getMaxDepth(),
                                    throwsCached))
            ) {
                //it throws, we want to cache that.
                registerProblems(holder, realParent, namedFunction, throwsCached, lambdaContext)
            }
        }
    }
    
    
    private fun registerProblems(
            holder: ProblemsHolder,
            realParent: PsiElement,
            namedFunction: KtCallExpression,
            throwsTypes: List<UClass>,
            lambdaParameterData: LambdaParameterData?
    ) {
        holder.registerProblem(
                realParent,
                "This call throws, so you should handle it with try catch, or declare that this method throws.\n It throws the following types:" +
                        throwsTypes.joinToString(", ") { it.name ?: "" },
                *(createQuickFixes(namedFunction, throwsTypes.map {
                    it.name ?: ""
                }, false) + getIgnoreQuickFixes(lambdaParameterData))
        )
    }
    
    private fun registerAnnotationProblem(
            holder: ProblemsHolder,
            realParent: PsiElement,
            namedFunction: KtCallExpression,
            throwsTypes: List<UClass>,
            markedTypes: List<UClass>
    
    ) {
        val throwsText = throwsTypes.joinToString(", ") { it.name ?: "" }
        val markedText = markedTypes.joinToString(", ") { it.name ?: "" }
        holder.registerProblem(
                realParent,
                "This throw type ($throwsText) is not in the annotated types: $markedText",
                *(createQuickFixes(namedFunction, throwsTypes.map {
                    it.name ?: ""
                }, true))
        )
    }
    
    private fun getIgnoreQuickFixes(lambdaContext: LambdaParameterData?): List<LocalQuickFix> {
        if (lambdaContext == null) {
            return listOf()
        }
        val result = mutableListOf<LocalQuickFix>()
        if (!ignoreInMemory.isArgumentMarkedAsIgnore(lambdaContext.main, lambdaContext.parameterName)) {
            result += AddLambdaToIgnoreQuickFix(lambdaContext.main, lambdaContext.parameterName)
        }
        if (!CallthoughInMemory.isArgumentMarkedAsCallthough(lambdaContext.main, lambdaContext.parameterName)) {
            result += AddLambdaToCallthoughQuickFix(lambdaContext.main, lambdaContext.parameterName)
        }
        //we either have it ignored or no lambda, or "not", thus we can add it.
        return result
    }
    
    private fun createQuickFixes(
            namedFunction: KtCallExpression,
            exceptionTypes: List<String>,
            haveThrowsAnnotation: Boolean
    ): Array<LocalQuickFix> {
        val declare: LocalQuickFix = haveThrowsAnnotation.mapLazy({
            AddFunctionThrowsQuickFix(namedFunction, exceptionTypes)
        }, {
            DeclareFunctionAsThrowsQuickFix(namedFunction, exceptionTypes)
        })
        
        return arrayOf(
                WrapInTryCatchQuickFix(namedFunction, exceptionTypes),
                declare
        )
    }
    
    override fun isEnabledByDefault(): Boolean {
        return true
    }
    
}


