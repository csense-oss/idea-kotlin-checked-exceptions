package csense.idea.kotlin.checked.exceptions.quickfixes.add

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class AddThrowsTypesQuickFix(
    toExpression: KtAnnotated,
    @Suppress("ActionIsNotPreviewFriendly")
    private val missingThrowsTypes: List<KtPsiClass>
) : BaseLocalQuickFixUpdateCode<KtAnnotated>(toExpression) {

    private val throwsTypesListHtml: String = missingThrowsTypes.coloredFqNameString(
        cssColor = IncrementalExceptionCheckerVisitor.typeCssColor
    )

    override fun getActionText(): String = "add throws type to parent scope"


    override fun getText(): String =
        "<html>Add missing $throwsTypesListHtml thrown type to parent scope</html>"

    override fun tryUpdate(
        project: Project,
        file: PsiFile,
        element: KtAnnotated
    ): PsiElement? {
        return when (val throwsAnnotation: KtAnnotationEntry? = element.throwsAnnotationOrNull()) {
            null -> addNewThrowsAnnotationTo(element = element, file = file)
            else -> throwsAnnotation.addThrowsTypes()
        }
    }


    private fun addNewThrowsAnnotationTo(
        element: KtAnnotated,
        file: PsiFile
    ): PsiElement? {
        val replacement: KtNamedFunction = factory.createFunction(
            """
            @Throws(${missingThrowsTypes.joinToString { it.nameRef(file) }})
            ${element.text}
        """.trimIndent()
        )
        return element.replace(replacement)
    }

    private fun KtAnnotationEntry.addThrowsTypes(): PsiElement? {
        valueArgumentList?.addTypeRefs(missingThrowsTypes, forFile = containingFile)
        return null
    }
}

