package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import org.jetbrains.kotlin.psi.*

fun KtLambdaExpression.computeLambdaCaptureTypes(
    currentCaptures: List<KtPsiClass>,
    lambdaLookup: LambdaArgumentLookup
): List<KtPsiClass> {
    val resolution: ProjectClassResolutionInterface = ProjectClassResolutionInterface.getOrCreate(project)
    if (lambdaLookup.isLambdaInIgnoreExceptions(resolution)) {
        return listOfNotNull(resolution.kotlinOrJavaThrowable)
    }
    if (lambdaLookup.isLambdaCallThough(resolution)) {
            return currentCaptures
    }

    return lambdaLookup.getCatchesExceptionTypesAnnotationOrEmpty(
        resolution
    )
}

fun LambdaArgumentLookup.isLambdaInIgnoreExceptions(
    resolution: ProjectClassResolutionInterface
): Boolean {
    return resolution.ignoreRepo.isLambdaIgnoreExceptions(this)
}

fun LambdaArgumentLookup.isLambdaCallThough(
    resolution: ProjectClassResolutionInterface
): Boolean {
    return resolution.callThoughRepo.isLambdaCallThough(this)
}


fun LambdaArgumentLookup.getCatchesExceptionTypesAnnotationOrEmpty(
    resolution: ProjectClassResolutionInterface
): List<KtPsiClass> {
    val allCatchesException: List<KtAnnotationEntry> = parameterToValueExpression
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

