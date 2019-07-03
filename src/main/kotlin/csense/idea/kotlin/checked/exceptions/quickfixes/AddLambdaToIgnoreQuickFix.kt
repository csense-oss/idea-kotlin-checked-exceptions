package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import org.jetbrains.kotlin.psi.*

class AddLambdaToIgnoreQuickFix(
        lambdaFunction: KtFunction,
        private val parameterName: String
) : LocalQuickFixOnPsiElement(lambdaFunction) {
    override fun getFamilyName(): String {
        return "csense - checked exceptions - add to ignore quick fix"
    }

    override fun getText(): String {
        return "Add lambda parameter to ignore throws file"
    }

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val name = (startElement as KtFunction).fqName?.asString() ?: return
        IgnoreStorage.addEntry(project, IgnoreEntry(name, parameterName))
    }

}