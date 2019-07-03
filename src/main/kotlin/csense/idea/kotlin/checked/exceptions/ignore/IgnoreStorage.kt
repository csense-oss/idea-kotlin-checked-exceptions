package csense.idea.kotlin.checked.exceptions.ignore

import com.intellij.openapi.project.*
import java.nio.file.*

//TODO sync ?
object IgnoreStorage {

    private var lastFileModifiedTime: Long? = null

    private val current: MutableList<IgnoreEntry> = mutableListOf()
    //sync with the file ".ignore.throws" if this feature is enabled. (default it is).

    private fun read(project: Project): List<IgnoreEntry> {
        val path = resolvePath(project) ?: return listOf()
        return Files.readAllLines(path).parseOrIgnore()
    }

    fun addEntry(project: Project, entry: IgnoreEntry) {
        ensureHaveReadFile(project)
        //TODO do not double add ?
        current.add(entry)
        saveFile(project)
    }

    fun removeEntry(project: Project, entry: IgnoreEntry) {
        ensureHaveReadFile(project)
        current.remove(entry)
        saveFile(project)
    }

    fun getEntries(project: Project): List<IgnoreEntry> {
        ensureHaveReadFile(project)
        return current
    }

    private fun ensureHaveReadFile(project: Project) {
        val last = getLastAccessed(project)
        if (last != lastFileModifiedTime) {
            current.addAll(read(project))
        }
    }

    private fun getLastAccessed(project: Project): Long? {
        val path = resolvePath(project) ?: return null
        return if (Files.exists(path)) {
            Files.getLastModifiedTime(path).toMillis()
        } else {
            null
        }
    }

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

