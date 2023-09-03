package easy.form;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/09/01 15:13
 **/

public class SupportView extends DialogWrapper {

    private JLabel alipayLabel;
    private JLabel wechatLabel;
    private JPanel panel;

    public SupportView() {
        super(false);
        init();
        setTitle("感谢赞赏");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    private void createUIComponents() {
        ImageIcon wechatIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/wechat.png")));
        wechatIcon.setImage(wechatIcon.getImage().getScaledInstance((int) (wechatIcon.getIconWidth() * (400d / wechatIcon.getIconHeight())), 400, Image.SCALE_DEFAULT));
        wechatLabel = new JLabel(wechatIcon);
        wechatLabel.setVisible(true);

        ImageIcon alipayIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/alipay.png")));
        alipayIcon.setImage(alipayIcon.getImage().getScaledInstance((int) (alipayIcon.getIconWidth() * (400d / alipayIcon.getIconHeight())), 400, Image.SCALE_DEFAULT));
        alipayLabel = new JLabel(alipayIcon);
        alipayLabel.setVisible(true);
    }

}
