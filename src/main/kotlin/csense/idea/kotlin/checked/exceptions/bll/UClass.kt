package csense.idea.kotlin.checked.exceptions.bll

import org.jetbrains.uast.*

fun List<UClass>.toTypeList(): List<String> = map {
    it.qualifiedName ?: ""
}