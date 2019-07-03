package csense.idea.kotlin.checked.exceptions.intentionAction

import com.intellij.codeInsight.intention.impl.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*

class DeclareFunctionAsThrowsIntentionAction(
        private val throwsExp: KtThrowExpression,
        val throwType: String
) : BaseIntentionAction() {
    override fun getFamilyName(): String =
            "Csense checked exceptions - intention action"

    override fun getText(): String =
            "Mark function as throws"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        WriteCommandAction.writeCommandAction(project).run<Throwable> {
            throwsExp.findFunctionScope()?.let {
                it.addAnnotationEntry(createThrowsAnnotation(it, throwType))
            }
        }
    }


    private fun createThrowsAnnotation(caller: KtFunction, exceptionType: String?): KtAnnotationEntry {
        val text =
                if (exceptionType != null) {
                    "@Throws($exceptionType::class)"
                } else {
                    "@Throws"
                }
        return KtPsiFactory(caller).createAnnotationEntry(text)

    }
}
