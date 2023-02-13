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
//fun KtElement.getContainingFunctionOrPropertyAccessor(): KtModifierListOwner? {
//    val parent = getParentOfType<KtNamedFunction>(true)
//    return parent ?: getParentOfType<KtPropertyAccessor>(true)
//}
//
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



//fun KtElement.resolveToCall(bodyResolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): ResolvedCall<out CallableDescriptor>? =
//    getResolvedCall(analyze(bodyResolveMode))
//
//@JvmOverloads
//fun KtElement.analyze(bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL): BindingContext =
//    getResolutionFacade().analyze(this, bodyResolveMode)
//
