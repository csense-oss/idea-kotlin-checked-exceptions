package csense.idea.kotlin.checked.exceptions.builtin.operations

import com.intellij.openapi.project.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.kotlin.checked.exceptions.builtin.*
import csense.idea.kotlin.checked.exceptions.builtin.throws.*

fun KtPsiFunction.throwsTypesOrBuiltIn(): List<KtPsiClass> {
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
        KtPsiClass.resolveByKotlin(
            fqName = exceptionFqName,
            project = project
        )
    }
}