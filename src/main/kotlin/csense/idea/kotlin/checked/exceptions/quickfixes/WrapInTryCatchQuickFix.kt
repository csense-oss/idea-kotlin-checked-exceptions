package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.util.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*


class WrapInTryCatchQuickFix(
        namedFunction: KtCallExpression,
        exceptionTypes: List<String>) : LocalQuickFixOnPsiElement(namedFunction) {
    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        project.executeWriteCommand(text) {

            val top = startElement.findParentAndChild<KtBlockExpression>() ?: return@executeWriteCommand
            val elementToUse = top.second

            val exceptionType = throwType
            val newElement = createTryCatchWithElement(elementToUse, exceptionType)
            try {
                elementToUse.replace(newElement)
            } catch (e: IncorrectOperationException) {
                TODO("Add error handling here")
            }
        }
    }


    private fun createTryCatchWithElement(element: PsiElement, exceptionType: String): KtTryExpression {
        val tryExpression = KtPsiFactory(element)
                .createExpression("try{\n}\ncatch(e:$exceptionType){ TODO(\"Add error handling here\")}") as KtTryExpression
        tryExpression.tryBlock.addAfter(element, tryExpression.tryBlock.lBrace)
        return tryExpression
    }


    override fun getFamilyName(): String {
        return "csense kotlin checked exceptions- wrap in try catch quick fix"
    }

    override fun getText(): String {
        return "wrap in try catch (\"$throwType\")"
    }

    private val throwType: String =
            exceptionTypes.singleOrNull() ?: kotlinMainExceptionFqName

}

inline fun <reified T : PsiElement> PsiElement.findParentAndChild(): Pair<T, PsiElement>? {
    var currentElement: PsiElement? = this
    var prev = this
    while (currentElement != null) {
        if (currentElement is T) {
            return Pair(currentElement, prev)
        }
        prev = currentElement
        currentElement = currentElement.parent
    }
    return null
}