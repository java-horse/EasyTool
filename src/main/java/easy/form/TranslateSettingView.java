package easy.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.TranslateEnum;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Objects;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 11:45
 **/

public class TranslateSettingView {

    private static final Logger log = Logger.getInstance(TranslateSettingView.class);

    private TranslateConfig translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();

    private JPanel panel;
    private JPanel commonPanel;
    private JLabel translateChannelLabel;
    private JComboBox translateChannelBox;
    private JLabel appIdLabel;
    private JTextField appIdTextField;
    private JLabel appSecretLabel;
    private JTextField appSecretTextField;
    private JLabel secretIdLabel;
    private JTextField secretIdTextField;
    private JLabel secretKeyLabel;
    private JTextField secretKeyTextField;
    private JLabel accessKeyIdLabel;
    private JTextField accessKeyIdTextField;
    private JLabel accessKeySecretLabel;
    private JTextField accessKeySecretTextField;
    private JPanel supportPanel;
    private JButton clearButton;
    private JButton reviewButton;
    private JButton payButton;
    private JButton startButton;
    private JButton resetButton;

    /**
     * 在{@link #createUIComponents()}之后调用
     */
    public TranslateSettingView() {
        setVisibleChannel(translateChannelBox.getSelectedItem());
        resetButton.addActionListener(event -> {
            int result = JOptionPane.showConfirmDialog(null, "确认删除所有配置数据?", "重置数据",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                translateConfig.reset();
                refresh();
            }
        });
        clearButton.addActionListener(event -> {
            int result = JOptionPane.showConfirmDialog(null, "确认清空所有翻译缓存?", "清空缓存", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                // TODO 清空翻译数据缓存
            }
        });
        startButton.addActionListener(event -> {
            try {
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(URI.create(Constants.GITEE_URL));
                }
            } catch (Exception ex) {
                log.error("打开链接失败: " + Constants.GITEE_URL, ex);
            }
        });
        reviewButton.addActionListener(event -> {
            try {
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(URI.create(Constants.JETBRAINS_URL));
                }
            } catch (Exception ex) {
                log.error("打开链接失败: " + Constants.JETBRAINS_URL, ex);
            }
        });
        payButton.addActionListener(event -> {
            SupportView supportView = new SupportView();
            supportView.show();
        });

        translateChannelBox.addItemListener(e -> {
            JComboBox<?> jComboBox = (JComboBox<?>) e.getSource();
            setVisibleChannel(jComboBox.getSelectedItem());
        });
    }

    private void createUIComponents() {
        translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();
    }

    /**
     * 设置有效翻译渠道
     *
     * @param selectedItem
     * @return void
     * @author mabin
     * @date 2023/9/3 15:29
     **/
    private void setVisibleChannel(Object selectedItem) {
        if (Objects.equals(TranslateEnum.BAIDU.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(true);
            appIdTextField.setVisible(true);
            appSecretLabel.setVisible(true);
            appSecretTextField.setVisible(true);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.ALIYUN.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(true);
            accessKeyIdTextField.setVisible(true);
            accessKeySecretLabel.setVisible(true);
            accessKeySecretTextField.setVisible(true);
        } else if (Objects.equals(TranslateEnum.YOUDAO.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(true);
            secretIdTextField.setVisible(true);
            secretKeyLabel.setVisible(true);
            secretKeyTextField.setVisible(true);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
        }
    }

    /**
     * 刷新翻译渠道设置
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/3 15:31
     **/
    public void refresh() {
        setTranslateChannelBox(translateConfig.getTranslateChannel());
        setAppIdTextField(translateConfig.getAppId());
        setAppSecretTextField(translateConfig.getAppSecret());
        setSecretIdTextField(translateConfig.getSecretId());
        setSecretKeyTextField(translateConfig.getSecretKey());
        setAccessKeyIdTextField(translateConfig.getAccessKeyId());
        setAccessKeySecretTextField(translateConfig.getAccessKeySecret());
    }


    public JComponent getComponent() {
        return panel;
    }

    public JLabel getTranslateChannelLabel() {
        return translateChannelLabel;
    }

    public void setTranslateChannelLabel(JLabel translateChannelLabel) {
        this.translateChannelLabel = translateChannelLabel;
    }

    public JComboBox getTranslateChannelBox() {
        return translateChannelBox;
    }

    public void setTranslateChannelBox(String translateChannel) {
        this.translateChannelBox.setSelectedItem(translateChannel);
    }

    public JLabel getAppIdLabel() {
        return appIdLabel;
    }

    public void setAppIdLabel(JLabel appIdLabel) {
        this.appIdLabel = appIdLabel;
    }

    public JTextField getAppIdTextField() {
        return appIdTextField;
    }

    public void setAppIdTextField(String appId) {
        this.appIdTextField.setText(appId);
    }

    public JLabel getAppSecretLabel() {
        return appSecretLabel;
    }

    public void setAppSecretLabel(JLabel appSecretLabel) {
        this.appSecretLabel = appSecretLabel;
    }

    public JTextField getAppSecretTextField() {
        return appSecretTextField;
    }

    public void setAppSecretTextField(String appSecrt) {
        this.appSecretTextField.setText(appSecrt);
    }

    public JLabel getSecretIdLabel() {
        return secretIdLabel;
    }

    public void setSecretIdLabel(JLabel secretIdLabel) {
        this.secretIdLabel = secretIdLabel;
    }

    public JTextField getSecretIdTextField() {
        return secretIdTextField;
    }

    public void setSecretIdTextField(String secretId) {
        this.secretIdTextField.setText(secretId);
    }

    public JLabel getSecretKeyLabel() {
        return secretKeyLabel;
    }

    public void setSecretKeyLabel(JLabel secretKeyLabel) {
        this.secretKeyLabel = secretKeyLabel;
    }

    public JTextField getSecretKeyTextField() {
        return secretKeyTextField;
    }

    public void setSecretKeyTextField(String secretKey) {
        this.secretKeyTextField.setText(secretKey);
    }

    public JLabel getAccessKeyIdLabel() {
        return accessKeyIdLabel;
    }

    public void setAccessKeyIdLabel(JLabel accessKeyIdLabel) {
        this.accessKeyIdLabel = accessKeyIdLabel;
    }

    public JTextField getAccessKeyIdTextField() {
        return accessKeyIdTextField;
    }

    public void setAccessKeyIdTextField(String accessKeyId) {
        this.accessKeyIdTextField.setText(accessKeyId);
    }

    public JLabel getAccessKeySecretLabel() {
        return accessKeySecretLabel;
    }

    public void setAccessKeySecretLabel(JLabel accessKeySecretLabel) {
        this.accessKeySecretLabel = accessKeySecretLabel;
    }

    public JTextField getAccessKeySecretTextField() {
        return accessKeySecretTextField;
    }

    public void setAccessKeySecretTextField(String accessKeySecret) {
        this.accessKeySecretTextField.setText(accessKeySecret);
    }
}
