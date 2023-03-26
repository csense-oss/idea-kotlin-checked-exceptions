package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import csense.idea.kotlin.checked.exceptions.bll.ignore.*
import org.jetbrains.kotlin.psi.*

fun KtLambdaExpression.computeLambdaCaptureTypes(
    currentCaptures: List<KtPsiClass>
): List<KtPsiClass> = when {
    isLambdaInIgnoreExceptions() -> {
        val resolution: ProjectClassResolutionInterface = ProjectClassResolutionInterface.getOrCreate(project)
        listOfNotNull(resolution.kotlinOrJavaThrowable)
    }
    isLambdaCallThough() -> currentCaptures
    else -> emptyList()
}

fun KtLambdaExpression.isLambdaInIgnoreExceptions(
): Boolean {
    val repo = IgnoreRepo(project)
    return repo.isLambdaIgnoreExceptions(this)
}

fun KtLambdaExpression.isLambdaCallThough(
): Boolean {
    //TODO this is quite "inefficient".. :/ should use a service or alike?!
    val repo = CallThoughRepo(project)
    return repo.isLambdaCallThough(this)
}