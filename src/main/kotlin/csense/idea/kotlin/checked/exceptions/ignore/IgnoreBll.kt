package csense.idea.kotlin.checked.exceptions.ignore

import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import org.jetbrains.kotlin.psi.*

fun KtElement.isContainedInFunctionCatchingOrIgnored(ignoreInMemory: IgnoreInMemory): Boolean {
    var current: PsiElement = this
    while (true) {
        if (current is KtLambdaExpression &&
                (current.parent?.parent is KtCallExpression ||
                        current.parent?.parent?.parent is KtCallExpression)) {
            val parent = current.parent?.parent as? KtCallExpression
                    ?: current.parent?.parent?.parent as KtCallExpression
            val main = parent.resolveMainReference() as? KtFunction

            val index = current.resolveParameterIndex()
            if (main != null && index != null && index >= 0) {
                val nameToFindInCode = main.valueParameters[index].name
                if (nameToFindInCode != null) {
                    if (ignoreInMemory.isArgumentMarkedAsIgnore(main, nameToFindInCode)) {
                        return true
                    }
                    //TODO find invocationS , there could be multiple !!
                    main.findInvocationOfName(nameToFindInCode)?.isWrappedInTryCatch()?.let {
                        return it
                    }
                }
            }
        }
        current = current.parent ?: return false
    }
}