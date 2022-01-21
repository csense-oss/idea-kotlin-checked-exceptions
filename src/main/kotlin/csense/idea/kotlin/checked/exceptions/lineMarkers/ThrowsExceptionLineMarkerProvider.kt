package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import csense.idea.base.bll.uast.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.lexer.*
import org.jetbrains.kotlin.psi.*

/**
 * Highlights "throws" expressions, which is to say, where you throw exceptions.
 *
 */

class ThrowsExceptionLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (!Settings.shouldHighlightCheckedExceptions || element !is LeafPsiElement) {
            return
        }
        if (element.elementType != KtTokens.THROW_KEYWORD) {
            return
        }
        val asMethod = element.parent as? KtThrowExpression ?: return
        val realType = asMethod.thrownExpression?.resolveFirstClassType()
        val uType = realType?.toUExceptionClass()
        if (uType?.isRuntimeExceptionClass() == true && !Settings.runtimeAsCheckedException) {
            return //skip
        }
        //TODO the default here is kinda bogus
        val type = uType?.qualifiedName ?: kotlinMainExceptionFqName
        val builder =
            NavigationGutterIconBuilder
                .create(exceptionIcon)
                .setTargets(asMethod)
                .setTooltipText("You are throwing an exception of type \"$type\"")
        result.add(builder.createLineMarkerInfo(element))
    }

    companion object {
        val exceptionIcon = IconLoader.getIcon("/icons/exception.svg", ThrowsExceptionLineMarkerProvider::class.java)
    }

}