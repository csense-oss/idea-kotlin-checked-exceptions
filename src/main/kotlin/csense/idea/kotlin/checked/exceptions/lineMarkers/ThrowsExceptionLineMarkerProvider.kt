package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import csense.idea.base.bll.annotator.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.linemarkers.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.kotlin.extensions.*
import org.intellij.lang.annotations.*
import org.jetbrains.kotlin.psi.*
import javax.swing.*

/**
 * Highlights "throws" expressions
 */

class ThrowsExceptionLineMarkerProvider : AbstractSafeRelatedItemLineMarkerProvider<KtThrowExpression>(
    classType = type()
) {

    override fun onCollectNavigationMarkersFor(
        typedElement: KtThrowExpression,
        leafPsiElement: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val thrownType: KtPsiClass = typedElement.resolveThrownTypeOrNull() ?: return
        val gutter: RelatedItemLineMarkerInfo<PsiElement> = createGutter(
            forElement = leafPsiElement,
            type = thrownType.getFqNameTypeAliased() ?: "",
            isRuntimeException = thrownType.isSubtypeOfRuntimeException()
        )
        if (result.doesNotContain(gutter)) {
            result += gutter
        }
    }

    private fun MutableCollection<in RelatedItemLineMarkerInfo<*>>.doesNotContain(
        gutter: RelatedItemLineMarkerInfo<*>
    ): Boolean = none { it: Any? ->
        val marker: RelatedItemLineMarkerInfo<*> = it as? RelatedItemLineMarkerInfo<*> ?: return@none false
        marker.element == gutter.element && marker.lineMarkerTooltip == gutter.lineMarkerTooltip
    }

    private fun createGutter(
        forElement: PsiElement,
        type: String,
        isRuntimeException: Boolean
    ): RelatedItemLineMarkerInfo<PsiElement> {
        @Language("html")
        val runtimeExceptionText: String = when (isRuntimeException) {
            true -> "(subtype of <i style=\"color:$iconColorTheme\">RuntimeException</i>)"
            false -> ""
        }

        @Language("html")
        val htmlToolTip =
            "<html>Throwing exception <b style=\"color:$iconColorTheme\">$type</b>$runtimeExceptionText</html>"
        return NavigationGutterIconBuilder
            .create(exceptionIcon)
            .setTargets(forElement)
            .setTooltipText(htmlToolTip)
            .createLineMarkerInfo(forElement)
    }

    companion object {
        val exceptionIcon: Icon = IconLoader.getIcon("/icons/exception.svg", Companion::class.java)
        const val iconColorTheme = "#EDA200"
    }

}
