package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import org.jetbrains.kotlin.idea.debugger.sequence.psi.*
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.idea.references.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.types.*


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
        val asText = eachThrowType.map { it.firstChild.firstChild.text } // to get type use
        //(it.firstChild as? KTExpression)?.resolveType()
        asText
    }
}


fun KtElement.getContainingFunctionOrPropertyAccessor(): KtModifierListOwner? =
        getParentOfType<KtNamedFunction>(true) ?: getParentOfType<KtPropertyAccessor>(true)

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
 * Examines the current scope (until a function or property is reached) for a try catch
 * @receiver PsiElement
 * @return Boolean
 */
fun PsiElement.isWrappedInTryCatch(): Boolean = findParentTryCatch() != null

fun PsiElement.isNotWrappedInTryCatch(): Boolean {
    return !isWrappedInTryCatch()
}

fun PsiElement.findParentTryCatch(): KtTryExpression? {
    var current: PsiElement = this
    while (true) {
        if (current is KtProperty || current is KtNamedFunction) {
            return null
        }
        if (current is KtTryExpression) {
            return current
        }
        current = current.parent ?: return null
    }
}

fun KtElement.isContainingFunctionMarkedAsThrows(): Boolean {
    var current: PsiElement = this
    while (true) {
        when (current) {
            is KtPropertyAccessor -> return current.throwsDeclared()
            is KtNamedFunction -> return current.throwsDeclared()
            else -> current = current.parent ?: return false
        }

    }
}

fun KtLambdaExpression.resolveParameterIndex(): Int? {
    val callExp =
            parent?.parent as? KtCallExpression
                    ?: parent?.parent?.parent as? KtCallExpression
                    ?: return null
    return callExp.getParameterInfos().indexOfFirst {
        (it.typeInfo as? TypeInfo.ByExpression)?.expression === this
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
fun KtExpression.findFunctionScope(): KtNamedFunction? = getParentOfType(true)

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

fun KtTryExpression.notCatchesAll(throws: List<String>): Boolean {
    return !catchesAll(throws)
}

fun KtTryExpression.catchesAll(throws: List<String>): Boolean {
    return throws.all {
        catchClauses.catches(it)
    }
}

fun List<KtCatchClause>.catches(fullyQualifiedType: String): Boolean = this.any {
//    val fqType = it.catchParameter?.resolveFullyQualifiedType() ?: return false
    return true
//    val type = it.catchParameter?.resolveType() ?: return false
//    return type.toString() == fullyQualifiedType
}


//fun KtParameter.resolveFullyQualifiedType(): KotlinType {
//    return (this as KtExpression).resolveType()
//}
