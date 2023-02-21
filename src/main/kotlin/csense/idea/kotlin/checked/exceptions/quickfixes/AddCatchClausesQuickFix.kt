package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class AddCatchClausesQuickFix(
    tryExpression: KtTryExpression,
    private val uncaughtExceptions: List<KtPsiClass>,
) : LocalQuickFixUpdateCode<KtTryExpression>(element = tryExpression) {

    private val typeListHtml: String by lazy {
        uncaughtExceptions.coloredFqNameString(
            cssColor = IncrementalExceptionCheckerVisitor.typeCssColor
        )
    }

    override fun getFamilyName(): String {
        return "Csense kotlin checked exceptions- add catch(es) quick fix"
    }

    override fun getText(): String {
        return "<html>add catch(es) for $typeListHtml exception(s)</html>"
    }

    override fun tryUpdate(project: Project, file: PsiFile, element: KtTryExpression): PsiElement? {
        uncaughtExceptions.forEach { it: KtPsiClass ->
            val catchClause: KtCatchClause = factory.createCatchClause(
                catchExpression = "exception: ${it.fqName}"
            ) ?: return@forEach
            element.addCatchClauseLast(catchClause)
        }
        return null
    }
}
