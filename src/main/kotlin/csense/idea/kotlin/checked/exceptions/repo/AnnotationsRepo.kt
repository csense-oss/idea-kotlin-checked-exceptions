package csense.idea.kotlin.checked.exceptions.repo

import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.csense.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*

object AnnotationsRepo {


    fun isAnyRethrowsExceptions(
        argument: LambdaArgumentLookup
    ): Boolean {
        return argument
            .parameterToValueExpression
            .parameterValueAnnotations
            .anyByFqNames(rethrowsExceptionFqNames)
    }


    fun getCatchesExceptionTypesAnnotationOrEmpty(
        argument: LambdaArgumentLookup,
        resolution: ProjectClassResolutionInterface
    ): List<KtPsiClass> {
        val allCatchesException: List<KtAnnotationEntry> = argument.getAllCatchAnnotations()
        return allCatchesException.toResolvedExceptionsTypes(resolution)
    }

    private fun LambdaArgumentLookup.getAllCatchAnnotations(): List<KtAnnotationEntry> {
        return parameterToValueExpression.parameterValueAnnotations.filterByFqNames(
            fqNames = catchesExceptionFqName
        )
    }

    private fun List<KtAnnotationEntry>.toResolvedExceptionsTypes(
        resolution: ProjectClassResolutionInterface
    ): List<KtPsiClass> {
        if (isEmpty()) {
            return emptyList()
        }
        val resolvedTypes: List<KtPsiClass> = map { it: KtAnnotationEntry ->
            it.resolveValueParametersAsKClassTypes()
        }.flatten()
        //in the case that none of the types could be looked up, return the general throwable type.
        return resolvedTypes.onEmpty(
            listOfNotNull(resolution.kotlinOrJavaThrowable)
        )
    }

    val rethrowsExceptionFqNames: Set<String> = setOf(
        "csense.kotlin.annotations.exceptions.RethrowsExceptions",
        "org.csenseoss.kotlin.annotations.exceptions.RethrowsExceptions"
    )
    val catchesExceptionFqName: Set<String> = setOf(
        "csense.kotlin.annotations.exceptions.CatchesExceptions",
        "org.csenseoss.kotlin.annotations.exceptions.CatchesExceptions"
    )

}