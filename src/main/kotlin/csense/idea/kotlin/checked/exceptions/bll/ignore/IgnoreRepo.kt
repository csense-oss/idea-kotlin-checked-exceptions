package csense.idea.kotlin.checked.exceptions.bll.ignore

import com.intellij.openapi.project.*
import org.jetbrains.kotlin.psi.*

class IgnoreRepo(
    private val project: Project
){
    fun isLambdaIgnoreExceptions(lambda: KtLambdaExpression): Boolean {
        //TODO("Not yet implemented")
        return false
    }
}