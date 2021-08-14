package csense.idea.kotlin.checked.exceptions.settings

import com.intellij.codeInsight.daemon.*
import com.intellij.openapi.options.*
import com.intellij.openapi.project.*
import csense.idea.kotlin.checked.exceptions.*
import csense.idea.kotlin.checked.exceptions.cache.*
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
        return "Csense - Kotlin Checked exceptions"
    }
    
    override fun apply() {
        ui?.store()
        ProjectManager.getInstance().openProjects.forEach {
            DaemonCodeAnalyzer.getInstance(it).restart()
        }
      
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