package csense.idea.kotlin.checked.exceptions.notification

import com.intellij.openapi.components.*
import csense.kotlin.logger.*

class CheckedExceptionsNotificationComponent : ProjectComponent {

    override fun projectOpened() {}

    override fun projectClosed() {}

    override fun initComponent() {
        L.usePrintAsLoggers()
        L.isLoggingAllowed(true)
    }

    override fun disposeComponent() {}
}
