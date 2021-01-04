package csense.idea.kotlin.checked.exceptions.annotator

import com.intellij.lang.annotation.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import csense.idea.base.bll.uast.*
import csense.idea.base.module.isInTestSourceRoot
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.intentionAction.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.*


class ThrowsAnnotator : Annotator {

    private fun getMaxDepth(): Int {
        return Settings.maxDepth
    }
    
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        val throwsExp = element as? KtThrowExpression ?: return
        if (element.isInTestSourceRoot()) {
            return
        }
        val throwType = throwsExp.tryAndResolveThrowTypeOrDefaultUClass() ?: return
        //skip runtime exception types iff they are disabled.
        if(!Settings.runtimeAsCheckedException && throwType.isRuntimeExceptionClass()){
            return
        }
        val throws = listOf(throwType)
        val range: TextRange = TextRange(element.getTextRange().startOffset,
                element.getTextRange().endOffset)
        throwsExp.findFunctionScope()?.let {
            
            val lambdaContext = it.getPotentialContainingLambda()
            val tryCatchExpression = it.findParentTryCatch()
            val isAllCaught = tryCatchExpression != null && tryCatchExpression.catchesAll(listOf(throwType))
            val markedThrows = it.containingFunctionMarkedAsThrowTypes()
            if (markedThrows.isNotEmpty()) {
                //test type, and report if not correct.
                if (!throws.isAllThrowsHandledByTypes(markedThrows)) {
                    registerAnnotationProblem(holder, listOf(throwType), throwsExp, range)
                }
                
            } else if (!isAllCaught
                    && (lambdaContext == null ||
                            !it.isContainedInLambdaCatchingOrIgnoredRecursive(
                                    IgnoreInMemory,
                                    getMaxDepth(),
                                    listOf(throwType)))
            ) {
                //it throws, we want to cache that.
                registerProblems(holder, listOf(throwType), throwsExp, range)
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
        holder.createAnnotation(
                HighlightSeverity.WARNING,
                range,
                "Throws \"$throwText\"").registerFix(
                DeclareFunctionAsThrowsIntentionAction(throwsExp, throwType.firstOrNull()?.name
                        ?: ""))
    }
    
    private fun registerAnnotationProblem(
            holder: AnnotationHolder,
            throwType: List<UClass>,
            throwsExp: KtThrowExpression,
            range: TextRange
    ) {
        val throwText = throwType.mapNotNull { it.name }.joinToString(", ")
        holder.createAnnotation(
                HighlightSeverity.WARNING,
                range,
                "Throws \"$throwText\", but does not have that in the throws annotation").registerFix(
                AddThrowsTypeIntentionAction(throwsExp, throwType.firstOrNull()?.name
                        ?: ""))
    }
}