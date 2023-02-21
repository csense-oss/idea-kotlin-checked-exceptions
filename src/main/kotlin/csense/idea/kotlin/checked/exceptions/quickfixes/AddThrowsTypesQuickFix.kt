package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.psi.*

//TODO SHORTEN NAMES!??!?!? IMPORTS!!??!?! :(((((((((((((((((((((
//TODO make better base class? hmmmmmmmmmmm
class AddThrowsTypesQuickFix(
    toExpression: KtAnnotated,
    private val missingThrowsTypes: List<KtPsiClass>
) : LocalQuickFixUpdateCode<KtAnnotated>(toExpression) {

    private val throwsTypesListHtml: String = missingThrowsTypes.coloredFqNameString(
        cssColor = IncrementalExceptionCheckerVisitor.typeCssColor
    )

    private val missingThrowsFqNamesRef: String by lazy {
        missingThrowsTypes.joinToString(separator = ",") { it: KtPsiClass ->
            it.fqNameRef()
        }
    }


    override fun getFamilyName(): String =
        Constants.groupName + " - " + "add throws type to parent scope"


    override fun getText(): String =
        "<html>Add missing $throwsTypesListHtml thrown type to parent scope</html>"

    override fun tryUpdate(
        project: Project,
        file: PsiFile,
        element: KtAnnotated
    ): PsiElement? {
        val throwsAnnotation: KtAnnotationEntry? = element.throwsAnnotationOrNull()
        return when (throwsAnnotation) {
            null -> addThrowsAnnotationTo(element = element, project = project)
            else -> addThrowsTypesTo(throwsAnnotation = throwsAnnotation)
        }
    }

    private fun addThrowsTypesTo(
        throwsAnnotation: KtAnnotationEntry,
    ): PsiElement {
        throwsAnnotation.valueArgumentList?.addTypeRefs(missingThrowsTypes)
        return throwsAnnotation
    }

    private fun addThrowsAnnotationTo(
        element: KtAnnotated,
        project: Project
    ): PsiElement? {
        val newAnnotation: KtAnnotationEntry =
            createThrowsAnnotationCode(throwsTypesCode = missingThrowsFqNamesRef, project = project)
        return element.addBefore(
            /* element = */ newAnnotation,
            /* anchor = */ element.firstChild
        )
    }

    private fun createThrowsAnnotationCode(
        throwsTypesCode: String,
        project: Project
    ): KtAnnotationEntry {
        return KtPsiFactory(
            project = project,
            markGenerated = false
        ).createAnnotationEntry("@Throws($throwsTypesCode)")
    }
}
