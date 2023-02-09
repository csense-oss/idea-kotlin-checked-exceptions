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

//fun JavaPsiFacade.javaClass(
//    classFqName: String,
//    inProject: Project
//): PsiClass? {
//    val name: String = resolveKotlinClassNameToJava(classFqName)
//    return findClass(
//        /* qualifiedName = */ name,
//        /* scope = */ GlobalSearchScope.allScope(inProject)
//    )
//}
//
////eg. see also ClassReference.kt etc.
//@Suppress("UnusedReceiverParameter")
//fun JavaPsiFacade.resolveKotlinClassNameToJava(kotlinFqName: String): String {
//    return JavaToKotlinClassMap.mapKotlinToJava(
//        FqNameUnsafe(kotlinFqName)
//    )?.asSingleFqName()?.asString() ?: kotlinFqName
//}