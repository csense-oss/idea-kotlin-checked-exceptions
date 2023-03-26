package csense.idea.kotlin.checked.exceptions.bll.files

import com.intellij.openapi.project.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import java.nio.file.*


class CachedFqNameFunctionParameterStorage private constructor(
    private val fileCache: CachedFileInMemory<HashSet<CachedFqNameFunctionParameter>>
) {

    init {
        fileCache.reload()
    }

    fun addEntry(entry: CachedFqNameFunctionParameter) = tryAndLog {
        fileCache.updateWith { it: HashSet<CachedFqNameFunctionParameter> ->
            it.add(entry)
            it
        }
    }

    fun removeEntry(entry: CachedFqNameFunctionParameter) = tryAndLog {
        fileCache.updateWith { it: HashSet<CachedFqNameFunctionParameter> ->
            it.remove(entry)
            it
        }
    }

    fun contains(entry: CachedFqNameFunctionParameter): Boolean = tryAndLog {
        return fileCache.withCurrentValue { it: HashSet<CachedFqNameFunctionParameter> ->
            it.contains(entry)
        }
    } ?: false


    companion object {

        fun forProjectOrNull(
            project: Project,
            fileName: String
        ): CachedFqNameFunctionParameterStorage? {
            val rootPath: String = project.basePath ?: return null
            val cachedFile: Path = Paths.get(rootPath, fileName)
            val cache: CachedFileInMemory<HashSet<CachedFqNameFunctionParameter>> = CachedFileInMemory(
                initial = hashSetOf(),
                filePath = cachedFile,
                serialization = ::serialize,
                deserialization = ::deserialize
            )
            return CachedFqNameFunctionParameterStorage(fileCache = cache)
        }

        private fun serialize(
            toSerialize: HashSet<CachedFqNameFunctionParameter>
        ): String =
            toSerialize.map { it: CachedFqNameFunctionParameter ->
                it.serializeToString()
            }.joinToStringNewLine()

        private fun deserialize(string: String): HashSet<CachedFqNameFunctionParameter> {
            return string.lines().mapNotNull { it: String ->
                CachedFqNameFunctionParameter.fromLineOrNull(it)
            }.toHashSet()
        }
    }
}