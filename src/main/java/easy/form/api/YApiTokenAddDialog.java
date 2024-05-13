package easy.form.api;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.api.sdk.yapi.YApiClient;
import easy.api.sdk.yapi.model.ApiProject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class YApiTokenAddDialog extends DialogWrapper {

    private String apiServerUrl;
    private JTextField tokenTextField;

    public YApiTokenAddDialog(String apiServerUrl) {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        this.apiServerUrl = apiServerUrl;
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

    /**
     * 获取YApi项目信息
     *
     * @return {@link easy.api.sdk.yapi.model.ApiProject}
     * @author mabin
     * @date 2024/05/11 17:22
     */
    public ApiProject getApiProject() {
        String token = tokenTextField.getText();
        if (StringUtils.isAnyBlank(apiServerUrl, token)) {
            return null;
        }
        ApiProject apiProject = new YApiClient(apiServerUrl, token).getProject();
        if (Objects.nonNull(apiProject)) {
            apiProject.setToken(token);
        }
        return apiProject;
    }

}
