package easy.form;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

public class WordMapAddView extends DialogWrapper {

    private JPanel panel;
    private JTextField sourceTextField;
    private JTextField targetTextField;
    private JLabel source;
    private JLabel target;

    public WordMapAddView() {
        super(false);
        init();
        setTitle("Add Word Mapping");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (sourceTextField.getText() == null || sourceTextField.getText().isEmpty()) {
            return new ValidationInfo("Please enter Source Word!", sourceTextField);
        }
        if (targetTextField.getText() == null || targetTextField.getText().isEmpty()) {
            return new ValidationInfo("Please enter Target Word!", targetTextField);
        }
        return super.doValidate();
    }

    public Map.Entry<String, String> getMapping() {
        return new SimpleEntry<>(sourceTextField.getText().toLowerCase(), targetTextField.getText());
    }

}
