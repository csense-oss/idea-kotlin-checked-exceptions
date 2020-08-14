package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.callthough.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.idea.inspections.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*

class IncrementalCheckedExceptionInspection : AbstractKotlinInspection() {
    
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
        return namedFunctionVisitor {
//            logMeasureTimeInMillis("incremental time") {
            val project = holder.project
            val visitor = IncrementalFunctionCheckedVisitor(holder, project)
            it.accept(visitor, IncrementalStep(listOf()))
//            }
        }
    }
}

class IncrementalFunctionCheckedVisitor(
        val holder: ProblemsHolder,
        val project: Project
) : KtTreeVisitor<IncrementalStep>() {
    
    private val javaThrowableUClass: UClass? by lazy {
        JavaPsiFacade.getInstance(project)
                .findClass("java.lang.Throwable", GlobalSearchScope.allScope(project))
                ?.toUElementOfType<UClass>()
    }
    
    override fun visitTryExpression(expression: KtTryExpression, data: IncrementalStep): Void? {
        val captures = expression.catchClauses.mapNotNull { it.catchParameter?.resolveTypeClassException(project) }
        val newState = data.copy(captures = data.captures + captures.filterRuntimeExceptionsBySettings())
        return super.visitTryExpression(expression, newState)
    }
    
    override fun visitNamedFunction(function: KtNamedFunction, data: IncrementalStep): Void? {
        val throws = function.throwsTypes()
        val newData = IncrementalStep(
                data.captures + throws.filterRuntimeExceptionsBySettings(),
                isMarkedThrows = throws.isNotEmpty())
        return super.visitNamedFunction(function, newData)
    }
    
    
    override fun visitCallExpression(expression: KtCallExpression, data: IncrementalStep): Void? {
        
        val thrown = expression.resolveMainReference()?.throwsTypesIfFunction(expression)
        val nonCaught = thrown?.filterOnlyNonCaught(data.captures)
        if (nonCaught.isNotNullOrEmpty()) {
            val throwTypes = nonCaught.map { it.name ?: "" }
            val text = "This call throws, so you should handle it with try catch, or declare that this method throws.\n It throws the following types:" +
                    throwTypes.joinToString(", ")
            holder.registerProblem(
                    expression,
                    text,
                    *createQuickFixes(
                            expression,
                            throwTypes,
                            data.isMarkedThrows)
            )
        }
        return super.visitCallExpression(expression, data)
    }
    
    
    override fun visitLambdaExpression(expression: KtLambdaExpression, data: IncrementalStep): Void? {
        val lambda = expression.asPotentialContainingLambda()
        val currentCaptures: List<UClass> = if (lambda != null) {
            when {
                //ignore => capture all
                lambda.isIgnored(IgnoreInMemory) -> {
                    javaThrowableUClass?.let { listOf(it) } ?: emptyList()
                }
                //call though => use parent captures
                lambda.isCallThough() -> {
                    data.captures
                }
                //else its just a normal lambda, thus it defines its own captures
                else -> listOf()
            }
        } else {
            data.captures
        }
        
        return super.visitLambdaExpression(expression,
                data.copy(
                        captures = currentCaptures,
                        containingLambda = lambda
                )
        )
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
    
}

data class IncrementalStep(
        val captures: List<UClass>,
        val isMarkedThrows: Boolean = false,
        val containingLambda: LambdaParameterData? = null
)


fun KtParameter.resolveTypeClassException(project: Project): UClass? {
    val resolved = this.typeReference?.resolve()
    if (resolved?.getKotlinFqName() == KotlinBuiltIns.FQ_NAMES.throwable) {
        return JavaPsiFacade.getInstance(project)
                .findClass("java.lang.Throwable", GlobalSearchScope.allScope(project))
                ?.toUElementOfType()
    }
    return resolved?.toUElementOfType()
}
