package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import csense.idea.base.bll.findUClass
import csense.idea.base.bll.kotlin.findUClass
import csense.idea.base.bll.kotlin.isSubtypeOf
import csense.idea.base.bll.kotlin.resolveClassLiterals
import csense.idea.base.bll.uast.isSubTypeOf
import csense.kotlin.extensions.collections.isNotNullOrEmpty
import csense.kotlin.extensions.map
import csense.kotlin.extensions.tryAndLog
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.getResolutionFacade
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.uast.UClass
import org.jetbrains.uast.toUElement

fun PsiElement.throwsTypesIfFunction(): List<UClass>? {
    val result = when (this) {
        is KtFunction -> throwsTypes()
        is PsiMethod -> computeThrowsTypes()
        else -> null
    }
    return result.isNotNullOrEmpty().map(result, null)
}


fun PsiMethod.computeThrowsTypes(): List<UClass> {
    return throwsTypes.mapNotNull {
        it.resolve()?.sourceElement?.toUElement(UClass::class.java)
    }
}

fun KtFunction.findThrowsAnnotation(): KtAnnotationEntry? = annotationEntries.firstOrNull {
    it.shortName?.asString() == kotlinThrowsText
}

fun KtFunction.throwsTypes(): List<UClass> {
    val throwsAnnotation = findThrowsAnnotation() ?: return listOf()
    return if (throwsAnnotation.children.size <= 1) { //eg "@Throws"
//        listOf(kotlinMainExceptionFq)
        listOf()//TODO make me
    } else {
        throwsAnnotation.valueArguments.map { value ->
            value.resolveClassLiterals().mapNotNull {
                it.findUClass()
            }
        }.flatten()
    }
}


fun KtElement.getContainingFunctionOrPropertyAccessor(): KtModifierListOwner? =
        getParentOfType<KtFunction>(true) ?: getParentOfType<KtPropertyAccessor>(true)

//fun KtAnnotated.throwsDeclared(): Boolean = annotationEntries.any {
//    it.shortName?.asString() == kotlinThrowsText
//}


fun KtAnnotated.throwsTypes(): List<UClass> {
    val annotation = annotationEntries.findThrows() ?: return listOf()

    return annotation.valueArguments.map { value ->
        value.resolveClassLiterals().mapNotNull { klass ->
            klass.findUClass()
        }
    }.flatten()
}

fun List<KtAnnotationEntry>.findThrows(): KtAnnotationEntry? {
    return find {
        it.shortName?.asString() == kotlinThrowsText
    }
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
        if (current is KtProperty && current.isMember || current is KtFunction) {
            return null
        }
        if (current is KtTryExpression) {
            return current
        }
        current = current.parent ?: return null
    }
}

fun KtElement.containingFunctionMarkedAsThrowTypes(): List<UClass> {
    var current: PsiElement = this
    while (true) {
        when (current) {
            is KtPropertyAccessor -> return current.throwsTypes()
            is KtFunction -> return current.throwsTypes()
            else -> current = current.parent ?: return listOf()
        }

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
fun KtExpression.findFunctionScope(): KtFunction? = getParentOfType(true)

/**
 * Tries to resolve the type of a throws expression in kotlin.
 * @receiver KtThrowExpression
 * @return String?
 */
fun KtThrowExpression.tryAndResolveThrowType(): String? = tryAndLog {
    val thrownExpression = this.thrownExpression as? KtCallExpression ?: return null
    val nameExpression = thrownExpression.calleeExpression as? KtNameReferenceExpression ?: return null
    val descriptor = nameExpression.resolveToCall()?.resultingDescriptor ?: return null
    val declarationDescriptor = descriptor.containingDeclaration
    return DescriptorUtils.getFqName(declarationDescriptor).asString()
}

fun KtThrowExpression.tryAndResolveThrowTypeOrDefault(): String = tryAndResolveThrowType() ?: kotlinMainExceptionFqName

fun KtThrowExpression.tryAndResolveThrowTypeOrDefaultUClass(): UClass? {
    val thrownExpression = this.thrownExpression as? KtCallExpression ?: return null
    val nameExpression = thrownExpression.calleeExpression as? KtNameReferenceExpression ?: return null
    val descriptor = nameExpression.resolveToCall()?.resultingDescriptor ?: return null
    return descriptor.findUClass()
}

fun KtElement.resolveToCall(bodyResolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): ResolvedCall<out CallableDescriptor>? =
        getResolvedCall(analyze(bodyResolveMode))

@JvmOverloads
fun KtElement.analyze(bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL): BindingContext =
        getResolutionFacade().analyze(this, bodyResolveMode)

fun KtTryExpression.catchesAll(throws: List<UClass>): Boolean = throws.all { clz: UClass ->
    catchClauses.catches(clz)
}

/**
 * Does any of the receiver classes catches the given class  ( is any of them subtypes of the thrown type)
 * @receiver List<UClass>
 * @param inputClass UClass
 * @return Boolean
 */
fun List<UClass>.catchesClass(inputClass: UClass): Boolean = any {
    inputClass.isSubTypeOf(it)
}


fun List<UClass>.isAllThrowsHandledByTypes(catches: List<UClass>) = all {
    catches.catchesClass(it)
}

fun List<KtCatchClause>.catches(inputClass: UClass): Boolean = this.any {
    it.catchParameter?.isSubtypeOf(inputClass) == true
}