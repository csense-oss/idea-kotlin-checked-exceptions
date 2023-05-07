package csense.idea.kotlin.checked.exceptions.quickfixes

import csense.idea.base.bll.quickfixes.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*

abstract class BaseLocalQuickFixUpdateCode<T : KtElement>(
    element: T
) : LocalQuickFixUpdateCode<T>(
    element
) {

    override fun getFamilyName(): String {
        return "${Constants.groupName} - ${getActionText()}"
    }

    abstract fun getActionText(): String

}