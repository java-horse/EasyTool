package easy.form.convert;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.base.Constants;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CharMappingEditView extends DialogWrapper {
    private JPanel panel;
    private JTextField chineseTextField;
    private JTextField englishTextField;

    public CharMappingEditView(String chinese, String english) {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("新增/更新映射字符");
        setCancelButtonText(BundleUtil.getI18n("global.button.cancel.text"));
        setOKButtonText(BundleUtil.getI18n("global.button.confirm.text"));
        if (StringUtils.isNoneBlank(english, chinese)) {
            setTitle("更新映射字符");
            chineseTextField.setText(chinese);
            englishTextField.setText(english);
        } else {
            setTitle("新增映射字符");
            chineseTextField.setToolTipText("请输入中文字符");
            englishTextField.setToolTipText("请输入英文字符");
        }
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 450), size.height);
        return panel;
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> validationInfoList = new ArrayList<>();
        if (StringUtils.isBlank(chineseTextField.getText()) || chineseTextField.getText().length() != Constants.NUM.ONE) {
            validationInfoList.add(new ValidationInfo("请输入合法中文字符", chineseTextField));
        }
        if (StringUtils.isBlank(englishTextField.getText()) || englishTextField.getText().length() != Constants.NUM.ONE) {
            validationInfoList.add(new ValidationInfo("请输入合法英文字符", englishTextField));
        }
        String cs = Constants.DEFAULT_CHAR_MAPPING.get(chineseTextField.getText());
        if (StringUtils.isNotBlank(cs) && !cs.equals(englishTextField.getText())) {
            validationInfoList.add(new ValidationInfo("请输入合法的英文映射字符", englishTextField));
        }
        return validationInfoList;
    }

    /**
     * 获取中英文字符映射
     *
     * @return {@link java.util.Map.Entry<java.lang.String,java.lang.String>}
     * @author mabin
     * @date 2024/08/03 10:32
     */
    public Map.Entry<String, String> getCharMapping() {
        return Map.entry(chineseTextField.getText(), englishTextField.getText());
    }

}
