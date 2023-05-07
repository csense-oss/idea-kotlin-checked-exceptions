package csense.idea.kotlin.checked.exceptions.quickfixes.add


import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.repo.*
import org.jetbrains.kotlin.psi.*

class AddLambdaToIgnoreQuickFix(
    lambdaLookup: LambdaArgumentLookup
) : LocalQuickFixOnSingleKtElement<KtFunction>(lambdaLookup.parentFunction) {

    @Suppress("ActionIsNotPreviewFriendly")
    private val repo: IgnoreRepo by lazy {
        IgnoreRepo(project)
    }

    private val parameterName: String? =
        lambdaLookup.parameterName

    private val parentFunctionFqName: String? =
        lambdaLookup.parentFunctionFqName

    override fun getFamilyName(): String {
        return "${Constants.groupName} - add to ignore quick fix"
    }

    override fun getText(): String {
        return "Ignore exceptions thrown in the lambda argument \"$parameterName\" (adds a new entry to the \"${IgnoreRepo.ignoreFileName}\" file)"
    }

    override fun invoke(project: Project, file: PsiFile, element: KtFunction) {
        repo.addEntry(
            fqName = parentFunctionFqName ?: return,
            parameterName = parameterName ?: return
        )
    }

}