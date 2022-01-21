package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*

class AddFunctionThrowsQuickFix(
    namedFunction: PsiElement,
    private val exceptionTypes: List<String>
) : LocalQuickFixOnPsiElement(namedFunction) {

    override fun getText(): String {
        return "Add throws type to function (\"$throwType\")"
    }

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) = tryAndLog {
        val ktElement = startElement as? KtElement ?: return@tryAndLog
        val parent: KtModifierListOwner = ktElement.getContainingFunctionOrPropertyAccessor() ?: return@tryAndLog
        val throws = parent.annotationEntries.findThrows() ?: return@tryAndLog
        val exceptionType = exceptionTypes.singleOrNull()
        val throwsNotNull = throws.valueArguments.mapNotNull { it.getArgumentExpression()?.text } + exceptionType
        val fullText = throwsNotNull.joinToString(", ")
        project.executeWriteCommand(text) {
            throws.replace(ThrowsAnnotationBll.createThrowsAnnotation(parent, fullText))
        }
    } ?: Unit


    override fun getFamilyName(): String {
        return "Csense kotlin checked exceptions - add throw type to throws annotation"
    }

    private val throwType: String =
        exceptionTypes.singleOrNull() ?: kotlinMainExceptionFqName
}