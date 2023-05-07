package csense.idea.kotlin.checked.exceptions.repo

import com.intellij.openapi.project.*
import com.intellij.psi.*
import csense.idea.base.bll.psiWrapper.`class`.*
import csense.idea.base.bll.psiWrapper.`class`.operations.*
import csense.idea.base.bll.psiWrapper.documentation.*
import csense.idea.base.bll.psiWrapper.documentation.operations.*
import csense.idea.base.bll.psiWrapper.function.*
import csense.idea.base.bll.psiWrapper.function.operations.*
import csense.idea.base.bll.psiWrapper.imports.*
import csense.idea.base.bll.psiWrapper.imports.operations.*
import csense.kotlin.extensions.collections.*

object KDocRepo {
    fun parseThrowingExceptionTypes(fnc: KtPsiFunction): List<KtPsiClass> {
        val docs: KtPsiDoc? = fnc.documentation()
        val throwsAnnotations: List<KtPsiDocElement> =
            docs?.findAnnotations(searchString = throwsAnnotation, ignoreCase = true) ?: return emptyList()
        return throwsAnnotations.mapNotNull { it: KtPsiDocElement ->
            resolveClassTypeFromDocumentation(
                forElement = it,
                inFile = fnc.containingFile,
                project = fnc.project
            )
        }
    }


    private fun resolveClassTypeFromDocumentation(
        forElement: KtPsiDocElement,
        inFile: PsiFile?,
        project: Project
    ): KtPsiClass? {
        val fromTypeToEnd: String = forElement.line.substringAfter(throwsAnnotation).trim()
        val type: String = fromTypeToEnd.substringBefore(" ").trim()
        return tryResolveTypeFromComment(type, inFile = inFile, project)
    }

    private fun tryResolveTypeFromComment(type: String, inFile: PsiFile?, forProject: Project): KtPsiClass? {
        val imports: List<KtPsiImports> = inFile?.ktPsiImports().orEmpty()
        return tryResolveFromImportsOrNull(type = type, imports = imports, project = forProject)
            ?: tryResolveFromStarImportsOrNull(type = type, imports = imports, project = forProject)
            ?: tryResolveDirectly(type = type, forProject = forProject)
    }

    private fun tryResolveFromImportsOrNull(
        type: String,
        imports: List<KtPsiImports>,
        project: Project
    ): KtPsiClass? {
        val anyImportForType: KtPsiImports? = imports.firstOrNull { it: KtPsiImports ->
            it.isForType(type)
        }
        return anyImportForType?.let { it: KtPsiImports ->
            tryResolveTypeFqName(fqName = it.import, project = project)
        }
    }

    private fun tryResolveFromStarImportsOrNull(
        type: String,
        imports: List<KtPsiImports>,
        project: Project
    ): KtPsiClass? {
        val starImports: List<KtPsiImports> = imports.filter { it.isStarImport() }
        return starImports.selectFirstOrNull {
            tryResolveTypeFqName(fqName = it.import.replace("*", type), project = project)
        }
    }

    private fun tryResolveDirectly(
        type: String,
        forProject: Project
    ): KtPsiClass? {
        return KtPsiClass.resolve(
            fqName = type,
            project = forProject,
            useKotlin = true
        ) ?: KtPsiClass.resolve(
            fqName = type,
            project = forProject,
            useKotlin = false
        ) ?: KtPsiClass.resolve(
            fqName = "kotlin.$type",
            project = forProject,
            useKotlin = true
        )
    }

    fun tryResolveTypeFqName(fqName: String, project: Project): KtPsiClass? {
        return KtPsiClass.resolve(fqName = fqName, project = project, useKotlin = true)
            ?: KtPsiClass.resolve(fqName = fqName, project = project, useKotlin = false)
    }

    const val throwsAnnotation = "throws"
}


