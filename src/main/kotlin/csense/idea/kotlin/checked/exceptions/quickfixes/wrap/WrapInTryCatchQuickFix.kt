package csense.idea.kotlin.checked.exceptions.quickfixes.wrap

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class WrapInTryCatchQuickFix(
    namedFunction: KtCallExpression,
    @Suppress("ActionIsNotPreviewFriendly")
    private val uncaughtExceptions: List<KtPsiClass>,
) : BaseLocalQuickFixUpdateCode<KtCallExpression>(element = namedFunction) {

    @Suppress("ActionIsNotPreviewFriendly")
    private val typeListHtml: String by lazy {
        uncaughtExceptions.coloredFqNameString(
            cssColor = IncrementalExceptionCheckerVisitor.typeCssColor
        )
    }

    @Throws(com.intellij.util.IncorrectOperationException::class)
    override fun tryUpdate(project: Project, file: PsiFile, element: KtCallExpression): PsiElement {
        val top: KtElement = element.tryResolveTopExpressionInBlock()
        val newElement: KtExpression = createTryCatchWithElement(top, forFile = file)
        return top.replace(newElement)
    }

    //TODO walkWhile?
    private fun KtElement.tryResolveTopExpressionInBlock(): KtElement {
        var currentElement: KtElement = this
        while (true) {
            val parent: PsiElement = currentElement.parent
            if (parent !is KtElement || parent is KtBlockExpression || parent is KtProperty) {
                break
            }
            currentElement = parent
        }
        return currentElement
    }

    private fun createTryCatchWithElement(
        element: PsiElement,
        forFile: PsiFile,
    ): KtExpression {
        val block: KtBlockExpression = factory.createBlock(createCode(element.text, forFile))
        return block.statements.singleOrNull() ?: block
    }


    private fun createCode(oldCode: String, forFile: PsiFile): String {
        val catches: String = uncaughtExceptions.joinToString(separator = "\n", transform = { it: KtPsiClass ->
            it.catchParameterCode(forFile = forFile)
        })

        //language=kotlin
        val result: String = """
            try {
                $oldCode
            }$catches
        """.trimIndent()
        return result
    }


    override fun getActionText(): String = "wrap in try catch quick fix"

    override fun getText(): String {
        return "<html>wrap in try catch for $typeListHtml exception(s)</html>"
    }
}

fun KtPsiClass.catchParameterCode(forFile: PsiFile): String {
    val exceptionName: String = codeNameToUseBasedOnImports(file = forFile)

    //language=kotlin
    val result = """
            catch(exception: $exceptionName){
                TODO("Add error handling here")
            }
        """.trimIndent()
    return result
}