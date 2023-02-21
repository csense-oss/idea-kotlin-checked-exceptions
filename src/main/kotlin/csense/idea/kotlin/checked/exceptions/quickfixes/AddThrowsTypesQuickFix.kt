package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.codeStyle.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.linemarkers.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.visitors.*
import org.jetbrains.kotlin.idea.core.*
import org.jetbrains.kotlin.idea.util.application.*
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
    ): PsiElement? {
        val throwsAnnotation: KtAnnotationEntry? = element.throwsAnnotationOrNull()
        return when (throwsAnnotation) {
            null -> addThrowsAnnotationTo(element = element, project = project)
            else -> addThrowsTypesTo(throwsAnnotation = throwsAnnotation, project = project)
        }
    }

    private fun addThrowsTypesTo(
        throwsAnnotation: KtAnnotationEntry,
        project: Project
    ): PsiElement? {
        val updatedAnnotation: KtAnnotationEntry = createAnnotationCode(throwsTypesCode = "", project = project)
        return throwsAnnotation.replace(updatedAnnotation)
    }

    private fun addThrowsAnnotationTo(
        element: KtAnnotated,
        project: Project
    ): PsiElement? {
        val typeNames: String = missingThrowsTypes.joinToString(separator = ",") { it: KtPsiClass ->
            it.fqName + "::class"
        }
        val newAnnotation: KtAnnotationEntry = createAnnotationCode(throwsTypesCode = typeNames, project = project)
        return element.addBefore(
            /* element = */ newAnnotation,
            /* anchor = */ element.firstChild
        )
    }

    private fun createAnnotationCode(
        throwsTypesCode: String,
        project: Project
    ): KtAnnotationEntry {
        return KtPsiFactory(
            project = project,
            markGenerated = false
        ).createAnnotationEntry("@Throws($throwsTypesCode)")
    }


}
//TODO base module ->

abstract class LocalQuickFixUpdateCode<T : KtElement>(
    element: T
) : LocalQuickFixOnSingleKtElement<T>(element) {

    final override fun invoke(project: Project, file: PsiFile, element: T) {
        if (!element.isWritable) {
            return
        }
        val updatedElement: PsiElement = project.executeWriteCommand(
            name = this::class.simpleName ?: name,
            groupId = familyName
        ) {
            tryUpdate(
                project = project,
                file = file,
                element = element
            )
        } ?: return
        reformat(project = project, element = updatedElement)
    }

    abstract fun tryUpdate(project: Project, file: PsiFile, element: T): PsiElement?

    fun reformat(project: Project, element: PsiElement): PsiElement {
        val styleManager: CodeStyleManager = CodeStyleManager.getInstance(project)
        return styleManager.reformat(/* element = */ element)
    }

}

abstract class LocalQuickFixOnSingleKtElement<T : KtElement>(
    element: T
) : LocalQuickFixOnPsiElement(element) {

    @Suppress("UNCHECKED_CAST")
    final override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val elementToUse: T = startElement as? T ?: return
        invoke(project = project, file = file, element = elementToUse)
    }

    abstract fun invoke(project: Project, file: PsiFile, element: T)

}

