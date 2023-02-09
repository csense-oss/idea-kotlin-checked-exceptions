package csense.idea.kotlin.checked.exceptions.annotator

import com.intellij.lang.annotation.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.settings.*

class ThrowsFunctionAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!Settings.shouldHighlightThrowsInsideOfFunction) {
            return
        }
//        val exp = element as? KtCallExpression ?: return
//        val resultingDescriptor = exp.resolveToCall()?.resultingDescriptor
//        val isNothing = resultingDescriptor?.returnType?.isNothing()
//        if (isNothing != true || resultingDescriptor.typeParameters.isNotEmpty()) {
//            return
//        }
//        if (element.isInTestModule()) {
//            return
//        }
//        val isCalledALambda = (element.resolveMainReference() as? KtParameter)?.typeReference?.isFunctional() == true
//        val isInlineFunctionParent = element.findParentOfType<KtFunction>()?.isInline() == true
//
//        //per spec: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html
//        // for example, if a function has the return type of Nothing, it means that it never returns (always throws an exception or "returns" beforehand).
//        //HOWEVER, if an inline fun returns before something that also results in "Nothing". This means that lambdas return nothing is not "necessary" throwing.
//
//        val message = if (isCalledALambda && isInlineFunctionParent) {
//            "Potentially throws (can return from lambda before control is returned here, or throw an exception)"
//        } else {
//            "Function throws"
//        }
//
//        @Language("HTML")
//        val docLink =
//            "<a href=\"https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html\">https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html</a>"
//
//        holder.newAnnotation(
//            /* severity = */ Settings.throwsInsideOfFunctionSeverity,
//            /* message = */ message,
//        ).tooltip("$message<br/>See documentation $docLink")
//            .range(element)
//            .create()
    }
}
