package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import com.intellij.psi.tree.*
import csense.idea.base.bll.ast.*
import csense.idea.base.bll.linemarkers.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.builtin.operations.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import csense.idea.kotlin.checked.exceptions.settings.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import org.intellij.lang.annotations.*
import org.jetbrains.kotlin.lexer.*
import org.jetbrains.kotlin.psi.*
import javax.swing.*


/**
 * Highlights method calls  that have checked exceptions associated with them.
 */
class CheckedExceptionLineMarkerProvider : SafeRelatedItemLineMarkerProvider() {

    override fun onCollectNavigationMarkers(
        element: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element.elementType.isNotKtIdentifier()) {
            return
        }
        element.parent.invokeIsInstance { call: KtCallExpression ->
            onCollectNavigationMarkersFor(typedElement = call, leafPsiElement = element, result = result)
        }
        element.parent.parent.invokeIsInstance { call: KtCallExpression ->
            onCollectNavigationMarkersFor(typedElement = call, leafPsiElement = element, result = result)
        }
    }

    fun onCollectNavigationMarkersFor(
        typedElement: KtCallExpression,
        leafPsiElement: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val throwsTypes: List<KtPsiClass> = typedElement.resolveMainReferenceAsFunction()
            ?.throwsTypesForSettings()
            ?: return

        if (throwsTypes.isEmpty()) {
            return
        }
        result += createGutter(
            leafPsiElement = leafPsiElement,
            typesOfExceptions = throwsTypes
        )
    }

    private fun createGutter(
        leafPsiElement: LeafPsiElement,
        typesOfExceptions: List<KtPsiClass>
    ): RelatedItemLineMarkerInfo<PsiElement> {

        val typesString: String = typesOfExceptions.coloredString(
            cssColor = IconThemeColor,
            tagType = "i"
        )

        @Language("html")
        val htmlToolTip = "<html>This expression is declared to throw the following type(s): <b>$typesString</b></html>"
        return NavigationGutterIconBuilder
            .create(exceptionIcon)
            .setTargets(leafPsiElement)
            .setTooltipText(htmlToolTip)
            .createLineMarkerInfo(leafPsiElement)
    }

    companion object {
        val exceptionIcon: Icon by lazy { IconLoader.getIcon("/icons/throws.svg", Companion::class.java) }
        const val IconThemeColor = "#DB5860"
    }
}
