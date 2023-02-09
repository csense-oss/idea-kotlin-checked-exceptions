package csense.idea.kotlin.checked.exceptions.bll

import csense.kotlin.extensions.primitives.*
import org.jetbrains.kotlin.psi.*


object ThrowsAnnotationBll {

    fun createThrowsAnnotation(
        caller: KtAnnotated,
        optionalExceptionType: String?
    ): KtAnnotationEntry {
        val exceptionType = optionalExceptionType.toExceptionTypeOrEmpty()
        val annotation = "@${Constants.kotlinThrowsText}(${exceptionType})"
        return KtPsiFactory(caller).createAnnotationEntry(annotation)
    }

    private fun String?.toExceptionTypeOrEmpty(): String =
        this?.nullOnBlank().orEmpty()

}