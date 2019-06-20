package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.util.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*

class DeclareFunctionAsThrowsQuickFix(namedFunction: KtCallExpression) : LocalQuickFixOnPsiElement(namedFunction) {

    override fun getText(): String {
        return "Mark function as throws."
    }

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val function = startElement.parentOfType(KtFunction::class) ?: return
        project.executeWriteCommand(text) {
            function.addAnnotationEntry(createThrowsAnnotation(function, null))
        }
    }

    fun createThrowsAnnotation(caller: KtFunction, exceptionType: String?): KtAnnotationEntry {
        val text =
                if (exceptionType != null) {
                    "@Throws($exceptionType::class.java)"
                } else {
                    "@Throws"
                }
        return KtPsiFactory(caller).createAnnotationEntry(text)

    }

    override fun getFamilyName(): String {
        return "csense kotlin checked exceptions"
    }

}