package csense.idea.kotlin.checked.exceptions;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import csense.idea.kotlin.checked.exceptions.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class SettingsPaneUi {
    @NotNull
    public JCheckBox highlightGutterCheckBox;
    @NotNull
    public JPanel root;
    @NotNull
    public JSpinner maxDepthSpinner;
    @NotNull
    private JComboBox<String> nothingSeverity;
    @NotNull
    private JCheckBox highlightGutterThrowsFunctionsCheckbox;
    @NotNull
    private JCheckBox ignoreThrowsCheckbox;
    @NotNull
    private JCheckBox callthoughCheckbox;
    @NotNull
    private JCheckBox runtimeAsCheckedExceptionCheckBox;


    public SettingsPaneUi() {
        AbstractAction didChangeCallback = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDidChange(true);
            }
        };

        highlightGutterThrowsFunctionsCheckbox.setSelected(Settings.INSTANCE.getShouldHighlightThrowsExceptions());
        highlightGutterThrowsFunctionsCheckbox.setAction(didChangeCallback);

        highlightGutterCheckBox.setSelected(Settings.INSTANCE.getShouldHighlightCheckedExceptions());
        highlightGutterCheckBox.setAction(didChangeCallback);

        maxDepthSpinner.addChangeListener(e -> setDidChange(true));
        maxDepthSpinner.setModel(new SpinnerNumberModel(Settings.INSTANCE.getMaxDepth(), 1, 100, 1));

        ignoreThrowsCheckbox.setSelected(Settings.INSTANCE.getUseIgnoreFile());
        ignoreThrowsCheckbox.setAction(didChangeCallback);
        callthoughCheckbox.setSelected(Settings.INSTANCE.getUseCallThoughFile());
        callthoughCheckbox.setAction(didChangeCallback);

        runtimeAsCheckedExceptionCheckBox.setSelected(Settings.INSTANCE.getRuntimeAsCheckedException());
        runtimeAsCheckedExceptionCheckBox.setAction(didChangeCallback);


        final HighlightSeverity settingsSeverity = Settings.INSTANCE.getThrowsInsideOfFunctionSeverity();
        for (HighlightSeverity severity : HighlightSeverity.DEFAULT_SEVERITIES) {
            nothingSeverity.addItem(severity.getName());
        }
        nothingSeverity.setSelectedItem(settingsSeverity.getName());
        nothingSeverity.addItemListener(e -> setDidChange(true));


    }


    private boolean didChange = false;

    public void store() {
        try {
            maxDepthSpinner.commitEdit();
        } catch (java.text.ParseException e) {
            //... so bad.
        }
        int maxDepthValue = (Integer) maxDepthSpinner.getValue();
        Settings.INSTANCE.setShouldHighlightCheckedExceptions(highlightGutterCheckBox.isSelected());
        Settings.INSTANCE.setMaxDepth(maxDepthValue);
        Settings.INSTANCE.setShouldHighlightThrowsExceptions(highlightGutterThrowsFunctionsCheckbox.isSelected());
        Settings.INSTANCE.setUseIgnoreFile(ignoreThrowsCheckbox.isSelected());
        Settings.INSTANCE.setUseCallThoughFile(callthoughCheckbox.isSelected());
        Settings.INSTANCE.setRuntimeAsCheckedException(runtimeAsCheckedExceptionCheckBox.isSelected());

        final String value = (String) nothingSeverity.getSelectedItem();
        for (HighlightSeverity severity : HighlightSeverity.DEFAULT_SEVERITIES) {
            if (Objects.equals(value, severity.getName())) {
                Settings.INSTANCE.setThrowsInsideOfFunctionSeverity(severity);
                break;
            }
        }
        setDidChange(false);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Checked exceptions severity");
        root.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Highlight\n(via gutter) the checked exceptions locations ?");
        root.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        highlightGutterCheckBox = new JCheckBox();
        highlightGutterCheckBox.setText("");
        root.add(highlightGutterCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }


    public boolean didChange() {
        return didChange;
    }

    public void setDidChange(boolean didChange) {
        this.didChange = didChange;
    }

}
