package easy.form.widget.core;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CoreCommonView {

    /**
     * 文件区域监听器
     *
     * @param area   面积
     * @param button 按钮
     * @author mabin
     * @date 2024/06/29 16:47
     */
    protected void areaListener(JTextArea area, JButton button) {
        if (ObjectUtils.anyNull(area, button)) {
            return;
        }
        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                button.setEnabled(StringUtils.isNotBlank(area.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                button.setEnabled(StringUtils.isNotBlank(area.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

}
