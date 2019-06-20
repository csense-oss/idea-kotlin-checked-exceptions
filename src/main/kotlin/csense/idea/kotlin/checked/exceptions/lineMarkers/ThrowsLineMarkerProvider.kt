package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*


class ThrowsLineMarkerProvider : RelatedItemLineMarkerProvider() {


    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>) {
        if (!Settings.shouldHighlightCheckedExceptions) {
            return
        }
        val asMethod = element as? KtCallExpression ?: return
        val method = asMethod.resolveMainReference() ?: return
        if (method.throwsIfFunction() == true) {
            val builder =
                    NavigationGutterIconBuilder
                            .create(IconLoader.getIcon("/icons/throws.png"))
                            .setTargets(asMethod)
                            .setTooltipText("This expression is declared throws")
            result.add(builder.createLineMarkerInfo(element))
        }
    }

}