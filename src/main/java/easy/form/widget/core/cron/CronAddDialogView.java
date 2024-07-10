package easy.form.widget.core.cron;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.base.Constants;
import easy.config.widget.WidgetConfig;
import easy.util.CronUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Objects;

public class CronAddDialogView extends DialogWrapper {

    private JPanel panel;
    private JTextField cronTextField;
    private JTextField descTextField;

    private WidgetConfig widgetConfig;

    public CronAddDialogView(WidgetConfig widgetConfig) {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("Cron Add View");
        this.widgetConfig = widgetConfig;
        init();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String cronValue = StringUtils.trim(cronTextField.getText());
        if (StringUtils.isBlank(cronValue)) {
            return new ValidationInfo("请输入Cron表达式", cronTextField);
        }
        if (!CronUtil.isCron(cronValue)) {
            return new ValidationInfo(String.format("Cron表达式【%s】不正确", cronValue), cronTextField);
        }
        String descValue = descTextField.getText();
        if (StringUtils.isBlank(descValue)) {
            return new ValidationInfo("请输入任务描述", descTextField);
        }
        if (descValue.length() > Constants.NUM.HUNDRED) {
            return new ValidationInfo(String.format("任务描述字符数要求<=%s", Constants.NUM.HUNDRED), descTextField);
        }
        return super.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 400), Math.max(size.height, 200));
        return panel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (Objects.isNull(widgetConfig)) {
            return;
        }
        LinkedHashMap<String, String> cronCollectionMap = widgetConfig.getCronCollectionMap();
        if (MapUtils.isNotEmpty(cronCollectionMap)) {
            cronCollectionMap.put(cronTextField.getText(), descTextField.getText());
        }
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

}
