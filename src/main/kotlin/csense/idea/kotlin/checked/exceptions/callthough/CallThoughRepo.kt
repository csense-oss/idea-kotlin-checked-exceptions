package csense.idea.kotlin.checked.exceptions.callthough

import com.intellij.openapi.project.*
import org.jetbrains.kotlin.psi.*

class CallThoughRepo(
    private val project: Project
) {

    fun isLambdaCallThough(lambda: KtLambdaExpression): Boolean {
        //TODO("Not yet implemented")
        return false
    }
}