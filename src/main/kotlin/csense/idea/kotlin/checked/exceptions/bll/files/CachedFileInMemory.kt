package csense.idea.kotlin.checked.exceptions.bll.files

import csense.idea.kotlin.checked.exceptions.bll.callthough.*
import java.io.*
import java.nio.file.*
import kotlin.contracts.*

class CachedFileInMemory<T>(
    initial: T,
    private val filePath: Path,
    private val serialization: (T) -> String,
    private val deserialization: (String) -> T
) {

    private var lastFileModifiedTimeInMillis: Long? = null

    private var current: T = initial

    init {
        reload()
    }

    @Throws(IOException::class)
    fun reload() {
        if (!Files.exists(filePath)) {
            return
        }
        val currentLastModified: Long? = getLastModifiedInMillis()
        if (isSameLastModifiedAsLast(currentLastModified)) {
            return
        }
        updateCurrent(currentLastModified = currentLastModified)
    }

    private fun isSameLastModifiedAsLast(currentLastModified: Long?): Boolean {
        return currentLastModified != null && currentLastModified == lastFileModifiedTimeInMillis
    }

    @Throws(IOException::class)
    private fun updateCurrent(currentLastModified: Long?) {
        current = read()
        lastFileModifiedTimeInMillis = currentLastModified
    }

    @Throws(IOException::class)
    private fun read(): T {
        return deserialization(Files.readString(filePath))
    }


    @Throws(IOException::class)
    fun save() {
        val current: T & Any = current ?: return
        Files.writeString(filePath, serialization(current))
        lastFileModifiedTimeInMillis = getLastModifiedInMillis()
    }

    @Throws(IOException::class)
    private fun getLastModifiedInMillis(): Long? {
        if (!Files.exists(filePath)) {
            return null
        }
        return Files.getLastModifiedTime(filePath).toMillis()
    }

    @Throws(IOException::class)
    fun updateWith(update: (T) -> T) {
        reload()
        current = update(current)
        save()
    }

    @Throws(IOException::class)
    fun <R> withCurrentValue(onCurrent: (T) -> R): R {
        reload()
        return onCurrent(current)
    }
}