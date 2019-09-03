package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.*
import com.intellij.psi.impl.source.*
import com.intellij.psi.util.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.idea.caches.resolve.*
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.idea.references.*
import org.jetbrains.kotlin.j2k.*
import org.jetbrains.kotlin.js.resolve.diagnostics.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.calls.callUtil.*
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.lazy.*
import org.jetbrains.uast.*
import org.jetbrains.uast.getContainingClass
import org.jetbrains.uast.kotlin.*

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


fun CallableDescriptor.findUClass(): UClass? {
    val psi = findPsi() ?: return null
    return if (psi is PsiClass) {
        psi.toUElement(UClass::class.java)
    } else {
        PsiTreeUtil.getParentOfType(psi, PsiClass::class.java)?.toUElement(UClass::class.java)
    }
}

fun KtElement.resolveToCall(bodyResolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): ResolvedCall<out CallableDescriptor>? =
        getResolvedCall(analyze(bodyResolveMode))

@JvmOverloads
fun KtElement.analyze(bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL): BindingContext =
        getResolutionFacade().analyze(this, bodyResolveMode)

fun KtTryExpression.catchesAll(throws: List<UClass>): Boolean {
    return throws.all { clz: UClass ->
        catchClauses.catches(clz)
    }
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
    return it.catchParameter?.isSubtypeOf(inputClass) == true
}

fun UClass.isSubTypeOf(other: UClass) = isChildOfSafe(other)

fun KtParameter.isSubtypeOf(throwType: UClass): Boolean {
    val caughtClass = resolveTypeClass()
            ?: return false

//    if (caughtClass.getKotlinFqName() == FqName("java.lang.Exception")) {
//        return true
//    }
    return throwType.isChildOfSafe(caughtClass)
}

fun KtParameter.resolveTypeClass(): UClass? {
    return (this.resolveToDescriptorIfAny() as? ValueDescriptor)
            ?.type
            ?.constructor
            ?.declarationDescriptor
            ?.findPsi()
            ?.toUElement(UClass::class.java)
}

fun ValueArgument.resolveClassLiterals(): List<KtClassLiteralExpression> {
    return when (val argumentExpression = getArgumentExpression()) {
        is KtClassLiteralExpression -> listOf(argumentExpression)
        is KtCollectionLiteralExpression -> argumentExpression.getInnerExpressions().filterIsInstance(KtClassLiteralExpression::class.java)
        is KtCallExpression -> argumentExpression.valueArguments.mapNotNull { it.getArgumentExpression() as? KtClassLiteralExpression }
        else -> emptyList()
    }
}

fun KtClassLiteralExpression.findUClass(): UClass? {
    val uLiteral = toUElement() as? KotlinUClassLiteralExpression
    return (uLiteral?.type as? PsiClassReferenceType)?.reference?.resolve()?.toUElement() as? UClass
}


fun UClass.isChildOfSafe(other: UClass): Boolean {
    var currentClass: UClass? = this
    while (currentClass != null) {
        if (currentClass == other) {
            return true
        }
        currentClass = currentClass.superClass
    }
    return false
}