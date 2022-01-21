package csense.idea.kotlin.checked.exceptions.annotator

import com.intellij.lang.annotation.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import csense.idea.base.bll.uast.*
import csense.idea.base.module.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.inspections.*
import csense.idea.kotlin.checked.exceptions.intentionAction.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*


class ThrowsAnnotator : Annotator {
    private val cachedJavaLangThrowable: MutableMap<Project, UClass?> = mutableMapOf()

    private fun getMaxDepth(): Int {
        return Settings.maxDepth
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val throwsExp = element as? KtThrowExpression ?: return
        if (element.isInTestSourceRoot()) {
            return
        }
        val realType = throwsExp.thrownExpression?.resolveFirstClassType()
        val project = element.project
        cachedJavaLangThrowable[project] = project.getJavaLangThrowableUClass()
        val throwType = realType?.toUExceptionClass(cachedJavaLangThrowable[project])
        //skip runtime exception types iff they are disabled.
        if (!Settings.runtimeAsCheckedException && throwType?.isRuntimeExceptionClass() == false) {
            return
        }
        val throws = listOfNotNull(throwType)
        val range = TextRange(
            element.getTextRange().startOffset,
            element.getTextRange().endOffset
        )
        throwsExp.findFunctionScope()?.let {

            val lambdaContext = it.getPotentialContainingLambda()
            val tryCatchExpression = it.findParentTryCatch()
            val isAllCaught = tryCatchExpression != null && tryCatchExpression.catchesAll(throws)
            val markedThrows = it.containingFunctionMarkedAsThrowTypes()
            if (markedThrows.isNotEmpty()) {
                //test type, and report if not correct.
                if (!throws.isAllThrowsHandledByTypes(markedThrows)) {
                    registerAnnotationProblem(holder, throws, throwsExp, range)
                }

            } else if (!isAllCaught
                && (lambdaContext == null ||
                        !it.isContainedInLambdaCatchingOrIgnoredRecursive(
                            IgnoreInMemory,
                            getMaxDepth(),
                            throws
                        ))
            ) {
                //it throws, we want to cache that.
                registerProblems(holder, throws, throwsExp, range)
            }
        }
    }

    private fun registerProblems(
        holder: AnnotationHolder,
        throwType: List<UClass>,
        throwsExp: KtThrowExpression,
        range: TextRange
    ) {
        val throwText = throwType.mapNotNull { it.name }.joinToString(", ")
        holder.newAnnotation(
            HighlightSeverity.WARNING,
            "Throws \"$throwText\""
        ).range(range).withFix(
            DeclareFunctionAsThrowsIntentionAction(
                throwsExp, throwType.firstOrNull()?.name
                    ?: ""
            )
        ).create()
    }

    private fun registerAnnotationProblem(
        holder: AnnotationHolder,
        throwType: List<UClass>,
        throwsExp: KtThrowExpression,
        range: TextRange
    ) {
        val throwText = throwType.mapNotNull { it.name }.joinToString(", ")
        holder.newAnnotation(
            HighlightSeverity.WARNING,
            "Throws \"$throwText\", but does not have that in the throws annotation"
        ).range(range).withFix(
            AddThrowsTypeIntentionAction(
                throwsExp, throwType.firstOrNull()?.name
                    ?: ""
            )
        ).create()
    }
}