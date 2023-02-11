package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.module.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.jetbrains.kotlin.psi.*


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

fun KtPsiClass.shouldSkip(): Boolean {
    return Settings.ignoreRuntimeExceptions && isSubtypeOfRuntimeException()
}