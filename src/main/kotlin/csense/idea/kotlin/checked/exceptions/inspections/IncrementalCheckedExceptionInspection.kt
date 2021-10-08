package csense.idea.kotlin.checked.exceptions.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.project.*
import com.intellij.psi.*
import com.intellij.psi.impl.source.*
import com.intellij.psi.search.*
import csense.idea.base.bll.kotlin.*
import csense.idea.base.bll.psi.*
import csense.idea.kotlin.checked.exceptions.bll.*
import csense.idea.kotlin.checked.exceptions.bll.Constants
import csense.idea.kotlin.checked.exceptions.callthough.*
import csense.idea.kotlin.checked.exceptions.ignore.*
import csense.idea.kotlin.checked.exceptions.quickfixes.*
import csense.kotlin.extensions.*
import csense.kotlin.extensions.collections.*
import org.jetbrains.kotlin.idea.inspections.*
import org.jetbrains.kotlin.nj2k.postProcessing.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.uast.*

class IncrementalCheckedExceptionInspection : AbstractKotlinInspection() {

    override fun getDisplayName(): String {
        return "Checked exceptions in kotlin"
    }

    override fun getStaticDescription(): String? {
        return "some desc"
    }

    override fun getDescriptionFileName(): String? {
        return "more desc ? "
    }

    override fun getShortName(): String {
        return "CheckedExceptionsKotlin"
    }

    override fun getGroupDisplayName(): String {
        return Constants.groupName
    }

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): KtVisitorVoid {
        return namedFunctionVisitor {
//            logMeasureTimeInMillis("incremental time") {
            val project = holder.project
            val visitor = IncrementalFunctionCheckedVisitor(holder, project)
            it.accept(visitor, IncrementalStep(listOf()))
//            }
        }
    }
}

class IncrementalFunctionCheckedVisitor(
    val holder: ProblemsHolder,
    val project: Project
) : KtTreeVisitor<IncrementalStep>() {

    private val javaThrowableUClass: UClass? by lazy {
        JavaPsiFacade.getInstance(project)
            .findClass("java.lang.Throwable", GlobalSearchScope.allScope(project))
            ?.toUElementOfType<UClass>()
    }

    override fun visitTryExpression(expression: KtTryExpression, data: IncrementalStep): Void? {

        val captures = expression.catchClauses.mapNotNull {
            it.catchParameter?.resolveTypeClassException(project)
//            {
//                javaThrowableUClass
//            }
        }
        val newState = data.copy(captures = data.captures + captures.filterRuntimeExceptionsBySettings())
        return super.visitTryExpression(expression, newState)
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: IncrementalStep): Void? {
        val throws = function.throwsTypes()
        val newData = IncrementalStep(
            data.captures + throws.filterRuntimeExceptionsBySettings(),
            isMarkedThrows = throws.isNotEmpty()
        )
        return super.visitNamedFunction(function, newData)
    }


    override fun visitCallExpression(expression: KtCallExpression, data: IncrementalStep): Void? {
        //skip if ignore.
        val thrown = expression.resolveMainReference()?.throwsTypesIfFunction(expression)
        val nonCaught = thrown?.filterOnlyNonCaught(data.captures)
        if (nonCaught.isNotNullOrEmpty()) {

            val throwTypes = nonCaught.map { it.name ?: "" }
            val text =
                "This call throws, so you should handle it with try catch, or declare that this method throws.\n It throws the following types:" +
                        throwTypes.joinToString(", ")
            holder.registerProblem(
                expression,
                text,
                *createQuickFixes(
                    expression,
                    throwTypes,
                    data.isMarkedThrows,
                    data.containingLambda
                )
            )
        }
        return super.visitCallExpression(expression, data)
    }


    override fun visitLambdaExpression(expression: KtLambdaExpression, data: IncrementalStep): Void? {
        val lambda = expression.asPotentialContainingLambda()
        val currentCaptures: List<UClass> = if (lambda != null) {
            when {
                //ignore => capture all
                lambda.isIgnored(IgnoreInMemory) -> {
                    javaThrowableUClass?.let { listOf(it) } ?: emptyList()
                }
                //call though => use parent captures
                lambda.isCallThough() -> {
                    data.captures
                }
                //else its just a normal lambda, thus it defines its own captures
                else -> listOf()
            }
        } else {
            data.captures
        }

        return super.visitLambdaExpression(
            expression,
            data.copy(
                captures = currentCaptures,
                containingLambda = lambda
            )
        )
    }

    private fun createQuickFixes(
        namedFunction: KtCallExpression,
        exceptionTypes: List<String>,
        haveThrowsAnnotation: Boolean,
        containingLambda: LambdaParameterData?
    ): Array<LocalQuickFix> {
        val declare: LocalQuickFix = haveThrowsAnnotation.mapLazy({
            AddFunctionThrowsQuickFix(namedFunction, exceptionTypes)
        }, {
            DeclareFunctionAsThrowsQuickFix(namedFunction, exceptionTypes)
        })

        val lambdaRelatedQuickfixes: Array<LocalQuickFix> = if (containingLambda != null) {
            val lambdaQuickFixes = mutableListOf<LocalQuickFix>()
            if (!containingLambda.isIgnored(IgnoreInMemory)) {
                lambdaQuickFixes.add(AddLambdaToIgnoreQuickFix(containingLambda.main, containingLambda.parameterName))
            }
            if (!containingLambda.isCallThough()) {
                lambdaQuickFixes.add(
                    AddLambdaToCallthoughQuickFix(
                        containingLambda.main,
                        containingLambda.parameterName
                    )
                )
            }
            lambdaQuickFixes.toTypedArray()
        } else {
            arrayOf()
        }

        return arrayOf(
            WrapInTryCatchQuickFix(namedFunction, exceptionTypes),
            declare
        ) + lambdaRelatedQuickfixes
    }

}

data class IncrementalStep(
    val captures: List<UClass>,
    val isMarkedThrows: Boolean = false,
    val containingLambda: LambdaParameterData? = null
)

//TODO add , getJavaThrowable: () -> UClass? instead to avoid searching for it multiple times.
fun KtCallableDeclaration.resolveTypeClassException(project: Project): UClass? {
    val resolved = this.resolveRealType()
    if (resolved?.getKotlinFqNameString() == "kotlin.Throwable") {
        return JavaPsiFacade.getInstance(project)
            .findClass("java.lang.Throwable", GlobalSearchScope.allScope(project))
            ?.toUElementOfType()
    }
    return resolved?.toUElementOfType()
}


fun KtProperty.resolveRealType(): PsiElement? {
    val type = typeReference
    if (type != null) {
        return type.resolve()?.resolveRealType()
    }
    val init = initializer
    if (init != null) {
        return init.resolveRealType()
    }
    val getter = getter
    if (getter != null) {
        return getter.resolveRealType()
    }
    return null
}

tailrec fun KtElement.resolveRealType(): PsiElement? {
    return when (this) {
        is KtCallExpression -> {
            val ref = resolveMainReferenceWithTypeAlias()
            ref?.resolveRealType()
        }
        is KtDotQualifiedExpression -> rightMostSelectorExpression()?.resolveRealType()
        is KtProperty -> resolveRealType()
        is KtSecondaryConstructor, is KtPrimaryConstructor -> {
            this.containingClass()
        }
        is KtReferenceExpression -> {
            resolve()?.resolveRealType()
        }
        is KtNamedFunction -> {
            this.getDeclaredReturnType()
        }
        else -> null
    }
}

fun PsiElement.resolveRealType(): PsiElement? {
    return when (this) {
        is KtElement -> resolveRealType()
        is PsiClass -> this
        is PsiMethod -> {
            if (this.isConstructor) {
                this.containingClass
            } else {
                (returnType as? PsiClassReferenceType)?.resolve()
            }

        }
        is PsiField -> {
            val project = project
            return JavaPsiFacade.getInstance(project)
                .findClass(this.type.canonicalText, this.type.resolveScope ?: GlobalSearchScope.allScope(project))
        }
        else -> null
    }
}

tailrec fun KtDotQualifiedExpression.rightMostSelectorExpression(): KtElement? {
    val selector = selectorExpression
    if (selector is KtDotQualifiedExpression) {
        return selector.rightMostSelectorExpression()
    }
    return selector
}


fun PsiElement.toUExceptionClass(cachedJavaLangThrowableUClass: UClass? = null): UClass? {
    return if (getKotlinFqNameString() == "kotlin.Throwable") {
        cachedJavaLangThrowableUClass ?: project.getJavaLangThrowableUClass()
    } else {
        toUElementOfType<UClass>()
    }
}

fun Project.getJavaLangThrowableUClass(): UClass? {
    return JavaPsiFacade.getInstance(this)
        .findClass("java.lang.Throwable", GlobalSearchScope.allScope(this))
        ?.toUElementOfType<UClass>()
}