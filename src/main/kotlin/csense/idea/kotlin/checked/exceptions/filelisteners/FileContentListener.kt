package csense.idea.kotlin.checked.exceptions.filelisteners

import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.newvfs.*
import com.intellij.openapi.vfs.newvfs.events.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.repo.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.primitives.*

class FileContentListener(
    val project: Project
) : BulkFileListener {

    val nameEndingsToWatchFor: List<String> by lazy {
        listOf(
            IgnoreRepo.ignoreFileName,
            CallThoughRepo.callthoughProjectFileName
        )
    }

    private val repos: ProjectClassResolutionInterface by lazy {
        ProjectClassResolutionInterface.getOrCreate(project)
    }

    override fun after(events: MutableList<out VFileEvent>) {
        super.after(events)
        val anyChanged: Boolean = events.any { it: VFileEvent ->
            it.path.endsWithAny(nameEndingsToWatchFor)
        }
        if (!anyChanged) {
            return
        }
        tryAndLog {
            repos.ignoreRepo.reload()
            repos.callThoughRepo.reload()
        }
    }
}