package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.util.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import java.lang.Exception

class DeclareFunctionAsThrowsQuickFix(
        namedFunction: PsiElement,
        private val exceptionTypes: List<String>) : LocalQuickFixOnPsiElement(namedFunction) {

    override fun getText(): String {
        return "Mark function as throws."
    }

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) = tryAndLog {
        val ktElement = startElement as? KtElement ?: return
        val parent: KtModifierListOwner = ktElement.getContainingFunctionOrPropertyAccessor() ?: return
        val exceptionType = exceptionTypes.singleOrNull()
        project.executeWriteCommand(text) {
            parent.addAnnotationEntry(ThrowsAnnotationBll.createThrowsAnnotation(parent, exceptionType))
        }
    } ?: Unit


    override fun getFamilyName(): String {
        return "csense kotlin checked exceptions - declares func throws"
    }

}