package easy.form.widget.core;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncodeUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.MessageConstants;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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
    private JButton clearButton;

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

        clearButton.setIcon(AllIcons.Actions.GC);
        clearButton.addActionListener(e -> {
            if (ObjectUtils.anyNotNull(upTextArea.getText(), downTextArea.getText())
                    && MessageUtil.showOkCancelDialog("Confirm Clear Data?") == MessageConstants.OK) {
                upTextArea.setText(StringUtils.EMPTY);
                downTextArea.setText(StringUtils.EMPTY);
            }
        });
    }

    public JPanel getContent() {
        return this.panel;
    }

}
