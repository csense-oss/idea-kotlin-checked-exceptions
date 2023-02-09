package csense.idea.kotlin.checked.exceptions.intentionAction

import com.intellij.codeInsight.intention.impl.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.kotlin.extensions.*
import org.jetbrains.kotlin.psi.*

//class DeclareFunctionAsThrowsIntentionAction(
//    private val throwsExp: KtThrowExpression,
//    val throwType: String
//) : BaseIntentionAction() {
//    override fun getFamilyName(): String =
//        "Csense checked exceptions - intention action"
//
//    override fun getText(): String =
//        "Mark function as throws \"$throwType\""
//
//    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true
//
//    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) = tryAndLog {
//        WriteCommandAction.writeCommandAction(project).run<Throwable> {
//            throwsExp.getContainingFunctionOrPropertyAccessor()?.let {
//                it.addAnnotationEntry(ThrowsAnnotationBll.createThrowsAnnotation(it, throwType))
//            }
//        }
//    } ?: Unit
//}
