//package csense.idea.kotlin.checked.exceptions.bll
//
//import com.intellij.openapi.project.*
//import com.intellij.psi.*
//import com.intellij.psi.search.*
//import csense.idea.base.bll.kotlin.*
//import csense.idea.base.bll.psi.*
//import csense.idea.base.bll.uast.*
//import csense.idea.kotlin.checked.exceptions.callthough.*
//import csense.idea.kotlin.checked.exceptions.ignore.*
//import csense.idea.kotlin.checked.exceptions.settings.*
//import csense.kotlin.extensions.collections.array.*
//import csense.kotlin.extensions.collections.nullOnEmpty
//import org.jetbrains.kotlin.descriptors.*
//import org.jetbrains.kotlin.idea.caches.resolve.*
//import org.jetbrains.kotlin.psi.*
//import org.jetbrains.kotlin.psi.psiUtil.*
//import org.jetbrains.kotlin.resolve.*
//import org.jetbrains.kotlin.resolve.calls.callUtil.*
//import org.jetbrains.kotlin.resolve.calls.model.*
//import org.jetbrains.kotlin.resolve.lazy.*
//import org.jetbrains.uast.*
//import kotlin.collections.getOrNull
//
//fun PsiElement.throwsTypesIfFunction(callExpression: KtCallExpression): List<UClass>? {
//    val result = when (this) {
//        is KtFunction -> throwsTypes()
//        is PsiMethod -> computeThrowsTypes(callExpression)
//        else -> null
//    }
//    //if we are to ignore runtime exceptions, filter those out.
//    return result?.filterRuntimeExceptionsBySettings()?.nullOnEmpty()
//}
//
//fun List<UClass>.filterRuntimeExceptionsBySettings(): List<UClass> {
//    return if (Settings.runtimeAsCheckedException) {
//        this
//    } else {
//        filterNot {
//            it.isRuntimeExceptionClass()
//        }
//    }
//}
//
//fun PsiMethod.computeThrowsTypes(callExpression: KtCallExpression): List<UClass> {
//    return throwsList.referenceElements.mapNotNull { it ->
//        val clz = it.resolve()
//        val uClass = clz?.toUElement(UClass::class.java)
//        if (clz is PsiTypeParameter) {
//            val resolvedType: UClass? = this.typeParameters.indexOfFirstOrNull { it.name == clz.name }?.let {
//                callExpression.typeArguments.getOrNull(it)?.typeReference?.resolve()?.toUElementOfType()
//            }
//            if (resolvedType != null) {
//                return@mapNotNull resolvedType
//            }
//        }
//        uClass
//    }
//}
//
//
//fun KtFunction.findThrowsAnnotation(): KtAnnotationEntry? = annotationEntries.firstOrNull {
//    it.shortName?.asString() == kotlinThrowsText
//}
//
//
//fun Project.resolveMainKotlinException(): UClass? {
//    return JavaPsiFacade.getInstance(this)
//        .findClass("java.lang.Throwable", GlobalSearchScope.allScope(this))
//        ?.toUElementOfType<UClass>()
//}
//
//fun KtFunction.throwsTypes(): List<UClass> {
//    val throwsAnnotation = findThrowsAnnotation() ?: return listOf()
//    return if (throwsAnnotation.children.size <= 1) { //eg "@Throws"
//        listOfNotNull(project.resolveMainKotlinException())
//    } else {
//        throwsAnnotation.valueArguments.map { value ->
//            value.resolveClassLiterals().mapNotNull {
//                it.findUClass()
//            }
//        }.flatten()
//    }
//}
//
//
//fun KtElement.getContainingFunctionOrPropertyAccessor(): KtModifierListOwner? {
//    val parent = getParentOfType<KtNamedFunction>(true)
//    return parent ?: getParentOfType<KtPropertyAccessor>(true)
//}
//
//fun KtAnnotated.throwsTypes(): List<UClass> {
//    val annotation = annotationEntries.findThrows() ?: return listOf()
//
//    return annotation.valueArguments.map { value ->
//        value.resolveClassLiterals().mapNotNull { klass ->
//            klass.findUClass()
//        }
//    }.flatten()
//}
//
//fun List<KtAnnotationEntry>.findThrows(): KtAnnotationEntry? {
//    return find {
//        it.shortName?.asString() == kotlinThrowsText
//    }
//}
//
//fun PsiElement.findParentTryCatch(): KtTryExpression? {
//    var current: PsiElement? = this
//    while (true) {
//        //skip the "try" from a catch clause..
//        if (current is KtCatchClause) {
//            current = current.parent?.parent
//        }
//        if (current is KtProperty && current.isMember || current is KtFunction && current.fqName != null) {
//            return null
//        }
//        if (current is KtLambdaExpression) {
//            val lambda = current.getPotentialContainingLambda() ?: return null
//            //if we do not know it, assume its a "callback" based one.
//            val isKnown = CallthoughInMemory.isArgumentMarkedAsCallthough(lambda.main, lambda.parameterName)
//            if (!isKnown) {
//                return null
//            }
//            current = current.parent?.parent
//        }
//        if (current is KtTryExpression) {
//            return current
//        }
//        current = current?.parent ?: return null
//    }
//}
//
//fun KtElement.containingFunctionMarkedAsThrowTypes(): List<UClass> {
//    var current: PsiElement = this
//    while (true) {
//        when (current) {
//            is KtLambdaExpression -> {
//                val lambda = current.getPotentialContainingLambda() ?: return listOf()
//                //if we do not know it, assume its a "callback" based one.
//                val isKnown = CallthoughInMemory.isArgumentMarkedAsCallthough(lambda.main, lambda.parameterName)
//                if (!isKnown) {
//                    return emptyList()
//                }
//                //go on
//                current = current.parent
//            }
//            is KtPropertyAccessor -> return current.throwsTypes()
//            is KtNamedFunction -> return current.throwsTypes()
//            else -> current = current.parent ?: return emptyList()
//        }
//
//    }
//}
//
//fun KtFunction.findInvocationOfName(name: String): KtCallExpression? {
//    return findDescendantOfType {
//        it.text.startsWith(name)
//    }
//}
//
//
///**
// * Tries to resolve the function we are in.
// * @receiver KtExpression
// */
//fun KtExpression.findFunctionScope(): KtFunction? = getParentOfType(true)
////
/////**
//// * Tries to resolve the type of a throws expression in kotlin.
//// * @receiver KtThrowExpression
//// * @return String?
//// */
////fun KtThrowExpression.tryAndResolveThrowType(): String? = tryAndLog {
////    val thrownExpression = this.thrownExpression as? KtCallExpression ?: return@tryAndLog null
////    val nameExpression = thrownExpression.calleeExpression as? KtNameReferenceExpression ?: return@tryAndLog null
////    val descriptor = nameExpression.resolveToCall()?.resultingDescriptor ?: return@tryAndLog null
////    val declarationDescriptor = descriptor.containingDeclaration
////    return@tryAndLog DescriptorUtils.getFqName(declarationDescriptor).asString()
////}
////
////fun KtThrowExpression.tryAndResolveThrowTypeOrDefaultUClass(): UClass? {
////    return thrownExpression?.tryAndResolveThrowTypeOrDefaultUClass()
////}
////
////private fun KtElement.tryAndResolveThrowTypeOrDefaultUClass(): UClass? {
////    return when (this) {
////        is KtCallExpression -> this.resolveMainReferenceWithTypeAliasForClass()
////        is KtDotQualifiedExpression -> selectorExpression?.tryAndResolveThrowTypeOrDefaultUClass()
////        is KtNameReferenceExpression -> {
////            val value = this.resolve() as? KtProperty ?: return null
////            value.resolveTypeClassException(project)
////        }
////        else -> null
////    }
////}
//
////
////fun KtCallExpression.resolveMainReferenceWithTypeAliasForClass(): UClass? {
////    val resolved = resolveMainReferenceWithTypeAlias()
////    val clz = when (resolved) {
////        is PsiClass -> resolved
////        is PsiMember -> resolved.containingClass
////        else -> resolved
////    }
////    return clz?.toUElement(UClass::class.java)
////}
//
//fun KtElement.resolveToCall(bodyResolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): ResolvedCall<out CallableDescriptor>? =
//    getResolvedCall(analyze(bodyResolveMode))
//
//@JvmOverloads
//fun KtElement.analyze(bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL): BindingContext =
//    getResolutionFacade().analyze(this, bodyResolveMode)
//
//fun KtTryExpression.catchesAll(throws: List<UClass>): Boolean = throws.all { clz: UClass ->
//    catchClauses.catches(clz)
//}
//
///**
// * Does any of the receiver classes catches the given class  ( is any of them subtypes of the thrown type)
// * @receiver List<UClass>
// * @param inputClass UClass
// * @return Boolean
// */
//fun List<UClass>.catchesClass(inputClass: UClass): Boolean = any {
//    it.isKotlinThrowable() || inputClass.isSubTypeOf(it)
//}
//
//
//private val throwableTypes = setOf(
//    "kotlin.Throwable",
//    "java.lang.Throwable"
//)
//
//fun UClass.isKotlinThrowable(): Boolean {
//    val name = this.getKotlinFqNameString()
//    return name in throwableTypes
//}
//
//
//fun List<UClass>.filterOnlyNonCaught(catches: List<UClass>) = filterNot {
//    catches.catchesClass(it)
//}
//
//fun List<UClass>.isAllThrowsHandledByTypes(catches: List<UClass>) = all {
//    catches.catchesClass(it)
//}
//
//
//fun List<KtCatchClause>.catches(inputClass: UClass): Boolean = this.any {
//    val resolved = it.catchParameter?.resolveTypeClass() ?: return@any false
//    return@any resolved.isKotlinThrowable() || resolved.isChildOfSafe(inputClass)
//}
