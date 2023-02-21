package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.psi.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*


class WrapInTryCatchQuickFix(
    namedFunction: KtCallExpression,
    exceptionTypes: List<String>
) : LocalQuickFixUpdateCode<KtCallExpression>(
    namedFunction
) {

    override fun tryUpdate(project: Project, file: PsiFile, element: KtCallExpression): PsiElement? {
        val top: Pair<KtBlockExpression, PsiElement> = startElement.findParentAndBeforeFromType() ?: return null
        val elementToUse: PsiElement = top.second

        val newElement: KtTryExpression = createTryCatchWithElement(elementToUse, throwType)
        return elementToUse.replace(newElement)
    }


    private fun createTryCatchWithElement(element: PsiElement, exceptionType: String): KtTryExpression {
        val tryExpression: KtTryExpression = KtPsiFactory(element)
            .createExpression("try{\n}\ncatch(e:$exceptionType){ TODO(\"Add error handling here\")}") as KtTryExpression
        tryExpression.tryBlock.addAfter(element, tryExpression.tryBlock.lBrace)
        return tryExpression
    }


    override fun getFamilyName(): String {
        return "Csense kotlin checked exceptions- wrap in try catch quick fix"
    }

    override fun getText(): String {
        return "wrap in try catch (\"$throwType\")"
    }

    private val throwType: String =
        exceptionTypes.singleOrNull() ?: Constants.kotlinMainExceptionFqName

}