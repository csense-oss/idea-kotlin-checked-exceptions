package csense.idea.kotlin.checked.exceptions.builtin.operations

import com.intellij.openapi.project.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.builtin.*

fun KtPsiFunction.throwsTypesOrBuiltIn(project: Project): List<KtPsiClass> {
    return builtInThrowsTypes(project) + throwsTypes()
}

private fun KtPsiFunction.builtInThrowsTypes(project: Project): List<KtPsiClass> {
    val toLookup = FqNameReceiver(
        fqName = fqName ?: "",
        receiverFqName = receiverTypeOrNull()?.fqName
    )
    val builtIn: BuiltInThrowingFunction = KotlinThrowingFunctions.allBuiltIn[toLookup]
        ?: return emptyList()

    return builtIn.exceptionFqNames.mapNotNull { exceptionFqName: String ->
        KtPsiClass.resolve(
            fqName = exceptionFqName,
            project = project
        )
    }
}