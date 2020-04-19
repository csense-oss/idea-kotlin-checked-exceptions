package csense.idea.kotlin.checked.exceptions.quickfixes

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.callthough.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import org.jetbrains.kotlin.psi.*

class AddLambdaToCallthoughQuickFix(
        lambdaFunction: KtFunction,
        private val parameterName: String
) : LocalQuickFixOnPsiElement(lambdaFunction) {
    override fun getFamilyName(): String {
        return "csense - checked exceptions - add to callthough file quick fix"
    }
    
    override fun getText(): String {
        return "Add lambda parameter to callthough file"
    }
    
    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val name = (startElement as KtFunction).fqName?.asString() ?: return
        CallthoughStorage.addEntry(project, CallthoughEntry(name, parameterName))
    }
    
}