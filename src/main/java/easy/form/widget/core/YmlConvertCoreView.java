package easy.form.widget.core;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.MessageConstants;
import easy.util.MessageUtil;
import easy.util.YmlAndPropUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class YmlConvertCoreView extends CoreCommonView {
    private JPanel panel;
    private JTextArea propTextArea;
    private JTextArea ymlTextArea;
    private JButton prop2YmlButton;
    private JButton yml2PropButton;
    private JButton clearButton;

    public YmlConvertCoreView() {
        prop2YmlButton.setIcon(AllIcons.General.ArrowDown);
        prop2YmlButton.setEnabled(false);
        areaListener(propTextArea, prop2YmlButton);
        yml2PropButton.setIcon(AllIcons.General.ArrowUp);
        yml2PropButton.setEnabled(false);
        areaListener(ymlTextArea, yml2PropButton);
        prop2YmlButton.addActionListener(e -> ymlTextArea.setText(YmlAndPropUtil.convertYml(propTextArea.getText())));
        yml2PropButton.addActionListener(e -> propTextArea.setText(YmlAndPropUtil.convertProp(ymlTextArea.getText())));
        clearButton.setIcon(AllIcons.Actions.GC);
        clearButton.addActionListener(e -> {
            if (ObjectUtils.anyNotNull(propTextArea.getText(), ymlTextArea.getText())
                    && MessageUtil.showOkCancelDialog("Confirm Clear Data?") == MessageConstants.OK) {
                propTextArea.setText(StringUtils.EMPTY);
                ymlTextArea.setText(StringUtils.EMPTY);
            }
        });
    }

    public JPanel getContent() {
        return this.panel;
    }
}
