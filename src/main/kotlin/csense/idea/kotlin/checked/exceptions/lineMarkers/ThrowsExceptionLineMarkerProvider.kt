package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*

/**
 * Highlights "throws" expressions, which is to say, where you throw exceptions.
 *
 */

class ThrowsExceptionLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>) {
        if (!Settings.shouldHighlightThrowsExceptions) {
            return
        }
        val asMethod = element as? KtThrowExpression ?: return

        val type = asMethod.tryAndResolveThrowTypeOrDefault()
        val builder =
                NavigationGutterIconBuilder
                        .create(exceptionIcon)
                        .setTargets(asMethod)
                        .setTooltipText("You are throwing an exception of type \"$type\"")
        result.add(builder.createLineMarkerInfo(element))
    }
    companion object{
        val exceptionIcon = IconLoader.getIcon("/icons/exception.svg")
    }

}