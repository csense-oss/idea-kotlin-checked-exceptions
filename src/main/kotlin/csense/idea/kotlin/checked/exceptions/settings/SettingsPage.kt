package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.openapi.options.*
import csense.idea.base.bll.linemarkers.*
import csense.idea.kotlin.checked.exceptions.*
import csense.kotlin.extensions.*
import javax.swing.*

class SettingsPage : SearchableConfigurable {
    private var ui: SettingsPaneUi? = null
    
    override fun isModified(): Boolean {
        return ui?.didChange() ?: false
    }
    
    override fun getId(): String {
        return "csenseKotlinCheckedExceptionsSettingsPage"
    }
    
    override fun getDisplayName(): String {
        return "Csense - Kotlin Checked Exceptions"
    }
    
    override fun apply() {
        ui?.store()
        restartLineMarkersForAllProjects()
    }
    
    override fun createComponent(): JComponent? {
        return tryAndLog {
            ui = SettingsPaneUi()
            ui?.root
        }
    }
    
    override fun disposeUIResources() {
        ui = null
    }
    
}