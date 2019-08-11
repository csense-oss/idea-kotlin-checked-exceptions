package csense.idea.kotlin.checked.exceptions.annotator

import com.intellij.lang.annotation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.intentionAction.*
import org.jetbrains.kotlin.psi.*


class ThrowsAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val throwsExp = element as? KtThrowExpression ?: return

        val range = TextRange(element.getTextRange().startOffset,
                element.getTextRange().endOffset)
        throwsExp.findFunctionScope()?.let {
            if (it.throwsDeclared() || throwsExp.isWrappedInTryCatch()) {
                return
            }
        }

        val throwType = throwsExp.tryAndResolveThrowTypeOrDefault()
        holder.createWarningAnnotation(
                range,
                "Throws \"$throwType\"").registerFix(DeclareFunctionAsThrowsIntentionAction(throwsExp, throwType))
    }

}