package csense.idea.kotlin.checked.exceptions.lineMarkers

import com.intellij.codeInsight.daemon.*
import com.intellij.codeInsight.navigation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.*
import csense.idea.base.bll.linemarkers.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import csense.idea.kotlin.checked.exceptions.settings.*
import csense.kotlin.extensions.*
import org.intellij.lang.annotations.*
import org.jetbrains.kotlin.psi.*
import javax.swing.*


/**
 * Highlights method calls  that have checked exceptions associated with them.
 */
class CheckedExceptionLineMarkerProvider : AbstractSafeRelatedItemLineMarkerProvider<KtCallExpression>(type()) {

//    override fun onCollectNavigationMarkers(
//        element: LeafPsiElement,
//        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
//    ) {

//        if (element.elementType != KtTokens.IDENTIFIER) {
//            return
//        }
//        val asMethod: KtCallExpression = element.parent as? KtCallExpression
//            ?: element.parent?.parent as? KtCallExpression
//            ?: return
//        val throwsTypes = SharedMethodThrowingCache.throwsTypes(asMethod)
//        if (throwsTypes.isEmpty()) {
//            return
//        }
//        val throwsTypesText = throwsTypes.toTypeList().joinToString(", ")
//        val builder =
//            NavigationGutterIconBuilder
//                .create(exceptionIcon)
//                .setTargets(asMethod)
//                .setTooltipText("This expression is declared to throw the following type(s):\n$throwsTypesText")
//        result.add(builder.createLineMarkerInfo(element))
//    }

    override fun onCollectNavigationMarkersFor(
        typedElement: KtCallExpression,
        leafPsiElement: LeafPsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (!Settings.shouldHighlightCheckedExceptions) {
            return
        }
        val throwsTypes: List<KtPsiClass> =
            typedElement.resolveMainReferenceAsFunction()?.throwsTypes()?.filterRuntimeExceptionsBySettings() ?: return
        if (throwsTypes.isNotEmpty()) {
            result += createGutter(
                leafPsiElement = leafPsiElement,
                typesOfExceptions = throwsTypes
            )
        }
    }

    private fun createGutter(
        leafPsiElement: LeafPsiElement,
        typesOfExceptions: List<KtPsiClass>
    ): RelatedItemLineMarkerInfo<PsiElement> {
        val throwsTypesText: String =
            typesOfExceptions.joinToString(separator = "</i>,<i>", prefix = "<i>", postfix = "</i>") {
                it.fqName ?: ""
            }

        @Language("html")
        val htmlToolTip =
            "<html>This expression is declared to throw the following type(s): <b style=\"color:${IconThemeColor}\">$throwsTypesText</b></html>"
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
