package easy.form.widget.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.ui.MessageConstants;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashSet;
import java.util.Set;

public class UuidCoreView extends CoreCommonView {
    private JPanel panel;
    private JTextArea uuidTextArea;
    private JSpinner uuidCountSpinner;
    private JSpinner uuidLengthSpinner;
    private JComboBox uuidTypeComboBox;
    private JButton generateButton;
    private JButton clearButton;
    private JButton copyButton;
    private JComboBox upperComboBox;

    private static final String UUID_SIMPLE_TYPE = "简化（去除横线）";
    private static final String UPPER = "大写";

    public UuidCoreView() {
        uuidCountSpinner.setModel(new SpinnerNumberModel(10, 1, 10000, 5));
        uuidLengthSpinner.setModel(new SpinnerNumberModel(36, 10, 36, 1));
        clearButton.setIcon(AllIcons.Actions.GC);
        copyButton.setIcon(AllIcons.Actions.Copy);
        areaListener(uuidTextArea, clearButton, copyButton);
        generateButton.setIcon(AllIcons.General.ArrowDown);
        generateButton.addActionListener(e -> {
            Set<String> uuidSet = new HashSet<>(16);
            if (StringUtils.isNotBlank(uuidTextArea.getText()) && MessageUtil.showOkCancelDialog("是否重新生成UUID?") != MessageConstants.OK) {
                return;
            }
            for (int i = 0; i < Convert.toInt(uuidCountSpinner.getValue()); i++) {
                String simpleUUID;
                Integer uuidLength = Convert.toInt(uuidLengthSpinner.getValue());
                if (StringUtils.equals(UUID_SIMPLE_TYPE, Convert.toStr(uuidTypeComboBox.getSelectedItem()))) {
                    simpleUUID = IdUtil.fastSimpleUUID();
                    if (uuidLength > 32) {
                        uuidLength = 32;
                    }
                } else {
                    simpleUUID = IdUtil.fastUUID();
                }
                if (uuidLength < simpleUUID.length()) {
                    simpleUUID = StringUtils.substring(simpleUUID, 0, uuidLength);
                }
                if (StringUtils.equals(UPPER, Convert.toStr(upperComboBox.getSelectedItem()))) {
                    simpleUUID = simpleUUID.toUpperCase();
                }
                uuidSet.add(simpleUUID);
            }
            uuidTextArea.setText(String.join(StringUtils.LF, uuidSet));
        });
        clearButton.addActionListener(e -> {
            if (StringUtils.isBlank(uuidTextArea.getText())) {
                return;
            }
            if (MessageUtil.showOkCancelDialog("是否清空UUID?") == MessageConstants.OK) {
                uuidTextArea.setText(StringUtils.EMPTY);
            }
        });
        copyButton.addActionListener(e -> {
            if (StringUtils.isBlank(uuidTextArea.getText())) {
                return;
            }
            CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(uuidTextArea.getText()));
        });
    }

    public JComponent getContent() {
        return panel;
    }

}
