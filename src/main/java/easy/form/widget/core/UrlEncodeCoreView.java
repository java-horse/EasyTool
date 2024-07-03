package easy.form.widget.core;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncodeUtil;
import com.intellij.icons.AllIcons;
import easy.util.EasyCommonUtil;
import easy.widget.core.CoreCommonView;

import javax.swing.*;
import java.nio.charset.StandardCharsets;


/**
 * Url编码
 *
 * @author mabin
 * @project EasyTool
 * @package easy.form.widget
 * @date 2024/06/29 09:59
 */
public class UrlEncodeCoreView extends CoreCommonView {
    private JPanel panel;
    private JButton downButton;
    private JButton upButton;
    private JTextArea upTextArea;
    private JTextArea downTextArea;
    private JLabel encodeTipLabel;

    public UrlEncodeCoreView() {
        downButton.setIcon(AllIcons.General.ArrowDown);
        downButton.setEnabled(false);
        areaListener(upTextArea, downButton);
        downButton.addActionListener(e -> downTextArea.setText(URLEncodeUtil.encode(upTextArea.getText(), StandardCharsets.UTF_8)));
        upButton.setIcon(AllIcons.General.ArrowUp);
        upButton.setEnabled(false);
        areaListener(downTextArea, upButton);
        upButton.addActionListener(e -> upTextArea.setText(URLDecoder.decode(downTextArea.getText(), StandardCharsets.UTF_8)));
        EasyCommonUtil.customLabelTipText(encodeTipLabel, String.format("默认【%s】编码", StandardCharsets.UTF_8));
    }

    public JPanel getContent() {
        return this.panel;
    }

}
