package csense.idea.kotlin.checked.exceptions.bll

import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
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