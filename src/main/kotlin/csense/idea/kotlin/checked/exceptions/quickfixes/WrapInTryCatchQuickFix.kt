package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*


class WrapInTryCatchQuickFix(namedFunction: KtCallExpression) : LocalQuickFixOnPsiElement(namedFunction) {
    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        project.executeWriteCommand(text) {

            val elementToUse = when (startElement.parent) {
                is KtDotQualifiedExpression -> startElement.parent
                is KtProperty -> startElement
                else -> startElement
            }
            val newElement = createTryCatchWithElement(elementToUse)
            elementToUse.replace(newElement)
        }
    }


    private fun createTryCatchWithElement(element: PsiElement): KtTryExpression {
        val tryExpression = KtPsiFactory(element)
                .createExpression("try{\n}\ncatch(e:Exception){ TODO(\"Add error handling here\")}") as KtTryExpression
        tryExpression.tryBlock.addAfter(element, tryExpression.tryBlock.lBrace)
        return tryExpression
    }


    override fun getFamilyName(): String {
        return "csense kotlin checked exceptions"
    }

    override fun getText(): String {
        return "wrap in try catch"
    }
}