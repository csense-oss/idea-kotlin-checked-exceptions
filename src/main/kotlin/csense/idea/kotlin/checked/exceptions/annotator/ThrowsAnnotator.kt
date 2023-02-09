package csense.idea.kotlin.checked.exceptions.annotator

import com.intellij.lang.annotation.*
import csense.idea.base.bll.annotator.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.module.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.settings.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.psi.*


class ThrowsAnnotator : TypedAnnotator<KtThrowExpression>() {

    override val tType: Class<KtThrowExpression> = type()

//    private val cachedJavaLangThrowable: MutableMap<Project, UClass?> = mutableMapOf()

//    private fun getMaxDepth(): Int {
//        return Settings.maxDepth
//    }

    override fun annotateTyped(typedElement: KtThrowExpression, holder: AnnotationHolder) {
        val resolved = typedElement.resolveThrownTypeOrNullIfShouldBeSkipped() ?: return

//        val container = typedElement.findContainingFunctionOrLambda()
//        val errors = container.evaluateProblemsWithExceptionTypes(resolvedThrowsType)
//        errors.applyTo(holder)
    }
//
//    fun shouldSkip(resolvedType: KtPsiClass?): Boolean {
//        resolvedType ?: return true
//
//        val ignoreRuntimeExceptions = !Settings.ignoreRuntimeExceptions
//        return ignoreRuntimeExceptions && resolvedType.isSubtypeOfRuntimeException()
//    }


//    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
//        val throwsExp = element as? KtThrowExpression ?: return
//        if (element.isInTestModule2()) {
//            return
//        }
//        val realType = throwsExp.thrownExpression?.resolveFirstClassType()
//        val project = element.project
//        cachedJavaLangThrowable[project] = project.getJavaLangThrowableUClass()
//        val throwType = realType?.toUExceptionClass(cachedJavaLangThrowable[project])
//        //skip runtime exception types iff they are disabled.
//        if (!Settings.runtimeAsCheckedException && throwType?.isRuntimeExceptionClass() == false) {
//            return
//        }
//        val throws = listOfNotNull(throwType)
//        val range = TextRange(
//            element.getTextRange().startOffset,
//            element.getTextRange().endOffset
//        )
//        throwsExp.findFunctionScope()?.let {
//
//            val lambdaContext = it.getPotentialContainingLambda()
//            val tryCatchExpression = it.findParentTryCatch()
//            val isAllCaught = tryCatchExpression != null && tryCatchExpression.catchesAll(throws)
//            val markedThrows = it.containingFunctionMarkedAsThrowTypes()
//            if (markedThrows.isNotEmpty()) {
//                //test type, and report if not correct.
//                if (!throws.isAllThrowsHandledByTypes(markedThrows)) {
//                    registerAnnotationProblem(holder, throws, throwsExp, range)
//                }
//
//            } else if (!isAllCaught
//                && (lambdaContext == null ||
//                        !it.isContainedInLambdaCatchingOrIgnoredRecursive(
//                            IgnoreInMemory,
//                            getMaxDepth(),
//                            throws
//                        ))
//            ) {
//                //it throws, we want to cache that.
//                registerProblems(holder, throws, throwsExp, range)
//            }
//        }
//    }


//    private fun registerProblems(
//        holder: AnnotationHolder,
//        throwType: List<UClass>,
//        throwsExp: KtThrowExpression,
//        range: TextRange
//    ) {
//        val throwText = throwType.mapNotNull { it.name }.joinToString(", ")
//        holder.newAnnotation(
//            HighlightSeverity.WARNING,
//            "Throws \"$throwText\""
//        ).range(range).withFix(
//            DeclareFunctionAsThrowsIntentionAction(
//                throwsExp, throwType.firstOrNull()?.name
//                    ?: ""
//            )
//        ).create()
//    }
//
//    private fun registerAnnotationProblem(
//        holder: AnnotationHolder,
//        throwType: List<UClass>,
//        throwsExp: KtThrowExpression,
//        range: TextRange
//    ) {
//        val throwText = throwType.mapNotNull { it.name }.joinToString(", ")
//        holder.newAnnotation(
//            HighlightSeverity.WARNING,
//            "Throws \"$throwText\", but does not have that in the throws annotation"
//        ).range(range).withFix(
//            AddThrowsTypeIntentionAction(
//                throwsExp, throwType.firstOrNull()?.name
//                    ?: ""
//            )
//        ).create()
//    }
}

fun KtExpression.findContainingFunctionOrLambda(): KtExpression {
    TODO()
}

//TODO move.
fun KtThrowExpression.resolveThrownTypeOrNullIfShouldBeSkipped(): KtPsiClass? {
    if (this.isInTestModule()) {
        return null
    }
    val resolvedThrowsType: KtPsiClass = this.resolveThrownTypeOrNull() ?: return null
    if (resolvedThrowsType.shouldSkip()) {
        return null
    }
    return resolvedThrowsType
}


fun KtThrowExpression.resolveThrownTypeOrNull(): KtPsiClass? {
    val resolution = ProjectClassResolutionInterface.getOrCreate(project)
    val thrown = this.thrownExpression ?: run {
        val throwable = resolution.getThrowable() ?: return@resolveThrownTypeOrNull null
        return@resolveThrownTypeOrNull KtPsiClass.Psi(throwable)
    }
    return thrown.resolveFirstClassType2()
}


fun KtPsiClass.shouldSkip(): Boolean {
    return Settings.ignoreRuntimeExceptions && isSubtypeOfRuntimeException()
}