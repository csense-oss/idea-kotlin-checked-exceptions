package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import csense.idea.base.bll.psi.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.annotator.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.builtin.operations.*
import csense.idea.kotlin.checked.exceptions.settings.*
import csense.kotlin.extensions.*
import org.intellij.lang.annotations.*
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
                    /* visitor = */ visitor,
                    /* data = */ IncrementalExceptionCheckerState.empty
                )
            },
            onPropertyDelegate = {
                it.accept(
                    /* visitor = */ visitor,
                    /* data = */ IncrementalExceptionCheckerState.empty
                )
            }
        )
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
            holder.registerProblem(
                /* psiElement = */ expression,
                /* descriptionTemplate = */ nonCaughtExceptions.notCaughtExceptionMessage()
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
                /* psiElement = */ expression,
                /* descriptionTemplate = */ nonCaughtExceptions.notCaughtExceptionMessage()
            )
        }
        return super.visitThrowExpression(expression, currentState)
    }


    override fun visitPropertyDelegate(
        delegate: KtPropertyDelegate,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(state)

        val potentialExceptions: List<KtPsiClass> = delegate.references.firstNotNullOfOrNull {
            it.resolve()?.toKtPsiFunction()
        }?.throwsTypesForSettings().orEmpty()

        val nonCaughtExceptions: List<KtPsiClass> = potentialExceptions.filterNonRelated(to = currentState.captures)
        if (nonCaughtExceptions.isNotEmpty()) {
            holder.registerProblem(
                /* psiElement = */ delegate,
                /* descriptionTemplate = */ nonCaughtExceptions.notCaughtExceptionMessage()
            )
        }
        return super.visitPropertyDelegate(
            delegate,
            currentState
        )
    }


    override fun visitLambdaExpression(
        expression: KtLambdaExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {

        val lambdaCaptures: List<KtPsiClass> = computeLambdaCaptureTypes(
            lambda = expression,
            currentCaptures = state?.captures.orEmpty()
        )

        val updatedState = IncrementalExceptionCheckerState(
            captures = lambdaCaptures,
            throwsTypes = state?.throwsTypes.orEmpty(),
            lastLambda = expression
        )

        return super.visitLambdaExpression(
            expression,
            updatedState
        )
    }

    private fun computeLambdaCaptureTypes(
        lambda: KtLambdaExpression,
        currentCaptures: List<KtPsiClass>
    ): List<KtPsiClass> = when {
        isLambdaInIgnoreExceptions(lambda) -> TODO("Root exception type")
        isLambdaCallThough(lambda) -> currentCaptures
        else -> emptyList()
    }


    private fun isLambdaInIgnoreExceptions(
        lambda: KtLambdaExpression
    ): Boolean {
        val lambdaFqTypeName: String = lambda.getKotlinFqNameString() ?: ""

        //TODO()
        return false
    }

    private fun isLambdaCallThough(
        lambda: KtLambdaExpression
    ): Boolean {
        val lambdaFqTypeName: String = lambda.getKotlinFqNameString() ?: ""

        //TODO read contracts.if annotated with callsInPlace => it will be call though.

        //TODO()
        return false
    }

    private fun List<KtPsiClass>.notCaughtExceptionMessage(): String {
        @Language("html")
        val typePrefix = "<b style='color:$typeCssColor'>"

        @Language("html")
        val typesHtml: String = this.joinToString(
            separator = "</b>, $typePrefix",
            prefix = typePrefix,
            postfix = "</b>",
            transform = { ktPsiClass: KtPsiClass ->
                ktPsiClass.fqName.orEmpty()
            }
        )

        @Language("html")
        val resultHtml = "<html>Thrown type(s) $typesHtml are <b>not</b> caught </html>"
        return resultHtml
    }

    private fun KtPsiFunction.throwsTypesForSettings(): List<KtPsiClass> {
        return throwsTypesOrBuiltIn(project).filterRuntimeExceptionsBySettings()
    }

    private fun createNewStateFrom(
        previousState: IncrementalExceptionCheckerState?,
        newCaptures: List<KtPsiClass> = listOf(),
        newThrows: List<KtPsiClass> = listOf(),
        newLambda: KtLambdaExpression? = null
    ): IncrementalExceptionCheckerState = IncrementalExceptionCheckerState(
        captures = previousState?.captures.orEmpty() + newCaptures,
        throwsTypes = previousState?.throwsTypes.orEmpty() + newThrows,
        lastLambda = newLambda ?: previousState?.lastLambda
    )

    companion object {
        const val typeCssColor: String = "#ff6b2b"
    }
}

data class IncrementalExceptionCheckerState(
    val captures: List<KtPsiClass>,
    val throwsTypes: List<KtPsiClass>,
    val lastLambda: KtLambdaExpression?
) {
    companion object {
        val empty = IncrementalExceptionCheckerState(
            captures = emptyList(),
            throwsTypes = emptyList(),
            lastLambda = null
        )
    }
}

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