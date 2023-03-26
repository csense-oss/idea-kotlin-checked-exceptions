package csense.idea.kotlin.checked.exceptions.quickfixes

//import csense.idea.kotlin.checked.exceptions.callthough.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.psi.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import org.jetbrains.kotlin.psi.*

class AddLambdaToCallthoughQuickFix(
    lambdaFunction: KtFunction,
    private val parameterName: String
) : LocalQuickFixOnSingleKtElement<KtFunction>(lambdaFunction) {
    override fun getFamilyName(): String {
        return "Csense - checked exceptions - add to callthough file quick fix"
    }

    override fun getText(): String {
        return "Mark the lambda argument \"$parameterName\" as passing exceptions on (adds to .callthough.throws file)"
    }

    override fun invoke(project: Project, file: PsiFile, element: KtFunction) {
        val name: String = element.getKotlinFqNameString() ?: return
        CallthoughStorage.forProjectOrNull(project)?.addEntry(
            CallthoughEntry(
                fullName = name,
                parameterName = parameterName
            )
        )
    }

}