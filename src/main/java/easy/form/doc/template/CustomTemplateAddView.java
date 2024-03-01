package easy.form.doc.template;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.config.doc.JavaDocTemplateConfig;
import easy.enums.JavaDocVariableTypeEnum;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

public class CustomTemplateAddView extends DialogWrapper {
    private JPanel panel;
    private JTextField methodName;
    private JTextField groovyCode;
    private JTextPane textPane;
    private JComboBox<String> customTypeComboBox;

    public CustomTemplateAddView() {
        super(false);
        init();
        setTitle("添加自定义方法");
        setSize(400, 500);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (methodName.getText() == null || methodName.getText().length() <= 0
            || !methodName.getText().startsWith("$") || !methodName.getText().endsWith("$")) {
            return new ValidationInfo("请输入方法名，并用$前后包裹，例如:$NAME$", methodName);
        }
        if (groovyCode.getText() == null || groovyCode.getText().length() <= 0) {
            return new ValidationInfo("请输入正确的自定义脚本", groovyCode);
        }
        if (customTypeComboBox.getSelectedItem() == null) {
            return new ValidationInfo("请选择自定义类型", groovyCode);
        }
        return super.doValidate();
    }

    public Map.Entry<String, JavaDocTemplateConfig.CustomValue> getEntry() {
        return new SimpleEntry<>(methodName.getText(),
            new JavaDocTemplateConfig.CustomValue(JavaDocVariableTypeEnum.fromDesc(String.valueOf(customTypeComboBox.getSelectedItem())), groovyCode.getText()));
    }

}
