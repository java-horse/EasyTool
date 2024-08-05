package easy.form.doc;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.helper.ServiceHelper;
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

    public BatchJavaDocCheckView() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle(String.format("%s Batch Generate JavaDoc", Constants.PLUGIN_NAME));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        classCheckBox.setToolTipText("批量生成类注释");
        methodCheckBox.setToolTipText("批量生成方法注释");
        fieldCheckBox.setToolTipText("批量生成属性注释");
        innerClassCheckBox.setToolTipText("批量生成内部类注释");
        classCheckBox.setSelected(javaDocConfig.getClassBatchEnable());
        methodCheckBox.setSelected(javaDocConfig.getMethodBatchEnable());
        fieldCheckBox.setSelected(javaDocConfig.getFieldBatchEnable());
        innerClassCheckBox.setSelected(javaDocConfig.getInnerClassBatchEnable());
        return panel;
    }

    @Override
    protected void doOKAction() {
        javaDocConfig.setClassBatchEnable(classCheckBox.isSelected());
        javaDocConfig.setMethodBatchEnable(methodCheckBox.isSelected());
        javaDocConfig.setFieldBatchEnable(fieldCheckBox.isSelected());
        javaDocConfig.setInnerClassBatchEnable(innerClassCheckBox.isSelected());
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

}
