package csense.idea.kotlin.checked.exceptions.bll.ignore

import com.intellij.openapi.project.*
import csense.kotlin.extensions.*
import java.io.*
import java.nio.file.*

object IgnoreStorage {

    private var lastFileModifiedTime: Long? = null

    private val current: HashSet<IgnoreEntry> = hashSetOf()
    //sync with the file ".ignore.throws" if this feature is enabled. (default it is).

    @Throws(IOException::class)
    private fun read(project: Project): List<IgnoreEntry> {
        val path = resolvePath(project) ?: return listOf()
        return Files.readAllLines(path).parseOrIgnore()
    }

    fun addEntry(project: Project, entry: IgnoreEntry) = tryAndLog {
        ensureHaveReadFile(project)
        current.add(entry)
        saveFile(project)
    }

    fun removeEntry(project: Project, entry: IgnoreEntry) = tryAndLog {
        ensureHaveReadFile(project)
        current.remove(entry)
        saveFile(project)
    }

    fun contains(mainFqName: String, parameterName: String, project: Project): Boolean = tryAndLog {
        ensureHaveReadFile(project)
        current.contains(IgnoreEntry(mainFqName, parameterName))
    } ?: false

    @Throws(IOException::class)
    private fun ensureHaveReadFile(project: Project) {
        val path = resolvePath(project) ?: return
        val last = getLastAccessed(project) ?: return
        if (last != lastFileModifiedTime && Files.exists(path)) {
            current.clear()
            current.addAll(read(project))
            lastFileModifiedTime = last
        }
    }

    @Throws(IOException::class)
    private fun getLastAccessed(project: Project): Long? {
        val path = resolvePath(project) ?: return null
        return if (Files.exists(path)) {
            Files.getLastModifiedTime(path).toMillis()
        } else {
            null
        }
    }

    @Throws(IOException::class)
    private fun saveFile(project: Project) {
        val path = resolvePath(project) ?: return
        Files.write(path, current.map {
            "${it.fullName} ${it.parameterName}"
        })
        lastFileModifiedTime = getLastAccessed(project)
    }

    private fun resolvePath(project: Project): Path? {
        val rootPath = project.basePath ?: return null
        return Paths.get(rootPath, ".ignore.throws")
    }


}

private fun List<String>.parseOrIgnore(): List<IgnoreEntry> = mapNotNull {
    val raw = it.split(' ')
    if (raw.size != 2) {
        null
    } else {
        val (fqName, name) = raw
        IgnoreEntry(fqName, name)
    }
}

