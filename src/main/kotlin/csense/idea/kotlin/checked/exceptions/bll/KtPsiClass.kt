package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.`class`.operations.`is`.*
import csense.idea.kotlin.checked.exceptions.settings.*
import org.intellij.lang.annotations.*

fun List<KtPsiClass>.coloredString(cssColor: String, tagType: String = "b"): String {
    @Language("html")
    val typePrefix = "<$tagType style='color: $cssColor'>"
    return joinToString(
        separator = "</$tagType>, $typePrefix",
        prefix = typePrefix,
        postfix = "</$tagType>",
        transform = { ktPsiClass: KtPsiClass ->
            ktPsiClass.getFqNameTypeAliased().orEmpty()
        }
    )
}

fun List<KtPsiClass>.filterRuntimeExceptionsBySettings(): List<KtPsiClass> = when {
    !Settings.ignoreRuntimeExceptions -> this
    else -> filterNotSubTypeOfRuntimeException()
}

fun List<KtPsiClass>.filterNotSubTypeOfRuntimeException(): List<KtPsiClass> = filterNot { it: KtPsiClass ->
    it.isSubtypeOfRuntimeException()
}

//In short: in kotlin you cannot capture java.lang.Throwable
//and for some reason as the only type, kotlin.Throwable IS NOT a typealias.
//thus its actually unrelated type-wise, but behind the scenes (in the compiler) they are (the same?)
//so if there is any kotlin.throwable on the other side all exceptions are "related".
fun List<KtPsiClass>.filterUnrelatedExceptions(to: List<KtPsiClass>): List<KtPsiClass> {
    val anyRootKotlinThrowable: Boolean = to.any { it.isKotlinThrowable() }
    if (anyRootKotlinThrowable) {
        return emptyList()
    }
    return filterNonRelated(to)
}

