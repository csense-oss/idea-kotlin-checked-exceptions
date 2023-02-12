package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import com.intellij.psi.tree.*
import csense.idea.base.bll.ast.*
import csense.idea.base.bll.kotlin.*
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
        tryParentsAsCallExpression(element, result)
        tryParentsAsPropertyNamedExpression(element, result)
    }

    private fun tryParentsAsPropertyNamedExpression(
        leaf: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        leaf.parent.invokeIsInstance { call: KtSimpleNameExpression ->
            onNameExpression(element = call, leafPsiElement = leaf, result = result)
        }
        leaf.parent.parent.invokeIsInstance { call: KtSimpleNameExpression ->
            onNameExpression(element = call, leafPsiElement = leaf, result = result)
        }
    }

    private fun onNameExpression(
        element: KtSimpleNameExpression,
        leafPsiElement: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val resolvedAsKtProperty: KtProperty = element.resolveAsKtProperty() ?: return
        val throwsNonEmpty: List<KtPsiClass> = resolvedAsKtProperty.throwsTypesWithGetter().nullOnEmpty() ?: return

        result += createGutter(
            leafPsiElement = leafPsiElement,
            typesOfExceptions = throwsNonEmpty
        )
    }

    private fun tryParentsAsCallExpression(
        leaf: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        leaf.parent.invokeIsInstance { call: KtCallExpression ->
            onCallExpression(element = call, leafPsiElement = leaf, result = result)
        }
        leaf.parent.parent.invokeIsInstance { call: KtCallExpression ->
            onCallExpression(element = call, leafPsiElement = leaf, result = result)
        }

    }


    private fun onCallExpression(
        element: KtCallExpression,
        leafPsiElement: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val throwsNonEmpty: List<KtPsiClass> = element.resolveMainReferenceAsFunction()
            ?.throwsTypesForSettings()
            .nullOnEmpty()
            ?: return

        result += createGutter(
            leafPsiElement = leafPsiElement,
            typesOfExceptions = throwsNonEmpty
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
        val htmlToolTip = "<html>This expression is declared to throw <b>$typesString</b></html>"
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
