package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.psi.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.idea.caches.resolve.*
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.*
import org.jetbrains.kotlin.idea.refactoring.fqName.*
import org.jetbrains.kotlin.idea.references.*
import org.jetbrains.kotlin.js.resolve.diagnostics.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.calls.callUtil.*
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.lazy.*
import org.jetbrains.uast.*

fun PsiElement.throwsTypesIfFunction(): List<UClass>? {
    val result = when (this) {
        is KtNamedFunction -> throwsTypes()
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

fun KtNamedFunction.findThrowsAnnotation(): KtAnnotationEntry? = annotationEntries.firstOrNull {
    it.shortName?.asString() == kotlinThrowsText
}

fun KtNamedFunction.throwsTypes(): List<UClass> {
    val throwsAnnotation = findThrowsAnnotation() ?: return listOf()
    return if (throwsAnnotation.children.size <= 1) { //eg "@Throws"
//        listOf(kotlinMainExceptionFq)
        listOf()//TODO make me
    } else {
        throwsAnnotation.valueArguments.map { value ->
            value.resolveClassLiterals().mapNotNull {
                it.findPsi().toUElement(UClass::class.java)
            }
        }.flatten()
    }
}


fun KtElement.getContainingFunctionOrPropertyAccessor(): KtModifierListOwner? =
        getParentOfType<KtNamedFunction>(true) ?: getParentOfType<KtPropertyAccessor>(true)

fun KtAnnotated.throwsDeclared(): Boolean = annotationEntries.any {
    it.shortName?.asString() == kotlinThrowsText
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
        if (current is KtProperty && current.isMember || current is KtNamedFunction) {
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
fun KtThrowExpression.tryAndResolveThrowType(): String? = tryAndLog {
    val thrownExpression = this.thrownExpression as KtCallExpression
    val nameExpression = thrownExpression.calleeExpression as? KtNameReferenceExpression ?: return null
    val descriptor = nameExpression.resolveToCall()?.resultingDescriptor ?: return null
    val declarationDescriptor = descriptor.containingDeclaration
    return DescriptorUtils.getFqName(declarationDescriptor).asString()
}

fun KtThrowExpression.tryAndResolveThrowTypeOrDefault(): String = tryAndResolveThrowType() ?: kotlinMainExceptionFqName

fun KtElement.resolveToCall(bodyResolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): ResolvedCall<out CallableDescriptor>? =
        getResolvedCall(analyze(bodyResolveMode))

@JvmOverloads
fun KtElement.analyze(bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL): BindingContext =
        getResolutionFacade().analyze(this, bodyResolveMode)

fun KtTryExpression.notCatchesAll(throws: List<UClass>): Boolean {
    return !catchesAll(throws)
}

fun KtTryExpression.catchesAll(throws: List<UClass>): Boolean {
    return throws.all { clz: UClass ->
        catchClauses.catches(clz)
    }
}

fun List<KtCatchClause>.catches(inputClass: UClass): Boolean = this.any {
    return it.catchParameter?.isSubtypeOf(inputClass) == true
}

private fun KtParameter.isSubtypeOf(superType: UClass): Boolean {
    val caughtClass = resolveTypeClass() ?: return false
    return caughtClass.isChildOf(superType, true)
}

fun KtParameter.resolveTypeClass(): UClass? {
    return (this.resolveToDescriptorIfAny() as? ValueDescriptor)
            ?.type
            ?.constructor
            ?.declarationDescriptor
            ?.findPsi()
            ?.toUElement(UClass::class.java)
}

fun KtParameter.resolveFullyQualifiedName(): FqName? {
    return (this.resolveToDescriptorIfAny() as? ValueDescriptor)?.type?.constructor?.declarationDescriptor?.fqNameSafe
}


fun ValueArgument.resolveClassLiterals(): List<KtClassLiteralExpression> {
    return when (val argumentExpression = getArgumentExpression()) {
        is KtClassLiteralExpression -> listOf(argumentExpression)
        is KtCollectionLiteralExpression -> argumentExpression.getInnerExpressions().filterIsInstance(KtClassLiteralExpression::class.java)
        is KtCallExpression -> argumentExpression.valueArguments.mapNotNull { it.getArgumentExpression() as? KtClassLiteralExpression }
        else -> emptyList()
    }
}


fun KtClassLiteralExpression.typeFqName(): FqName? {
    return receiverExpression?.getKotlinFqName()
}


fun FqName.isIn(cls: PsiClass): Boolean {
    val name = asString()
    var currentType: PsiClass = cls

    while (true) {
        if (currentType.getKotlinFqName()?.asString() == name) {
            return true
        }
        currentType = currentType.superClass ?: return false
    }
}

fun KtClassLiteralExpression.findPsi(): PsiElement? {
    return receiverExpression?.resolveMainReferenceToDescriptors()?.firstOrNull()?.findPsi()
}