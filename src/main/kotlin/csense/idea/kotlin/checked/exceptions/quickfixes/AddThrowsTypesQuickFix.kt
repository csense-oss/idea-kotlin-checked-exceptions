package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

class AddThrowsTypesQuickFix(
    toExpression: KtAnnotated,
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
        when (val throwsAnnotation: KtAnnotationEntry? = element.throwsAnnotationOrNull()) {
            null -> addNewThrowsAnnotationTo(element = element)
            else -> throwsAnnotation.addThrowsTypes()
        }
        return null
    }


    private fun addNewThrowsAnnotationTo(
        element: KtAnnotated
    ) {
        val newAnnotation: KtAnnotationEntry = createNewThrowsAnnotation()
        newAnnotation.addThrowsTypes()
        element.addFirst(newAnnotation)
    }

    private fun KtAnnotationEntry.addThrowsTypes() {
        valueArgumentList?.addTypeRefs(missingThrowsTypes, forFile = containingFile)
    }


    private fun createNewThrowsAnnotation(): KtAnnotationEntry {
        return factory.createAnnotationEntry("@Throws()")
    }
}

