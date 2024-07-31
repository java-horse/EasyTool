package easy.form.translate;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Objects;

public class WordMapAddView extends DialogWrapper {

    private JPanel panel;
    private JTextField sourceTextField;
    private JTextField targetTextField;
    private JLabel source;
    private JLabel target;
    private Map<String, String> typeMap;

    public WordMapAddView(Map<String, String> typeMap) {
        super(false);
        this.typeMap = typeMap;
        init();
        setTitle(BundleUtil.getI18n("global.word.mapping.text"));
        setCancelButtonText(BundleUtil.getI18n("global.button.cancel.text"));
        setOKButtonText(BundleUtil.getI18n("global.button.confirm.text"));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        EasyCommonUtil.customBackgroundText(sourceTextField, BundleUtil.getI18n("global.source.word.tip.text"));
        EasyCommonUtil.customBackgroundText(targetTextField, BundleUtil.getI18n("global.target.word.tip.text"));
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 500), size.height);
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (sourceTextField.getText() == null || sourceTextField.getText().isEmpty()
                || StringUtils.equals(sourceTextField.getText().trim(), BundleUtil.getI18n("global.source.word.tip.text"))) {
            return new ValidationInfo(BundleUtil.getI18n("global.source.word.tip.text"), sourceTextField);
        }
        if (Objects.nonNull(typeMap.get(sourceTextField.getText().trim()))) {
            return new ValidationInfo(BundleUtil.getI18n("global.word.mapping.repeat.text"), sourceTextField);
        }
        if (targetTextField.getText() == null || targetTextField.getText().isEmpty()
                || StringUtils.equals(targetTextField.getText().trim(), BundleUtil.getI18n("global.target.word.tip.text"))) {
            return new ValidationInfo(BundleUtil.getI18n("global.target.word.tip.text"), targetTextField);
        }
        return super.doValidate();
    }

    public Map.Entry<String, String> getMapping() {
        return new SimpleEntry<>(sourceTextField.getText().toLowerCase(), targetTextField.getText());
    }

}
