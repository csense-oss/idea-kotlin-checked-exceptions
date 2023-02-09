package csense.idea.kotlin.checked.exceptions.bll

import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import csense.idea.kotlin.checked.exceptions.annotator.*
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.builtins.jvm.*
import org.jetbrains.kotlin.name.*


class ProjectClassResolutionInterface(
    private val project: Project
) {
    private val psiFacade: JavaPsiFacade by lazy {
        JavaPsiFacade.getInstance(project)
    }

    private val cachedThrowable: PsiClass? by lazy {
        psiFacade.javaClass(
            classFqName = kotlinThrowableClassName,
            inProject = project
        )
    }

    fun getThrowable(): PsiClass? {
        return cachedThrowable
    }

    //Todo this is bad, and should be redesigned around services or alike.
    companion object {

        const val kotlinThrowableClassName: String = "kotlin.Throwable"

        private val serviceMap: MutableMap<Project, ProjectClassResolutionInterface> = mutableMapOf()

        fun getOrCreate(forProject: Project): ProjectClassResolutionInterface {
            return serviceMap.getOrPut(key = forProject, defaultValue = {
                ProjectClassResolutionInterface(project = forProject)
            })
        }
    }
}

fun JavaPsiFacade.javaClass(
    classFqName: String,
    inProject: Project
): PsiClass? {
    val name: String = resolveKotlinClassNameToJava(classFqName)
    return findClass(
        /* qualifiedName = */ name,
        /* scope = */ GlobalSearchScope.allScope(inProject)
    )
}

//eg. see also ClassReference.kt etc.
@Suppress("UnusedReceiverParameter")
fun JavaPsiFacade.resolveKotlinClassNameToJava(kotlinFqName: String): String {
    return JavaToKotlinClassMap.mapKotlinToJava(
        FqNameUnsafe(kotlinFqName)
    )?.asSingleFqName()?.asString() ?: kotlinFqName
}