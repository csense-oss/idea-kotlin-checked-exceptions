package csense.idea.kotlin.checked.exceptions.quickfixes.add

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import org.jetbrains.kotlin.psi.*

class AddLambdaToCallthoughQuickFix(
    lambdaLookup: LambdaArgumentLookup
) : LocalQuickFixOnSingleKtElement<KtFunction>(lambdaLookup.parentFunction) {

    private val parameterName: String? =
        lambdaLookup.parameterName

    private val fqName: String? =
        lambdaLookup.parentFunctionFqName

    @Suppress("ActionIsNotPreviewFriendly")
    private val repo: CallThoughRepo by lazy {
        CallThoughRepo(project)
    }

    override fun getFamilyName(): String {
        return "${Constants.groupName} - add to callthough file quick fix"
    }

    override fun getText(): String {
        return "Mark the lambda argument \"$parameterName\" as passing exceptions on (adds a new entry to the \"${CallThoughRepo.callthoughProjectFileName}\" file)"
    }

    override fun invoke(project: Project, file: PsiFile, element: KtFunction) {
        repo.addEntry(
            fqName = fqName ?: return,
            parameterName = parameterName ?: return
        )
    }

}