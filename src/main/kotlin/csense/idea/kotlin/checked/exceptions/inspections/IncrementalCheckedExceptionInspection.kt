package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.annotator.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.builtin.operations.*
import csense.idea.kotlin.checked.exceptions.settings.*
import csense.kotlin.extensions.*
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
        val project = holder.project
        val visitor = IncrementalExceptionCheckerVisitor(
            holder = holder,
            project = project
        )
        return NamedFunctionOrDelegationVisitor(
            onFunctionNamed = {
                it.accept(
                    visitor, IncrementalExceptionCheckerState(
                        captures = listOf(),
                        throwsTypes = listOf()
                    )
                )
            },
            onPropertyDelegate = {
                it.accept(
                    visitor, IncrementalExceptionCheckerState(
                        captures = listOf(),
                        throwsTypes = listOf()
                    )
                )
            }
        )
//        return namedFunctionVisitor {

//            it.accept(
//                /* visitor = */ visitor,
//                /* data = */ IncrementalExceptionCheckerState(
//                    captures = emptyList(),
//                    throwsTypes = emptyList()
//                )
//            )
//        }
    }
}

class IncrementalExceptionCheckerVisitor(
    val holder: ProblemsHolder,
    val project: Project
) : KtTreeVisitor<IncrementalExceptionCheckerState?>() {

    override fun visitTryExpression(
        expression: KtTryExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val captures: List<KtPsiClass> = expression.catchClauses.mapNotNull {
            it.catchParameter?.resolveToExceptionType()
        }.filterRuntimeExceptionsBySettings()

        val newState: IncrementalExceptionCheckerState = createNewStateFrom(
            previousState = state,
            newCaptures = captures
        )

        return super.visitTryExpression(expression, newState)
    }


    override fun visitNamedFunction(
        function: KtNamedFunction,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val throwsTypes: List<KtPsiClass> = function.toKtPsiFunction()?.throwsTypesForSettings().orEmpty()

        val newState: IncrementalExceptionCheckerState = createNewStateFrom(
            previousState = state,
            newCaptures = throwsTypes,
            newThrows = throwsTypes
        )
        return super.visitNamedFunction(function, newState)
    }


    override fun visitCallExpression(
        expression: KtCallExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(state)
        val potentialExceptions: List<KtPsiClass> =
            expression.resolveMainReferenceAsFunction()?.throwsTypesForSettings().orEmpty()


        val nonCaughtExceptions: List<KtPsiClass> = potentialExceptions.filterNonRelated(to = currentState.captures)
        if (nonCaughtExceptions.isNotEmpty()) {
//            val text =
//                "This call throws, so you should handle it with try catch, or declare that this method throws.\n It throws the following types:" +
//                        throwTypes.joinToString(", ")
            holder.registerProblem(
                expression,
                "... Expresion throws type(s)(${nonCaughtExceptions.joinToString { it.fqName ?: "" }}) not caught ..."//,
//                *createQuickFixes(
//                    expression,
//                    throwTypes,
//                    data.isMarkedThrows,
//                    data.containingLambda
//                )
            )
        }
        return super.visitCallExpression(expression, currentState)
    }

    override fun visitThrowExpression(
        expression: KtThrowExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(state)

        val throws: KtPsiClass? = expression.resolveThrownTypeOrNull()
        val throwsList: List<KtPsiClass> = listOfNotNull(throws).filterRuntimeExceptionsBySettings()
        val nonCaughtExceptions: List<KtPsiClass> = throwsList.filterNonRelated(to = currentState.captures)
        if (nonCaughtExceptions.isNotEmpty()) {
            holder.registerProblem(
                expression,
                "... Throws type not caught ..."//,
//                *createQuickFixes(
//                    expression,
//                    throwTypes,
//                    data.isMarkedThrows,
//                    data.containingLambda
//                )
            )
        }
        return super.visitThrowExpression(expression, currentState)
    }


    override fun visitPropertyDelegate(
        delegate: KtPropertyDelegate,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(state)


        val potentialExceptions = delegate.references.firstNotNullOfOrNull {
            it.resolve()?.toKtPsiFunction()
        }?.throwsTypesForSettings().orEmpty()

        val nonCaughtExceptions: List<KtPsiClass> = potentialExceptions.filterNonRelated(to = currentState.captures)
        if (nonCaughtExceptions.isNotEmpty()) {
            holder.registerProblem(
                delegate,
                "... Throws type not caught ..."//,
//                *createQuickFixes(
//                    expression,
//                    throwTypes,
//                    data.isMarkedThrows,
//                    data.containingLambda
//                )
            )
        }
        return super.visitPropertyDelegate(
            delegate,
            currentState
        )
    }

    private fun KtPsiFunction.throwsTypesForSettings(): List<KtPsiClass> {
        return throwsTypesOrBuiltIn(project).filterRuntimeExceptionsBySettings()
    }

//    override fun visitLambdaExpression(expression: KtLambdaExpression, data: IncrementalStep): Void? {
//        val lambda = expression.asPotentialContainingLambda()
//        val currentCaptures: List<UClass> = if (lambda != null) {
//            when {
//                //ignore => capture all
//                lambda.isIgnored(IgnoreInMemory) -> {
//                    javaThrowableUClass?.let { listOf(it) } ?: emptyList()
//                }
//                //call though => use parent captures
//                lambda.isCallThough() -> {
//                    data.captures
//                }
//                //else its just a normal lambda, thus it defines its own captures
//                else -> listOf()
//            }
//        } else {
//            data.captures
//        }
//
//        return super.visitLambdaExpression(
//            expression,
//            data.copy(
//                captures = currentCaptures,
//                containingLambda = lambda
//            )
//        )
//    }

    //    private fun createQuickFixes(
//        namedFunction: KtCallExpression,
//        exceptionTypes: List<String>,
//        haveThrowsAnnotation: Boolean,
//        containingLambda: LambdaParameterData?
//    ): Array<LocalQuickFix> {
//        val declare: LocalQuickFix = haveThrowsAnnotation.mapLazy({
//            AddFunctionThrowsQuickFix(namedFunction, exceptionTypes)
//        }, {
//            DeclareFunctionAsThrowsQuickFix(namedFunction, exceptionTypes)
//        })
//
//        val lambdaRelatedQuickfixes: Array<LocalQuickFix> = if (containingLambda != null) {
//            val lambdaQuickFixes = mutableListOf<LocalQuickFix>()
//            if (!containingLambda.isIgnored(IgnoreInMemory)) {
//                lambdaQuickFixes.add(AddLambdaToIgnoreQuickFix(containingLambda.main, containingLambda.parameterName))
//            }
//            if (!containingLambda.isCallThough()) {
//                lambdaQuickFixes.add(
//                    AddLambdaToCallthoughQuickFix(
//                        containingLambda.main,
//                        containingLambda.parameterName
//                    )
//                )
//            }
//            lambdaQuickFixes.toTypedArray()
//        } else {
//            arrayOf()
//        }
//
//        return arrayOf(
//            WrapInTryCatchQuickFix(namedFunction, exceptionTypes),
//            declare
//        ) + lambdaRelatedQuickfixes
//    }
    private fun createNewStateFrom(
        previousState: IncrementalExceptionCheckerState?,
        newCaptures: List<KtPsiClass> = listOf(),
        newThrows: List<KtPsiClass> = listOf()
    ): IncrementalExceptionCheckerState = IncrementalExceptionCheckerState(
        captures = previousState?.captures.orEmpty() + newCaptures,
        throwsTypes = previousState?.throwsTypes.orEmpty() + newThrows
    )
}

data class IncrementalExceptionCheckerState(
    val captures: List<KtPsiClass>,
    val throwsTypes: List<KtPsiClass>,
//    val containingLambda: LambdaParameterData? = null
)

fun KtCallableDeclaration.resolveToExceptionType(): KtPsiClass? =
    this.resolveFirstClassType2()

fun List<KtPsiClass>.filterRuntimeExceptionsBySettings(): List<KtPsiClass> {
    if (!Settings.ignoreRuntimeExceptions) {
        return this
    }
    return filterNot { it: KtPsiClass ->
        it.isSubtypeOfRuntimeException()
    }
}

//TODO Better name!?
fun List<KtPsiClass>.filterNonRelated(to: List<KtPsiClass>): List<KtPsiClass> {
    //TODO caching etc? compute a map of "classes" and then going over the other and testing?
    return this.filterNot {
        it.isSubTypeOfAny(to)
    }
}

fun KtPsiClass.isSubTypeOfAny(other: List<KtPsiClass>): Boolean {
    val fqNames = other.mapToSet { it.fqName }
    if (this.fqName in fqNames) {
        return true
    }
    forEachSuperClassType {
        if (it.fqName in fqNames) {
            return@isSubTypeOfAny true
        }
    }
    return false
}


class NamedFunctionOrDelegationVisitor(
    private val onFunctionNamed: (KtNamedFunction) -> Unit,
    private val onPropertyDelegate: (KtPropertyDelegate) -> Unit
) : KtVisitorVoid() {
    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        onFunctionNamed(function)
    }

    override fun visitPropertyDelegate(delegate: KtPropertyDelegate) {
        super.visitPropertyDelegate(delegate)
        onPropertyDelegate(delegate)
    }
}