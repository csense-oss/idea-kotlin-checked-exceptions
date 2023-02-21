package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
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

    override fun getFamilyName(): String =
        Constants.groupName + " - " + "add throws type to parent scope"


    override fun getText(): String =
        "<html>Add missing $throwsTypesListHtml thrown type to parent scope</html>"

    override fun tryUpdate(
        project: Project,
        file: PsiFile,
        element: KtAnnotated
    ) {
        val throwsAnnotation: KtAnnotationEntry = element.throwsAnnotationOrNull()
            ?: addNewThrowsAnnotationTo(element = element, project = project)
        addThrowsTypesTo(throwsAnnotation = throwsAnnotation)
    }


    private fun addNewThrowsAnnotationTo(
        element: KtAnnotated,
        project: Project
    ): KtAnnotationEntry {
        val newAnnotation: KtAnnotationEntry = createNewThrowsAnnotation(project = project)
        element.addFirst(newAnnotation)
        return newAnnotation
    }

    private fun addThrowsTypesTo(
        throwsAnnotation: KtAnnotationEntry,
    ) {
        throwsAnnotation.valueArgumentList?.addTypeRefs(missingThrowsTypes)
    }


    private fun createNewThrowsAnnotation(
        project: Project
    ): KtAnnotationEntry = KtPsiFactory(
        project = project,
        markGenerated = false
    ).createAnnotationEntry("@Throws")
}

