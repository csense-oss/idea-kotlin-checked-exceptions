package csense.idea.kotlin.checked.exceptions.quickfixes


import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.ignore.*
import org.jetbrains.kotlin.psi.*

class AddLambdaToIgnoreQuickFix(
    lambdaFunction: KtFunction,
    private val parameterName: String
) : LocalQuickFixOnSingleKtElement<KtFunction>(lambdaFunction) {
    override fun getFamilyName(): String {
        return "Csense - checked exceptions - add to ignore quick fix"
    }

    override fun getText(): String {
        return "Ignore exceptions thrown in the lambda argument \"$parameterName\" (adds to .ignore.throws file)"
    }

    override fun invoke(project: Project, file: PsiFile, element: KtFunction) {
        val name: String = element.fqName?.asString() ?: return
        IgnoreStorage.addEntry(
            project = project,
            entry = IgnoreEntry(
                fullName = name,
                parameterName = parameterName
            )
        )
    }

}