package csense.idea.kotlin.checked.exceptions.notification

import com.intellij.openapi.components.*
import csense.kotlin.logger.*

class CheckedExceptionsNotificationComponent : ProjectComponent {

    override fun projectOpened() {}

    override fun projectClosed() {}

    override fun initComponent() {
        L.usePrintAsLoggers()
        L.isLoggingAllowed(true)
//        ApplicationManager.getApplication()
//                .invokeLater({
//                    Notifications.Bus.notify(NOTIFICATION_GROUP.value
//                            .createNotification(
//                                    "Testing Personal Plugin",
//                                    "Love kotlin",
//                                    NotificationType.INFORMATION,
//                                    null))
//                }, ModalityState.NON_MODAL)
    }

    override fun disposeComponent() {}

    override fun getComponentName(): String {
        return CUSTOM_NOTIFICATION_COMPONENT
    }

    companion object {
        private const val CUSTOM_NOTIFICATION_COMPONENT =
                "CustomNotificationComponent"
//        private val NOTIFICATION_GROUP = object :
//                NotNullLazyValue<NotificationGroup>() {
//            override fun compute(): NotificationGroup {
//                return NotificationGroup(
//                        "Motivational message",
//                        NotificationDisplayType.STICKY_BALLOON,
//                        true)
//            }
//        }
    }
}
