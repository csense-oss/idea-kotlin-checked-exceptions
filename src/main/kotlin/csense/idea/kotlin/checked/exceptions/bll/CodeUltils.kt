package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.*
import com.intellij.psi.util.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.idea.debugger.sequence.psi.*
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.*
import org.jetbrains.kotlin.idea.references.*
import org.jetbrains.kotlin.js.descriptorUtils.*
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

/**
 * Examins the current scope (until a function or property is reached) for a try catch
 * @receiver PsiElement
 * @return Boolean
 */
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
//
//fun KtElement.isContainedInFunctionCatching(): Boolean {
//    var current: PsiElement = this
//    while (true) {
//        if (current is KtLambdaExpression &&
//                (current.parent?.parent is KtCallExpression ||
//                        current.parent?.parent?.parent is KtCallExpression)) {
//            val parent = current.parent?.parent as? KtCallExpression
//                    ?: current.parent?.parent?.parent as KtCallExpression
//            val main = parent.resolveMainReference() as? KtFunction
//
//            val index = current.resolveParameterIndex()
//            if (main != null && index != null && index >= 0) {
//                val nameToFindInCode = main.valueParameters[index].name
//                if (nameToFindInCode != null) {
//                    main.findInvocationOfName(nameToFindInCode)?.isWrappedInTryCatch()?.let {
//                        return it
//                    }
//                }
//            }
//        }
//        current = current.parent ?: return false
//    }
//}

fun KtLambdaExpression.resolveParameterIndex(): Int? {
    val callExp =
            parent?.parent as? KtCallExpression
                    ?: parent?.parent?.parent as? KtCallExpression
                    ?: return null
    return callExp.getParameterInfos().indexOfFirst {
        (it.typeInfo as?  TypeInfo.ByExpression)?.expression === this
    }
}

fun KtFunction.findInvocationOfName(name: String): KtCallExpression? {
    return findDescendantOfType {
        it.text.startsWith(name)
    }
}


/**
 * Tries to resolve the function we are in.
 * @receiver KtExpression
 */
fun KtExpression.findFunctionScope(): KtFunction? = parentOfType(KtFunction::class)

/**
 * Tries to resolve the type of a throws expression in kotlin.
 * @receiver KtThrowExpression
 * @return String?
 */
fun KtThrowExpression.tryAndResolveThrowType(): String? {
    val throwTypeExpression = children.firstOrNull() as? KtExpression
    return try {
        throwTypeExpression?.resolveType()?.constructor?.declarationDescriptor?.name?.identifier
    } catch (E: Exception) {
        null
    }
}