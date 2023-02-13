package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.ide.util.*
import com.intellij.lang.annotation.*
import csense.idea.base.bll.highligting.*
import csense.idea.base.settings.propertiesComponent.*

object Settings {

    private const val settingsPrefixed = "CsenseCheckedExceptionKotlin"

    private val backend: PropertiesComponent by lazy {
        PropertiesComponent.getInstance()
    }

    var shouldHighlightCheckedExceptions: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed
    )

    var shouldHighlightThrowsExceptions: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed, defaultValue = true
    )

    var shouldHighlightThrowsInsideOfFunction: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed,
        defaultValue = false
    )

    var useIgnoreFile: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed,
        postfixName = "Name"
    )

    var useCallThoughFile: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed,
        postfixName = "Name"
    )

    var ignoreRuntimeExceptions: Boolean by BooleanSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed
    )

    var throwsInsideOfFunctionSeverity: HighlightSeverity by SerializableStringSetting(
        backend = backend,
        settingsNamePrefix = settingsPrefixed,
        deserialize = { name: String ->
            HighlightSeverityCompat.fromName(name = name, default = HighlightSeverity.WARNING)
        },
        serialize = { it: HighlightSeverity ->
            it.name
        }
    )
}
