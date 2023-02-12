package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.kotlin.checked.exceptions.builtin.operations.*

fun KtPsiFunction.throwsTypesForSettings(): List<KtPsiClass> {
    return throwsTypesOrBuiltIn().filterRuntimeExceptionsBySettings()
}
fun KtPsiFunction?.throwsTypesForSettingsOrEmpty(): List<KtPsiClass> {
    return this?.throwsTypesForSettings().orEmpty()
}