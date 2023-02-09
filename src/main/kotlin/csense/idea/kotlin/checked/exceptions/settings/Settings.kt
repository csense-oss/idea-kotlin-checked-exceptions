package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.ide.util.*
import com.intellij.lang.annotation.*
import kotlin.reflect.*

object Settings {

    private const val settingsPrefixed = "CsenseCheckedExceptionKotlin"

    private val backend: PropertiesComponent by lazy {
        PropertiesComponent.getInstance()
    }

    var shouldHighlightCheckedExceptions: Boolean by BooleanSetting(backend, settingsPrefixed)

    var shouldHighlightThrowsExceptions: Boolean by BooleanSetting(backend, settingsPrefixed, defaultValue = true)

    var shouldHighlightThrowsInsideOfFunction: Boolean by BooleanSetting(
        backend,
        settingsPrefixed,
        defaultValue = false
    )

    var useIgnoreFile: Boolean by BooleanSetting(backend, settingsPrefixed, "Name")

    var useCallThoughFile: Boolean by BooleanSetting(backend, settingsPrefixed, "Name")

    var maxDepth: Int by IntSetting(backend, settingsPrefixed, "Name", 10, 1)

    var ignoreRuntimeExceptions: Boolean by BooleanSetting(backend, settingsPrefixed)

    private const val throwsInsideOfFunctionSeverityName = settingsPrefixed + "throwsInsideOfFunctionSeverity"
    var throwsInsideOfFunctionSeverity: HighlightSeverity
        get() = backend.getValue(throwsInsideOfFunctionSeverityName)?.let { name ->
            HighlightSeverity.DEFAULT_SEVERITIES.firstOrNull {
                it.name == name
            }
        } ?: HighlightSeverity.WARNING
        set(newValue) {
            val valueToSave = newValue.name
            backend.setValue(throwsInsideOfFunctionSeverityName, valueToSave)
        }
}

class IntSetting(
    private val backend: PropertiesComponent,
    private val settingsNamePrefix: String,
    private val postfixName: String = "",
    private val defaultValue: Int = 0,
    private val minValue: Int = Int.MIN_VALUE,
    private val maxValue: Int = Int.MAX_VALUE
) {
    operator fun getValue(prop: Any, property: KProperty<*>): Int {
        return backend.getInt(settingsNamePrefix + property.name + postfixName, defaultValue)
    }

    operator fun setValue(prop: Any, property: KProperty<*>, newValue: Int) {
        val safeValue = newValue.coerceAtLeast(minValue).coerceAtMost(maxValue)
        backend.setValue(settingsNamePrefix + property.name + postfixName, safeValue, defaultValue)
    }
}

class BooleanSetting(
    private val backend: PropertiesComponent,
    private val settingsNamePrefix: String,
    private val postfixName: String = "",
    private val defaultValue: Boolean = true
) {
    operator fun getValue(prop: Any, property: KProperty<*>): Boolean {
        return backend.getBoolean(settingsNamePrefix + property.name + postfixName, defaultValue)
    }

    operator fun setValue(prop: Any, property: KProperty<*>, newValue: Boolean) {
        backend.setValue(settingsNamePrefix + property.name + postfixName, newValue, defaultValue)
    }

}