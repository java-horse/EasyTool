package easy.form.widget.core;

import com.intellij.icons.AllIcons;
import easy.util.YmlAndPropUtil;

import javax.swing.*;

public class YmlConvertDialogView extends CoreCommonView {
    private JPanel panel;
    private JTextArea propTextArea;
    private JTextArea ymlTextArea;
    private JButton prop2YmlButton;
    private JButton yml2PropButton;

    public YmlConvertDialogView() {
        prop2YmlButton.setIcon(AllIcons.General.ArrowDown);
        prop2YmlButton.setEnabled(false);
        areaListener(propTextArea, prop2YmlButton);
        yml2PropButton.setIcon(AllIcons.General.ArrowUp);
        yml2PropButton.setEnabled(false);
        areaListener(ymlTextArea, yml2PropButton);
        prop2YmlButton.addActionListener(e -> ymlTextArea.setText(YmlAndPropUtil.convertYml(propTextArea.getText())));
        yml2PropButton.addActionListener(e -> propTextArea.setText(YmlAndPropUtil.convertProp(ymlTextArea.getText())));
    }

    public JPanel getContent() {
        return this.panel;
    }
}
