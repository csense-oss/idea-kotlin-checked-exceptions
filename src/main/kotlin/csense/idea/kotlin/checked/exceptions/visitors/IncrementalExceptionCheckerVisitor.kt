package csense.idea.kotlin.checked.exceptions.visitors

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.`class`.operations.filter.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import org.intellij.lang.annotations.*
import org.jetbrains.kotlin.psi.*

class IncrementalExceptionCheckerVisitor(
    val holder: ProblemsHolder,
    val project: Project
) : KtTreeVisitor<IncrementalExceptionCheckerState?>() {

    override fun visitTryExpression(
        expression: KtTryExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val captures: List<KtPsiClass> = expression.catchClauses.mapNotNull {
            it.catchParameter?.resolveFirstClassType2()
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
        val throwsTypes: List<KtPsiClass> = function.toKtPsiFunction().throwsTypesForSettingsOrEmpty()

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
        val thrownExceptions: List<KtPsiClass> = expression
            .resolveMainReferenceAsFunction()
            .throwsTypesForSettingsOrEmpty()

        findIssuesAndReport(
            expression = expression,
            currentState = currentState,
            potentialExceptions = thrownExceptions
        )

        return super.visitCallExpression(expression, currentState)
    }

    override fun visitThrowExpression(
        expression: KtThrowExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(state)

        val throws: KtPsiClass? = expression.resolveThrownTypeOrNull()
        val throwsList: List<KtPsiClass> = listOfNotNull(throws).filterRuntimeExceptionsBySettings()

        findIssuesAndReport(expression = expression, currentState = currentState, potentialExceptions = throwsList)

        return super.visitThrowExpression(expression, currentState)
    }


    override fun visitPropertyDelegate(
        delegate: KtPropertyDelegate,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(state)

        val thrownExceptions: List<KtPsiClass> = delegate.references.firstNotNullOfOrNull { it: PsiReference? ->
            it?.resolve()?.toKtPsiFunction()
        }.throwsTypesForSettingsOrEmpty()

        findIssuesAndReport(expression = delegate, currentState = currentState, potentialExceptions = thrownExceptions)

        return super.visitPropertyDelegate(delegate, currentState)
    }

    override fun visitSimpleNameExpression(
        expression: KtSimpleNameExpression,
        data: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = createNewStateFrom(previousState = data)

        val prop: KtProperty = expression.resolveAsKtProperty()
            ?: return super.visitSimpleNameExpression(expression, data)

        val declaredThrows: List<KtPsiClass> = prop.throwsTypesWithGetter()

        findIssuesAndReport(expression = expression, currentState = currentState, potentialExceptions = declaredThrows)

        return super.visitSimpleNameExpression(expression, data)
    }

    override fun visitLambdaExpression(
        expression: KtLambdaExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {

        val lambdaCaptures: List<KtPsiClass> = expression.computeLambdaCaptureTypes(
            currentCaptures = state?.captures.orEmpty()
        )

        val updatedState = IncrementalExceptionCheckerState(
            captures = lambdaCaptures,
            throwsTypes = state?.throwsTypes.orEmpty(),
            lastLambda = expression
        )

        return super.visitLambdaExpression(expression, updatedState)
    }


    private fun List<KtPsiClass>.notCaughtExceptionMessage(): String {
        val typesHtml: String = coloredFqNameString(
            cssColor = typeCssColor,
            tagType = "b"
        )

        //TODO this should be a resource bundle.
        @Language("html")
        val resultHtml = "<html>Uncaught exceptions $typesHtml</html>"
        return resultHtml
    }

    private fun <T : KtElement> findIssuesAndReport(
        expression: T,
        currentState: IncrementalExceptionCheckerState,
        potentialExceptions: List<KtPsiClass>
    ) {
        val nonCaughtExceptions: List<KtPsiClass> =
            potentialExceptions.filterUnrelatedExceptions(to = currentState.captures)
        if (nonCaughtExceptions.isNotEmpty()) {
            holder.registerProblem(
                /* psiElement = */ expression,
                /* descriptionTemplate = */ nonCaughtExceptions.notCaughtExceptionMessage(),
                /* ...fixes = */ *expression.quickFixesFor(
                    state = currentState,
                    nonCaughtExceptions = nonCaughtExceptions
                )
            )
        }
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