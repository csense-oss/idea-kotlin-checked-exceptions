package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.ide.util.*
import csense.idea.base.settings.propertiesComponent.*

object Settings {

    private const val settingsPrefixed = "CsenseCheckedExceptionKotlinV2"

    private val backend: PropertiesComponent by lazy {
        PropertiesComponent.getInstance()
    }

    var ignoreRuntimeExceptions: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed
    )

}
