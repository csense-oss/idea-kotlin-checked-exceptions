package csense.idea.kotlin.checked.exceptions.ignore

import com.intellij.openapi.project.*
import java.nio.file.*

//TODO sync ?
object IgnoreStorage {

    private var haveReadFile: Boolean = false

    private val current: MutableList<IgnoreEntry> = mutableListOf()
    //sync with the file ".ignore.throws" if this feature is enabled. (default it is).

    private fun read(project: Project): List<IgnoreEntry> {
        val rootPath = project.basePath ?: return listOf()
        return Files.readAllLines(Paths.get(rootPath, ".ignore.throws")).parseOrIgnore()
    }

    fun addEntry(project: Project, entry: IgnoreEntry) {
        ensureHaveReadFile(project)

    }

    fun removeEntry(project: Project, entry: IgnoreEntry) {
        ensureHaveReadFile(project)

    }

    fun getEntries(project: Project): List<IgnoreEntry> {
        ensureHaveReadFile(project)
        return current
    }

    private fun ensureHaveReadFile(project: Project) {
        if (!haveReadFile) {
            haveReadFile = true
            current.addAll(read(project))
        }
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

