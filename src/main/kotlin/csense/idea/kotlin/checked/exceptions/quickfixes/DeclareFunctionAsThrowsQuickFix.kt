package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.util.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*
import java.lang.Exception

class DeclareFunctionAsThrowsQuickFix(
        namedFunction: PsiElement,
        private val exceptionTypes: List<String>) : LocalQuickFixOnPsiElement(namedFunction) {

    override fun getText(): String {
        return "Mark function as throws."
    }

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        try {
            val function = startElement.parentOfType(KtFunction::class) ?: return
            val exceptionType = exceptionTypes.singleOrNull()
            project.executeWriteCommand(text) {
                function.addAnnotationEntry(createThrowsAnnotation(function, exceptionType))
            }
        } catch (e: Exception) {
            //this can throw for some reason... :/
        }
    }

    private fun createThrowsAnnotation(caller: KtFunction, exceptionType: String?): KtAnnotationEntry {
        val text =
                if (exceptionType != null) {
                    "@Throws($exceptionType::class)"
                } else {
                    "@Throws"
                }
        return KtPsiFactory(caller).createAnnotationEntry(text)

    }

    override fun getFamilyName(): String {
        return "csense kotlin checked exceptions - declares func throws"
    }

}