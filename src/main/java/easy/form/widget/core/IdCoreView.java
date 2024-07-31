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

public class IdCoreView extends CoreCommonView {
    private JPanel panel;
    private JTextArea uuidTextArea;
    private JComboBox<String> idTypeComboBox;
    private JSpinner uuidCountSpinner;
    private JSpinner uuidLengthSpinner;
    private JComboBox<String> uuidTypeComboBox;
    private JComboBox<String> upperComboBox;
    private JButton generateButton;
    private JButton clearButton;
    private JButton copyButton;

    private static final String UUID_SIMPLE_TYPE = "简化（去除横线）";
    private static final String UPPER = "大写";
    private static final String UUID = "UUID";
    private static final String NANO_ID = "NanoId";
    private static final String OBJECT_ID = "ObjectId";
    private static final String SNOWFLAKE_ID = "SnowflakeId";

    public IdCoreView() {
        uuidCountSpinner.setModel(new SpinnerNumberModel(10, 1, 10000, 5));
        uuidLengthSpinner.setModel(new SpinnerNumberModel(36, 10, 36, 1));
        idTypeComboBox.addItemListener(e -> {
            if (!StringUtils.equals(UUID, Convert.toStr(idTypeComboBox.getSelectedItem()))) {
                uuidLengthSpinner.setEnabled(false);
                uuidTypeComboBox.setEnabled(false);
                upperComboBox.setEnabled(false);
                return;
            }
            uuidLengthSpinner.setEnabled(true);
            uuidTypeComboBox.setEnabled(true);
            upperComboBox.setEnabled(true);
        });

        clearButton.setIcon(AllIcons.Actions.GC);
        copyButton.setIcon(AllIcons.Actions.Copy);
        areaListener(uuidTextArea, clearButton, copyButton);
        generateButton.setIcon(AllIcons.General.ArrowDown);
        generateButton.addActionListener(e -> {
            Set<String> idSet = new HashSet<>(Convert.toInt(uuidCountSpinner.getValue()));
            String idType = Convert.toStr(idTypeComboBox.getSelectedItem());
            if (StringUtils.isNotBlank(uuidTextArea.getText())
                    && MessageUtil.showOkCancelDialog(String.format("是否重新生成%s?", idType)) != MessageConstants.OK) {
                return;
            }
            switch (idType) {
                case UUID -> genUuid(idSet);
                case NANO_ID -> genNanoId(idSet);
                case OBJECT_ID -> genObjectId(idSet);
                case SNOWFLAKE_ID -> genSnowflakeId(idSet);
            }
            uuidTextArea.setText(String.join(StringUtils.LF, idSet));
        });
        clearButton.addActionListener(e -> {
            if (StringUtils.isBlank(uuidTextArea.getText())) {
                return;
            }
            if (MessageUtil.showOkCancelDialog(String.format("是否清空%s?", Convert.toStr(idTypeComboBox.getSelectedItem()))) == MessageConstants.OK) {
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

    /**
     * 生成SnowflakeId
     *
     * @param idSet id设置
     * @author mabin
     * @date 2024/07/23 16:13
     */
    private void genSnowflakeId(Set<String> idSet) {
        for (int i = 0; i < Convert.toInt(uuidCountSpinner.getValue()); i++) {
            idSet.add(IdUtil.getSnowflakeNextIdStr());
        }
    }

    /**
     * 生成ObjectId
     *
     * @param idSet id设置
     * @author mabin
     * @date 2024/07/23 16:13
     */
    private void genObjectId(Set<String> idSet) {
        for (int i = 0; i < Convert.toInt(uuidCountSpinner.getValue()); i++) {
            idSet.add(IdUtil.objectId());
        }
    }

    /**
     * 生成NanoId
     *
     * @param idSet id设置
     * @author mabin
     * @date 2024/07/23 16:12
     */
    private void genNanoId(Set<String> idSet) {
        for (int i = 0; i < Convert.toInt(uuidCountSpinner.getValue()); i++) {
            idSet.add(IdUtil.nanoId());
        }
    }

    /**
     * 生成UUID
     *
     * @param idSet id设置
     * @author mabin
     * @date 2024/07/23 16:10
     */
    private void genUuid(Set<String> idSet) {
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
            idSet.add(simpleUUID);
        }
    }

    public JComponent getContent() {
        return panel;
    }

}
