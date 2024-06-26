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
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.kotlin.extensions.*
import org.intellij.lang.annotations.*
import org.jetbrains.kotlin.psi.*

//TODO visit class level properties?

class IncrementalExceptionCheckerVisitor(
    val holder: ProblemsHolder,
    val project: Project
) : KtTreeVisitor<IncrementalExceptionCheckerState?>() {

    override fun visitTryExpression(
        expression: KtTryExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val captures: List<KtPsiClass> = expression.catchClauses.mapNotNull { it: KtCatchClause? ->
            it?.catchParameter?.resolveFirstClassType2()
        }.filterRuntimeExceptionsBySettings()

        val newState: IncrementalExceptionCheckerState = state.newStateByAppending(
            newCaptures = captures,
            containingTryExpression = expression
        )

        return super.visitTryExpression(expression, newState)
    }

    override fun visitProperty(
        property: KtProperty,
        data: IncrementalExceptionCheckerState?
    ): Void? {
        if (property.hasCustomCode()) {
            onVisitPropertyWithCustomCode(property = property, data = data)
            return null
        }
        return super.visitProperty(property, data)
    }

    private fun onVisitPropertyWithCustomCode(property: KtProperty, data: IncrementalExceptionCheckerState?) {
        val customGetter: KtExpression? = property.initalizerOrGetter()
        if (customGetter != null) {
            onVisitPropertyWithCustomGetter(property = property, data = data, customGetter = customGetter)
        }

        val customSetter: KtExpression? = property.setter?.bodyExpression ?: property.setter?.bodyBlockExpression
        if (customSetter != null) {
            onVisitPropertyWithCustomSetter(property = property, data = data, customSetter = customSetter)
        }

        val delegateExpression: KtExpression? = property.delegateExpression
        if (delegateExpression != null) {
            onVisitPropertyWithDelegate(property, data)
        }
    }

    private fun onVisitPropertyWithCustomGetter(
        property: KtProperty,
        data: IncrementalExceptionCheckerState?,
        customGetter: KtExpression
    ) {
        val declaredThrows: List<KtPsiClass> =
            property.throwsTypesWithGetter().filterRuntimeExceptionsBySettings()

        val newState: IncrementalExceptionCheckerState = data.newStateByAppending(
            newCaptures = declaredThrows,
            parentScope = property.getter
        )
        visitExpression(/* expression = */ customGetter, /* data = */ newState)
    }

    private fun onVisitPropertyWithCustomSetter(
        property: KtProperty,
        data: IncrementalExceptionCheckerState?,
        customSetter: KtExpression
    ) {
        val declaredThrows: List<KtPsiClass> =
            property.throwsTypesWithSetter().filterRuntimeExceptionsBySettings()

        val newState: IncrementalExceptionCheckerState = data.newStateByAppending(
            newCaptures = declaredThrows,
            parentScope = property.setter
        )
        visitExpression(/* expression = */ customSetter, /* data = */ newState)
    }

    private fun onVisitPropertyWithDelegate(
        property: KtProperty,
        data: IncrementalExceptionCheckerState?
    ) {
//        val declaredThrows: List<KtPsiClass> =
//            property.throwsTypesWithSetter().filterRuntimeExceptionsBySettings()
//
//        val newState: IncrementalExceptionCheckerState = data.newStateByAppending(
//            newCaptures = declaredThrows,
//            parentScope = property.setter
//        )
//        visitExpression(/* expression = */ customSetter, /* data = */ newState)
    }

    override fun visitNamedFunction(
        function: KtNamedFunction,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val throwsTypes: List<KtPsiClass> = function.toKtPsiFunction().throwsTypesForSettingsOrEmpty()

        val newState: IncrementalExceptionCheckerState = state.newStateByAppending(
            newCaptures = throwsTypes,
            newThrows = throwsTypes,
            parentScope = function
        )
        return super.visitNamedFunction(function, newState)
    }


    override fun visitCallExpression(
        expression: KtCallExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = state.newStateByAppending()
        val thrownExceptions: List<KtPsiClass> = expression
            .resolveMainReferenceAsFunction()
            .throwsTypesForCallBySettingsOrEmpty()

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
        val currentState: IncrementalExceptionCheckerState = state.newStateByAppending()

        val throws: KtPsiClass? = expression.resolveThrownTypeOrNull()
        val throwsList: List<KtPsiClass> = listOfNotNull(throws).filterRuntimeExceptionsBySettings()

        findIssuesAndReport(
            expression = expression,
            currentState = currentState,
            potentialExceptions = throwsList
        )

        return super.visitThrowExpression(expression, currentState)
    }


    override fun visitPropertyDelegate(
        delegate: KtPropertyDelegate,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = state.newStateByAppending()

        val thrownExceptions: List<KtPsiClass> = delegate.references.firstNotNullOfOrNull { it: PsiReference? ->
            it?.resolve()?.toKtPsiFunction()
        }.throwsTypesForSettingsOrEmpty()

        findIssuesAndReport(expression = delegate, currentState = currentState, potentialExceptions = thrownExceptions)

        return super.visitPropertyDelegate(delegate, currentState)
    }

    override fun visitSimpleNameExpression(
        expression: KtSimpleNameExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = state.newStateByAppending()

        val prop: KtProperty = expression.resolveAsKtProperty()
            ?: return super.visitSimpleNameExpression(expression, state)

        val declaredThrows: List<KtPsiClass> = prop.throwsTypesWithGetter()

        findIssuesAndReport(expression = expression, currentState = currentState, potentialExceptions = declaredThrows)

        return super.visitSimpleNameExpression(expression, state)
    }

    override fun visitLambdaExpression(
        expression: KtLambdaExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {

        val lambdaLookup: LambdaArgumentLookup? = expression.toLamdaArgumentLookup()

        val lambdaCaptures: List<KtPsiClass> = expression.computeLambdaCaptureTypesOrEmpty(
            lambdaLookup = lambdaLookup,
            state = state
        )

        val resolution: ProjectClassResolutionInterface = ProjectClassResolutionInterface.getOrCreate(project)
        val callsThough: Boolean = lambdaLookup?.isLambdaCallThough(resolution) == true

        val updatedState = IncrementalExceptionCheckerState(
            captures = lambdaCaptures,
            throwsTypes = state?.throwsTypes.orEmpty(),
            containingLambdas = state?.containingLambdas.orEmpty() + expression,
            containingTryExpression = state?.containingTryExpression,
            parentScope = callsThough.map(ifTrue = state?.parentScope, ifFalse = expression)
        )

        return super.visitLambdaExpression(expression, updatedState)
    }

    private fun KtLambdaExpression.computeLambdaCaptureTypesOrEmpty(
        lambdaLookup: LambdaArgumentLookup?,
        state: IncrementalExceptionCheckerState?
    ): List<KtPsiClass> {
        if (lambdaLookup == null) {
            return emptyList()
        }
        return computeLambdaCaptureTypes(
            currentCaptures = state?.captures.orEmpty(),
            lambdaLookup = lambdaLookup
        )
    }


    private fun List<KtPsiClass>.notCaughtExceptionMessage(): String {
        val typesHtml: String = coloredFqNameString(
            cssColor = typeCssColor,
            tagType = "b"
        )

        @Language("html")
        val resultHtml = "<html>Uncaught exceptions $typesHtml</html>"
        return resultHtml
    }

    private fun <T : KtElement> findIssuesAndReport(
        expression: T,
        currentState: IncrementalExceptionCheckerState,
        potentialExceptions: List<KtPsiClass>
    ) {
        val uncaughtExceptions: List<KtPsiClass> =
            potentialExceptions.filterUnrelatedExceptions(to = currentState.captures)
        if (uncaughtExceptions.isNotEmpty()) {
            holder.registerProblem(
                /* psiElement = */ expression,
                /* descriptionTemplate = */ uncaughtExceptions.notCaughtExceptionMessage(),
                /* ...fixes = */ *expression.quickFixesFor(
                    uncaughtExceptions = uncaughtExceptions,
                    state = currentState
                )
            )
        }
    }

    override fun visitCallableReferenceExpression(
        expression: KtCallableReferenceExpression,
        state: IncrementalExceptionCheckerState?
    ): Void? {
        val currentState: IncrementalExceptionCheckerState = state.newStateByAppending()

        val thrownExceptions: List<KtPsiClass> = expression
            .resolveKtPsiFunctionOrNull()
            ?.throwsTypesForCallBySettingsOrEmpty() ?: listOf()

        findIssuesAndReport(
            expression = expression,
            currentState = currentState,
            potentialExceptions = thrownExceptions
        )
        return super.visitCallableReferenceExpression(expression, state)
    }

    private fun IncrementalExceptionCheckerState?.newStateByAppending(
        newCaptures: List<KtPsiClass> = listOf(),
        newThrows: List<KtPsiClass> = listOf(),
        containingTryExpression: KtTryExpression? = null,
        parentScope: KtElement? = null
    ): IncrementalExceptionCheckerState = IncrementalExceptionCheckerState(
        captures = this?.captures.orEmpty() + newCaptures,
        throwsTypes = this?.throwsTypes.orEmpty() + newThrows,
        containingLambdas = this?.containingLambdas.orEmpty(),
        containingTryExpression = containingTryExpression ?: this?.containingTryExpression,
        parentScope = parentScope ?: this?.parentScope
    )

    companion object {
        //todo settings?
        const val typeCssColor: String = "#ff6b2b"
    }
}


data class IncrementalExceptionCheckerState(
    val captures: List<KtPsiClass>,
    val throwsTypes: List<KtPsiClass>,
    val containingLambdas: List<KtLambdaExpression>,
    val containingTryExpression: KtTryExpression?,
    val parentScope: KtElement? = null
) {
    companion object {
        val empty: IncrementalExceptionCheckerState = IncrementalExceptionCheckerState(
            captures = emptyList(),
            throwsTypes = emptyList(),
            containingLambdas = emptyList(),
            containingTryExpression = null
        )
    }
}