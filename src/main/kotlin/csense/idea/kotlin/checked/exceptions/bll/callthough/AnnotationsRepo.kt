package csense.idea.kotlin.checked.exceptions.bll.callthough

import csense.idea.base.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*

object AnnotationsRepo {


    fun isAnyRethrowsExceptions(
        argument: LambdaArgumentLookup
    ): Boolean {
        return argument.parameterToValueExpression.parameterValueAnnotations.any { it: KtAnnotationEntry ->
            it.fqName() == "csense.kotlin.annotations.exceptions.RethrowsExceptions"
        }
    }


    fun getCatchesExceptionTypesAnnotationOrEmpty(
        argument: LambdaArgumentLookup,
        resolution: ProjectClassResolutionInterface
    ): List<KtPsiClass> {
        val allCatchesException: List<KtAnnotationEntry> = argument.parameterToValueExpression
            .parameterValueAnnotations
            .filterByFqName(
                fqName = "csense.kotlin.annotations.exceptions.CatchesExceptions"
            )
        if (allCatchesException.isEmpty()) {
            return emptyList()
        }

        return allCatchesException.map { it: KtAnnotationEntry ->
            it.resolveValueParametersAsKClassTypes()
        }.flatten().onEmpty(
            listOfNotNull(resolution.kotlinOrJavaThrowable)
        )

    }


}