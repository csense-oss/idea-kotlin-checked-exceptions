package csense.idea.kotlin.checked.exceptions.bll.callthough

import com.intellij.openapi.project.*
import csense.idea.kotlin.checked.exceptions.bll.files.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import java.nio.file.*

class CallthoughStorage private constructor(
    private val fileCache: CachedFileInMemory<HashSet<CallthoughEntry>>
) {

    init {
        fileCache.reload()
    }

    fun addEntry(entry: CallthoughEntry) = tryAndLog {
        fileCache.updateWith { it: HashSet<CallthoughEntry> ->
            it.add(entry)
            it
        }
    }

    fun removeEntry(entry: CallthoughEntry) = tryAndLog {
        fileCache.updateWith { it: HashSet<CallthoughEntry> ->
            it.remove(entry)
            it
        }
    }

    fun contains(entry: CallthoughEntry): Boolean = tryAndLog {
        return fileCache.withCurrentValue { it: HashSet<CallthoughEntry> ->
            it.contains(entry)
        }
    } ?: false


    companion object {
        const val callthoughProjectFileName: String = ".callthough.throws"

        fun forProjectOrNull(project: Project): CallthoughStorage? {
            val rootPath: String = project.basePath ?: return null
            val callthoughFilePath: Path = Paths.get(rootPath, callthoughProjectFileName)
            val cache: CachedFileInMemory<HashSet<CallthoughEntry>> = CachedFileInMemory(
                initial = hashSetOf(),
                filePath = callthoughFilePath,
                serialization = ::serialize,
                deserialization = ::deserialize
            )
            return CallthoughStorage(fileCache = cache)
        }

        private fun serialize(toSerialize: HashSet<CallthoughEntry>): String {
            return toSerialize.map { it: CallthoughEntry ->
                "${it.fullName} ${it.parameterName}"
            }.joinToStringNewLine()
        }

        private fun deserialize(rawString: String): HashSet<CallthoughEntry> {
            return rawString.lines().mapNotNull { it: String ->
                parseSingleLine(it)
            }.toHashSet()
        }

        private fun parseSingleLine(line: String): CallthoughEntry? {
            val raw: List<String> = line.split(delimiters = arrayOf(" "))
            if (raw.size != 2) {
                return null
            }
            val (fqName: String, name: String) = raw
            return CallthoughEntry(fqName, name)
        }
    }
}

