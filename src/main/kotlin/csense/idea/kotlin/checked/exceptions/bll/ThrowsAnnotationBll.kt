package csense.idea.kotlin.checked.exceptions.bll

import org.jetbrains.kotlin.psi.*


object ThrowsAnnotationBll{
    fun createThrowsAnnotation(caller: KtAnnotated, exceptionType: String?): KtAnnotationEntry {
        val text =
                if (exceptionType != null) {
                    "@Throws($exceptionType::class)"
                } else {
                    "@Throws"
                }
        return KtPsiFactory(caller).createAnnotationEntry(text)

    }
}