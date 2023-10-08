package easy.form;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 16:31
 **/

public class WeChatOfficialView extends DialogWrapper {

    private JPanel panel;
    private JLabel weChatOfficialLabel;

    public WeChatOfficialView() {
        super(false);
        init();
        setTitle("更多精彩, 尽在<IT小滑头>公众号");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    private void createUIComponents() {
        ImageIcon wechatIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/wechat_official.jpg")));
        wechatIcon.setImage(wechatIcon.getImage().getScaledInstance((int) (wechatIcon.getIconWidth() * (400d / wechatIcon.getIconHeight())), 400, Image.SCALE_DEFAULT));
        weChatOfficialLabel = new JLabel(wechatIcon);
        weChatOfficialLabel.setVisible(true);
    }

}
