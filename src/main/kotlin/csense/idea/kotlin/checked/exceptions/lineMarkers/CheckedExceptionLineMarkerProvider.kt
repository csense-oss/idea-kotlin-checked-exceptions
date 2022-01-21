package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.cache.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.lexer.*
import org.jetbrains.kotlin.psi.*


/**
 * Highlights method calls  that have checked exceptions associated with them.
 */
class CheckedExceptionLineMarkerProvider : RelatedItemLineMarkerProvider() {
    
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (!Settings.shouldHighlightCheckedExceptions || element !is LeafPsiElement) {
            return
        }
        if (element.elementType != KtTokens.IDENTIFIER){
            return
        }
        val asMethod: KtCallExpression = element.parent as? KtCallExpression
                ?: element.parent?.parent as? KtCallExpression
                ?: return
        val throwsTypes = SharedMethodThrowingCache.throwsTypes(asMethod)
        if (throwsTypes.isEmpty()) {
            return
        }
        val throwsTypesText = throwsTypes.toTypeList().joinToString(", ")
        val builder =
                NavigationGutterIconBuilder
                        .create(exceptionIcon)
                        .setTargets(asMethod)
                        .setTooltipText("This expression is declared to throw the following type(s):\n$throwsTypesText")
        result.add(builder.createLineMarkerInfo(element))
    }
    companion object{
        val exceptionIcon =  IconLoader.getIcon("/icons/throws.svg", CheckedExceptionLineMarkerProvider::class.java)
    }
    
}
