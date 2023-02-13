package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.openapi.project.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*


class ProjectClassResolutionInterface(
    private val project: Project
) {

    val kotlinOrJavaThrowable: KtPsiClass? by lazy {
        KtPsiClass.getKotlinThrowable(project) ?: KtPsiClass.getJavaThrowable(project)
    }

    companion object {

        private val serviceMap: MutableMap<Project, ProjectClassResolutionInterface> = mutableMapOf()

        fun getOrCreate(forProject: Project): ProjectClassResolutionInterface {
            return serviceMap.getOrPut(key = forProject, defaultValue = {
                ProjectClassResolutionInterface(project = forProject)
            })
        }
    }
}
