package csense.idea.kotlin.checked.exceptions.callthough

import com.intellij.openapi.project.*
import csense.kotlin.extensions.*
import java.io.*
import java.nio.file.*

object CallthoughStorage {
    
    private var lastFileModifiedTime: Long? = null
    
    private val current: MutableList<CallthoughEntry> = mutableListOf()
    //sync with the file ".callthought.throws" if this feature is enabled. (default it is).
    
    @Throws(IOException::class)
    private fun read(project: Project): List<CallthoughEntry> {
        val path = resolvePath(project) ?: return listOf()
        return Files.readAllLines(path).parseOrIgnore()
    }
    
    fun addEntry(project: Project, entry: CallthoughEntry) = tryAndLog {
        ensureHaveReadFile(project)
        //TODO do not double add ?
        current.add(entry)
        saveFile(project)
    }
    
    fun removeEntry(project: Project, entry: CallthoughEntry) = tryAndLog {
        ensureHaveReadFile(project)
        current.remove(entry)
        saveFile(project)
    }
    
    fun getEntries(project: Project): List<CallthoughEntry> = tryAndLog {
        ensureHaveReadFile(project)
        current
    } ?: listOf()
    
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
        return Paths.get(rootPath, ".callthough.throws")
    }
}

private fun List<String>.parseOrIgnore(): List<CallthoughEntry> = mapNotNull {
    val raw = it.split(' ')
    if (raw.size != 2) {
        null
    } else {
        val (fqName, name) = raw
        CallthoughEntry(fqName, name)
    }
}

