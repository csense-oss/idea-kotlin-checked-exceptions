package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.`is`.*
import csense.idea.kotlin.checked.exceptions.settings.*

fun List<KtPsiClass>.filterRuntimeExceptionsBySettings(): List<KtPsiClass> = when {
    !Settings.ignoreRuntimeExceptions -> this
    else -> filterNotSubTypeOfRuntimeException()
}

