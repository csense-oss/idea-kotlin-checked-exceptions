package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.kotlin.checked.exceptions.builtin.operations.*
import csense.idea.kotlin.checked.exceptions.repo.*

fun KtPsiFunction?.throwsTypesForSettingsOrEmpty(): List<KtPsiClass> {
    return this?.throwsTypesForSettings().orEmpty()
}

fun KtPsiFunction.throwsTypesForSettings(): List<KtPsiClass> {
    return throwsTypesOrBuiltIn().filterRuntimeExceptionsBySettings()
}

fun KtPsiFunction?.throwsTypesForCallBySettingsOrEmpty(): List<KtPsiClass> {
    this ?: return emptyList()
    return throwsTypesForSettings() + KDocRepo.parseThrowingExceptionTypes(this)
}