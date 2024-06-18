package easy.form.doc.template;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.config.doc.JavaDocTemplateConfig;
import easy.enums.JavaDocVariableTypeEnum;
import easy.util.EasyCommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Objects;

public class CustomTemplateAddView extends DialogWrapper {
    private JPanel panel;
    private JTextField methodName;
    private JTextField groovyCode;
    private JTextPane textPane;
    private JComboBox<String> customTypeComboBox;
    private JLabel methodNameTipLabel;
    private JLabel customValueTipLabel;

    public CustomTemplateAddView() {
        super(false);
        init();
        setTitle("添加自定义变量");
        setSize(400, 550);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        EasyCommonUtil.customLabelTipText(methodNameTipLabel, "请输入变量名，并用$前后包裹，例如:$AUTHOR$");
        EasyCommonUtil.customLabelTipText(customValueTipLabel, "请选择下方Groovy脚本名称填入，例如:className");
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(methodName.getText()) || !methodName.getText().startsWith("$") || !methodName.getText().endsWith("$")) {
            return new ValidationInfo("请输入变量名，并用$前后包裹，例如:$AUTHOR$", methodName);
        }
        if (StringUtils.isBlank(groovyCode.getText())) {
            return new ValidationInfo("请输入正确的自定义脚本", groovyCode);
        }
        if (Objects.isNull(customTypeComboBox.getSelectedItem())) {
            return new ValidationInfo("请选择自定义类型", groovyCode);
        }
        return super.doValidate();
    }

    public Map.Entry<String, JavaDocTemplateConfig.CustomValue> getEntry() {
        return new SimpleEntry<>(methodName.getText(),
                new JavaDocTemplateConfig.CustomValue(JavaDocVariableTypeEnum.fromDesc(String.valueOf(customTypeComboBox.getSelectedItem())), groovyCode.getText()));
    }

}
