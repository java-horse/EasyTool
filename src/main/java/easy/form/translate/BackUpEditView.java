package easy.form.translate;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.enums.TranslateEnum;
import easy.util.BundleUtil;
import easy.util.EventBusUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BackUpEditView extends DialogWrapper {
    private JPanel panel;
    private JTextField sourceTextField;
    private JTextField targetTextField;
    private JTextField channelTextField;

    public BackUpEditView(String source, String target, String channel) {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("新增/更新备份数据");
        setCancelButtonText(BundleUtil.getI18n("global.button.cancel.text"));
        setOKButtonText(BundleUtil.getI18n("global.button.confirm.text"));
        if (StringUtils.isNoneBlank(source, target, channel)) {
            setTitle("更新备份数据");
            sourceTextField.setText(source);
            sourceTextField.setEnabled(false);
            sourceTextField.setEditable(false);
            targetTextField.setText(target);
            channelTextField.setText(channel);
            channelTextField.setEnabled(false);
            channelTextField.setEditable(false);
        } else {
            setTitle("新增备份数据");
            sourceTextField.setToolTipText("请输入源数据");
            targetTextField.setToolTipText("请输入目标数据");
            channelTextField.setToolTipText("请输入翻译引擎");
        }
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 500), size.height);
        return panel;
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> validationInfoList = new ArrayList<>();
        if (StringUtils.isBlank(sourceTextField.getText())) {
            validationInfoList.add(new ValidationInfo("请输入源数据", sourceTextField));
        }
        if (StringUtils.isBlank(targetTextField.getText())) {
            validationInfoList.add(new ValidationInfo("请输入目标数据", targetTextField));
        }
        if (StringUtils.isBlank(channelTextField.getText()) || !TranslateEnum.getTranslator().contains(channelTextField.getText())) {
            validationInfoList.add(new ValidationInfo("请输入有效翻译引擎", channelTextField));
        }
        return validationInfoList;
    }

    /**
     * 获取翻译备份
     *
     * @return {@link easy.util.EventBusUtil.TranslateBackUpEvent}
     * @author mabin
     * @date 2024/07/31 09:37
     */
    public EventBusUtil.TranslateBackUpEvent getTranslateBackUp() {
        return new EventBusUtil.TranslateBackUpEvent(sourceTextField.getText(), targetTextField.getText(), channelTextField.getText());
    }

}
