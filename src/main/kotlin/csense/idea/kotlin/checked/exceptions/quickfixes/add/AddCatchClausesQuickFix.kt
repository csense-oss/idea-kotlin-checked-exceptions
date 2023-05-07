package csense.idea.kotlin.checked.exceptions.quickfixes.add

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class AddCatchClausesQuickFix(
    tryExpression: KtTryExpression,
    @Suppress("ActionIsNotPreviewFriendly")
    private val uncaughtExceptions: List<KtPsiClass>,
) : BaseLocalQuickFixUpdateCode<KtTryExpression>(element = tryExpression) {

    @Suppress("ActionIsNotPreviewFriendly")
    private val typeListHtml: String by lazy {
        uncaughtExceptions.coloredFqNameString(
            cssColor = IncrementalExceptionCheckerVisitor.typeCssColor
        )
    }

    override fun getActionText(): String = "add catch(es) quick fix"

    override fun getText(): String {
        return "<html>add catch(es) for $typeListHtml exception(s)</html>"
    }

    override fun tryUpdate(project: Project, file: PsiFile, element: KtTryExpression): PsiElement? {
        val catchClauses: List<KtCatchClause> = uncaughtExceptions.createCatchClausesFor(file)
        element.addCatchClausesLast(catchClauses)
        return null
    }


    private fun List<KtPsiClass>.createCatchClausesFor(file: PsiFile): List<KtCatchClause> {
        return mapNotNull { it: KtPsiClass ->
            createCatchClausForOrNull(it, file)
        }
    }

    private fun createCatchClausForOrNull(ktPsiClass: KtPsiClass, file: PsiFile): KtCatchClause? {
        val exceptionName: String = ktPsiClass.codeNameToUseBasedOnImports(file)
        return factory.createCatchClause(
            catchExpression = "exception: $exceptionName"
        )
    }
}
