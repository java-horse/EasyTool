package easy.form.api;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class YApiTokenAddDialog extends DialogWrapper {

    private JTextField tokenTextField;

    public YApiTokenAddDialog() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("请填写YApi项目Token");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        tokenTextField = new JTextField();
        panel.add(tokenTextField);
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 500), size.height);
        return panel;
    }

    @Override
    protected ValidationInfo doValidate() {
        if (StrUtil.isBlank(tokenTextField.getText()) || tokenTextField.getText().length() < 64) {
            return new ValidationInfo("请输入正确的YApi的项目Token", tokenTextField);
        }
        return super.doValidate();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

}
