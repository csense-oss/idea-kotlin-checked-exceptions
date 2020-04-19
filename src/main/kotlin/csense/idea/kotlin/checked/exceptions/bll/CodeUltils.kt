package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.*
import csense.idea.base.bll.findUClass
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.uast.isSubTypeOf
import csense.idea.kotlin.checked.exceptions.callthough.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.isNotNullOrEmpty
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
import org.jetbrains.kotlin.resolve.source.*
import org.jetbrains.uast.*

fun PsiElement.throwsTypesIfFunction(callExpression: KtCallExpression): List<UClass>? {
    val result = when (this) {
        is KtFunction -> throwsTypes()
        is PsiMethod -> computeThrowsTypes(callExpression)
        else -> null
    }
    return result.isNotNullOrEmpty().map(result, null)
}


fun PsiMethod.computeThrowsTypes(callExpression: KtCallExpression): List<UClass> {
    //generic exception types is another thing, as we

//    val genericLookup = this.typeParameters.map { Pair(it.name, it) }
    return throwsTypes.mapIndexedNotNull { index, it ->
        val clz = it.resolve()?.sourceElement
        val uClass = clz?.toUElement(UClass::class.java)
        if (clz is PsiTypeParameter) {
            val resolvedType: UClass? = this.typeParameters.indexOfFirstOrNull { it.name == clz.name }?.let {
                callExpression.typeArguments.getOrNull(it)?.typeReference?.resolve()?.toUElementOfType()
            }
            if (resolvedType != null) {
                return@mapIndexedNotNull resolvedType
            }
        }
        
        uClass
    }
}

//waiting for csense (and to add to collection as well)
inline fun <T> Array<out T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    return when (val value = indexOfFirst(predicate)) {
        -1 -> null
        else -> value
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
    var current: PsiElement? = this
    while (true) {
        //skip the "try" from a catch clause..
        if (current is KtCatchClause) {
            current = current.parent?.parent
        }
        if (current is KtProperty && current.isMember || current is KtFunction && current.fqName != null) {
            return null
        }
        if (current is KtLambdaExpression) {
            val lambda = current.getPotentialContainingLambda() ?: return null
            val fncFqName = lambda.main.fqName?.asString() ?: return null
            //if we do not know it, assume its a "callback" based one.
            val isKnown = InlineLambdaCallInbuilt.inbuiltKotlinSdk.contains(fncFqName) ||
                    CallthoughInMemory.isArgumentMarkedAsCallthough(lambda.main, lambda.parameterName)
            if (!isKnown) {
                return null
            }
            current = current.parent?.parent
        }
        if (current is KtTryExpression) {
            return current
        }
        current = current?.parent ?: return null
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