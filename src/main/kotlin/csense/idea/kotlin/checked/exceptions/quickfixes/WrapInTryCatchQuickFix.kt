package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.psi.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.intellij.lang.annotations.*
import org.jetbrains.kotlin.psi.*


class WrapInTryCatchQuickFix(
    namedFunction: KtCallExpression,
    private val uncaughtExceptions: List<KtPsiClass>,
) : LocalQuickFixUpdateCode<KtCallExpression>(element = namedFunction) {

    private val typeListHtml: String by lazy {
        uncaughtExceptions.coloredFqNameString(
            cssColor = IncrementalExceptionCheckerVisitor.typeCssColor
        )
    }

    override fun tryUpdate(project: Project, file: PsiFile, element: KtCallExpression): PsiElement {
        val newElement: KtExpression = createTryCatchWithElement(element)
        return element.replace(newElement)
    }


    private fun createTryCatchWithElement(
        element: PsiElement
    ): KtExpression {
        val block: KtBlockExpression = factory.createBlock(createCode(element.text))
        return block.statements.singleOrNull() ?: block
    }


    private fun createCode(oldCode: String): String {
        val catches: String = uncaughtExceptions.joinToString(separator = "\n", transform = { it: KtPsiClass ->
            it.catchParameter()
        })

        @Language("kotlin")
        val result: String = """
            try {
                $oldCode
            }$catches
        """.trimIndent()
        return result
    }

    private fun KtPsiClass.catchParameter(): String {
        @Language("kotlin")
        val result = """
            catch(exception: $fqName){
                TODO("Add error handling here")
            }
        """.trimIndent()
        return result
    }


    override fun getFamilyName(): String {
        return "Csense kotlin checked exceptions- wrap in try catch quick fix"
    }

    override fun getText(): String {
        return "<html>wrap in try catch for $typeListHtml exception(s)</html>"
    }
}