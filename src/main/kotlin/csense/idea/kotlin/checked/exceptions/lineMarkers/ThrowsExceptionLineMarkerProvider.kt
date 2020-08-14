package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import csense.idea.base.bll.uast.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.lexer.*
import org.jetbrains.kotlin.psi.*
import javax.crypto.*

/**
 * Highlights "throws" expressions, which is to say, where you throw exceptions.
 *
 */

class ThrowsExceptionLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>) {
        if (!Settings.shouldHighlightCheckedExceptions || element !is LeafPsiElement) {
            return
        }
        if (element.elementType != KtTokens.IDENTIFIER){
            return
        }
        
        val asMethod = element.parent as? KtThrowExpression
                ?: element.parent?.parent as? KtThrowExpression
                ?: element.parent?.parent?.parent as? KtThrowExpression
                ?: return
        val uType = asMethod.tryAndResolveThrowTypeOrDefaultUClass()
        if(uType?.isRuntimeExceptionClass() == true && !Settings.runtimeAsCheckedException){
            return //skip
        }
        val type = uType?.qualifiedName ?:kotlinMainExceptionFqName
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