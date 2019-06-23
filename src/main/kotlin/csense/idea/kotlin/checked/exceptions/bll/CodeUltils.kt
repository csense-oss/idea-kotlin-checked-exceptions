package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.idea.references.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*


fun PsiElement.throwsIfFunction(): Boolean? {
    return throwsTypesIfFunction().isNotNullOrEmpty()
}

fun PsiElement.throwsTypesIfFunction(): List<String>? {
    val result = when (this) {
        is KtNamedFunction -> throwsTypes()
        is PsiMethod -> computeThrowsTypes()
        else -> null
    }
    return result.isNotNullOrEmpty().map(result, null)
}


fun PsiMethod.computeThrowsTypes(): List<String> {
    return throwsTypes.map { it.name }
}

fun KtNamedFunction.throwsTypes(): List<String> {
    val throwsAnnotation = annotationEntries.firstOrNull() {
        it.shortName?.asString() == "Throws"
    } ?: return listOf()
    return if (throwsAnnotation.children.size <= 1) {
        listOf("Exception")
    } else {
        //we have params at index 1
        val eachThrowType = throwsAnnotation.children[1].children
        val asText = eachThrowType.map { it.firstChild.firstChild.text }
        asText
    }
}


fun KtAnnotated.throwsDeclared(): Boolean = annotationEntries.any {
    it.shortName?.asString() == "Throws"
}

fun PsiMethod.throwsExceptions(): Boolean = throwsTypes.isNotEmpty()
/**
 * Resolves the original method.
 * @receiver KtCallExpression
 * @return PsiElement?
 */
fun KtCallExpression.resolveMainReference(): PsiElement? {
    return calleeExpression?.mainReference?.resolve()
}


fun PsiElement.isWrappedInTryCatch(): Boolean {
    var current: PsiElement = this
    while (true) {
        if (current is KtProperty || current is KtFunction) {
            return false
        }
        if (current is KtTryExpression) {
            return true
        }
        current = current.parent ?: return false
    }
}

fun KtElement.isNotWrappedInTryCatch(): Boolean {
    return !isWrappedInTryCatch()
}

fun KtElement.isContainingFunctionMarkedAsThrows(): Boolean {
    var current: PsiElement = this
    while (true) {
        when (current) {
            is KtPropertyAccessor -> return current.throwsDeclared()
            is KtFunction -> return current.throwsDeclared()
            else -> current = current.parent ?: return false
        }

    }
}

fun KtElement.isContainedInFunctionCatching(): Boolean {
    var current: PsiElement = this
    while (true) {
        if (current is KtLambdaExpression && current.parent?.parent is KtCallExpression) {
            val parent = current.parent?.parent as KtCallExpression
            val main = parent.resolveMainReference() as? KtFunction
            //we currently only support 1 parameter.
            if (main != null && main.valueParameters.size == 1) {
                val nameToFindInCode = main.valueParameters.first().name
                if (nameToFindInCode != null) {
                    main.findInvocationOfName(nameToFindInCode)?.isWrappedInTryCatch()?.let {
                        return it
                    }
                }
            }
        }
        current = current.parent ?: return false
    }
}


fun KtFunction.findInvocationOfName(name: String): KtCallExpression? {
    return findDescendantOfType {
        it.text.startsWith(name)
    }
}