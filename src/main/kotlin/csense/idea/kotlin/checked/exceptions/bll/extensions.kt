package csense.idea.kotlin.checked.exceptions.bll

import csense.kotlin.extensions.*


/**
 *
 * @param value Int
 * @return T?
 */
inline fun <reified T : Enum<T>> enumFromOrNull(value: Int): T? {
    return enumFromOr<T> { it.ordinal == value }
}


/**
 *
 * @param value Int
 * @param orElse T
 * @return T
 */
inline fun <reified T : Enum<T>> enumFromOr(value: Int, orElse: T): T {
    return enumFromOrNull<T>(value) ?: orElse
}
