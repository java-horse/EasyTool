package easy.form.doc;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BatchJavaDocCheckView extends DialogWrapper {
    private final JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private JPanel panel;
    private JCheckBox classCheckBox;
    private JCheckBox methodCheckBox;
    private JCheckBox fieldCheckBox;
    private JCheckBox innerClassCheckBox;
    private JCheckBox getAndSetMethodCheckBox;
    private JCheckBox constructorMethodCheckBox;
    private JLabel methodTipLabel;

    public BatchJavaDocCheckView() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle(String.format("%s Batch Generate JavaDoc", Constants.PLUGIN_NAME));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        EasyCommonUtil.customLabelTipText(methodTipLabel, "默认不包含Get/Set方法以及构造函数方法");
        classCheckBox.setToolTipText("批量生成类注释");
        methodCheckBox.setToolTipText("批量生成方法注释");
        fieldCheckBox.setToolTipText("批量生成属性注释");
        innerClassCheckBox.setToolTipText("递归扫描内部类信息");
        getAndSetMethodCheckBox.setToolTipText("批量生成Get/Set方法");
        constructorMethodCheckBox.setToolTipText("批量生成构造函数方法");
        classCheckBox.setSelected(javaDocConfig.getClassBatchEnable());
        methodCheckBox.setSelected(javaDocConfig.getMethodBatchEnable());
        fieldCheckBox.setSelected(javaDocConfig.getFieldBatchEnable());
        innerClassCheckBox.setSelected(javaDocConfig.getInnerClassBatchEnable());
        getAndSetMethodCheckBox.setSelected(javaDocConfig.getGetAndSetMethodBatchEnable());
        constructorMethodCheckBox.setSelected(javaDocConfig.getConstructorMethodBatchEnable());
        return panel;
    }

    @Override
    protected void doOKAction() {
        if (!methodCheckBox.isSelected() && (getAndSetMethodCheckBox.isSelected() || constructorMethodCheckBox.isSelected())) {
            MessageUtil.showInfoMessage(String.format("请先选中【%s】前提选项", methodCheckBox.getText()));
            return;
        }
        javaDocConfig.setClassBatchEnable(classCheckBox.isSelected());
        javaDocConfig.setMethodBatchEnable(methodCheckBox.isSelected());
        javaDocConfig.setFieldBatchEnable(fieldCheckBox.isSelected());
        javaDocConfig.setInnerClassBatchEnable(innerClassCheckBox.isSelected());
        javaDocConfig.setGetAndSetMethodBatchEnable(getAndSetMethodCheckBox.isSelected());
        javaDocConfig.setConstructorMethodBatchEnable(constructorMethodCheckBox.isSelected());
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

}
