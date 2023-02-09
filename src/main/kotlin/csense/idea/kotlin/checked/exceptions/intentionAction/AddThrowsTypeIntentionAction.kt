package csense.idea.kotlin.checked.exceptions.intentionAction

import com.intellij.codeInsight.intention.impl.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.idea.util.application.*
import org.jetbrains.kotlin.psi.*

//class AddThrowsTypeIntentionAction(
//        private val throwsExp: KtThrowExpression,
//        val throwType: String
//) : BaseIntentionAction() {
//    override fun getFamilyName(): String =
//            "Csense checked exceptions - intention action"
//
//    override fun getText(): String =
//            "add throw type \"$throwType\" to throws annotation"
//
//    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true
//
//    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) = tryAndLog {
//        throwsExp.getContainingFunctionOrPropertyAccessor()?.let {
//            val throws = it.annotationEntries.findThrows() ?: return@tryAndLog
//            val throwsNotNull = throws.valueArguments.mapNotNull { it.getArgumentExpression()?.text } + throwType
//            val fullText = throwsNotNull.joinToString(", ")
//            project.executeWriteCommand(text) {
//                throws.replace(ThrowsAnnotationBll.createThrowsAnnotation(it, fullText))
//            }
//        }
//    } ?: Unit
//}
