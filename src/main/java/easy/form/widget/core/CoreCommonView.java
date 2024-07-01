package easy.form.widget.core;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Arrays;
import java.util.Objects;

public class CoreCommonView {

    /**
     * 文件区域监听器
     *
     * @param area   面积
     * @param buttons 按钮集
     * @author mabin
     * @date 2024/06/29 16:47
     */
    protected void areaListener(JTextArea area, JButton... buttons) {
        if (Objects.isNull(area) || ArrayUtils.isEmpty(buttons)) {
            return;
        }
        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Arrays.stream(buttons).forEach(button -> button.setEnabled(StringUtils.isNotBlank(area.getText())));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Arrays.stream(buttons).forEach(button -> button.setEnabled(StringUtils.isNotBlank(area.getText())));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

}
