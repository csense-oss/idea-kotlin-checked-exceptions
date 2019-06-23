package csense.idea.kotlin.checked.exceptions.bll

inline fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}

inline fun <T> Collection<T>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}