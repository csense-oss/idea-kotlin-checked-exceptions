package csense.idea.kotlin.checked.exceptions.annotator

import com.intellij.lang.annotation.*
import com.intellij.openapi.module.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import csense.idea.base.module.isInTestModule
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.intentionAction.*
import csense.kotlin.extensions.primitives.containsAny
import org.jetbrains.kotlin.idea.util.projectStructure.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.types.typeUtil.*

class ThrowsFunctionAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val exp = element as? KtCallExpression ?: return
        val resultingDescriptor = exp.resolveToCall()?.resultingDescriptor
        val isNothing = resultingDescriptor?.returnType?.isNothing()
        if (isNothing != true || resultingDescriptor.typeParameters.isNotEmpty()) {
            return
        }
        //skip if test module and it contains either test or fail or assert
        if (element.isInTestModule() &&
                resultingDescriptor.name.asString().containsAny(
                        "fail", "test", "assert",
                        ignoreCase = true)) {
            return
        }
        //per spec: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html
        // for example, if a function has the return type of Nothing, it means that it never returns (always throws an exception).
        val range = TextRange(element.getTextRange().startOffset,
                element.getTextRange().endOffset)
        holder.createWarningAnnotation(
                range,
                "Throws inside of function")
    }
}
