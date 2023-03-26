package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
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

    return AnnotationsRepo.getCatchesExceptionTypesAnnotationOrEmpty(
        argument = lambdaLookup,
        resolution = resolution
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
