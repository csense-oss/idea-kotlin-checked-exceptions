package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import org.jetbrains.kotlin.psi.*

fun KtLambdaExpression.computeLambdaCaptureTypes(
    currentCaptures: List<KtPsiClass>
): List<KtPsiClass> {
    val resolution: ProjectClassResolutionInterface = ProjectClassResolutionInterface.getOrCreate(project)
    if (isLambdaInIgnoreExceptions(resolution)) {
        return listOfNotNull(resolution.kotlinOrJavaThrowable)
    }
    if (isLambdaCallThough(resolution)) {
        return currentCaptures
    }
//    val catchesExceptionAnnotation = getCatchesExcepionAnnotationOrNull()
//    if (catchesExceptionAnnotation != null) {
//        return catchesExceptionAnnotation.catches()
//    }

    return emptyList()
}

fun KtLambdaExpression.isLambdaInIgnoreExceptions(
    resolution: ProjectClassResolutionInterface
): Boolean {
    return resolution.ignoreRepo.isLambdaIgnoreExceptions(this)
}

fun KtLambdaExpression.isLambdaCallThough(
    resolution: ProjectClassResolutionInterface
): Boolean {
    return resolution.callThoughRepo.isLambdaCallThough(this)
}



