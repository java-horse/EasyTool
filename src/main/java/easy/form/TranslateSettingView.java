package easy.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.TranslateEnum;
import easy.service.TranslateService;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 11:45
 **/

public class TranslateSettingView {

    private static final Logger log = Logger.getInstance(TranslateSettingView.class);

    private TranslateConfig translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);

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
    private JLabel tencentSecretIdLabel;
    private JLabel tencentSecretKeyLabel;
    private JTextField tencentSecretIdTextField;
    private JTextField tencentSecretKeyTextField;
    private JLabel volcanoSecretIdLabel;
    private JLabel volcanoSecretKeyLabel;
    private JTextField volcanoSecretIdTextField;
    private JTextField volcanoSecretKeyTextField;
    private JLabel xfAppIdLabel;
    private JTextField xfAppIdTextField;
    private JLabel xfApiSecretLabel;
    private JTextField xfApiSecretTextField;
    private JLabel xfApiKeyLabel;
    private JTextField xfApiKeyTextField;
    private JLabel googleSecretKeyLabel;
    private JTextField googleSecretKeyTextField;
    private JLabel microsoftKeyLabel;
    private JTextField microsoftKeyTextField;
    private JLabel niuApiKeyLabel;
    private JTextField niuApiKeyTextField;
    private JLabel caiyunTokenLabel;
    private JTextField caiyunTokenTextField;

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
                translateService.clearCache();
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
     * 设置翻译渠道
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
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
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
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
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
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.TENCENT.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(true);
            tencentSecretIdTextField.setVisible(true);
            tencentSecretKeyLabel.setVisible(true);
            tencentSecretKeyTextField.setVisible(true);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.VOLCANO.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(true);
            volcanoSecretKeyLabel.setVisible(true);
            volcanoSecretIdTextField.setVisible(true);
            volcanoSecretKeyTextField.setVisible(true);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.XFYUN.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(true);
            xfAppIdTextField.setVisible(true);
            xfApiSecretLabel.setVisible(true);
            xfApiSecretTextField.setVisible(true);
            xfApiKeyLabel.setVisible(true);
            xfApiKeyTextField.setVisible(true);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.GOOGLE.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(true);
            googleSecretKeyTextField.setVisible(true);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.MICROSOFT.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(true);
            microsoftKeyTextField.setVisible(true);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.NIU.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(true);
            niuApiKeyTextField.setVisible(true);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
        } else if (Objects.equals(TranslateEnum.CAIYUN.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(true);
            caiyunTokenTextField.setVisible(true);
        } else {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
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
        setTencentSecretIdTextField(translateConfig.getTencentSecretId());
        setTencentSecretKeyTextField(translateConfig.getTencentSecretKey());
        setVolcanoSecretIdTextField(translateConfig.getVolcanoSecretId());
        setVolcanoSecretKeyTextField(translateConfig.getVolcanoSecretKey());
        setXfAppIdTextField(translateConfig.getXfAppId());
        setXfApiKeyTextField(translateConfig.getXfApiKey());
        setXfApiSecretTextField(translateConfig.getXfApiSecret());
        setGoogleSecretKeyTextField(translateConfig.getGoogleSecretKey());
        setMicrosoftKeyTextField(translateConfig.getMicrosoftKey());
        setNiuApiKeyTextField(translateConfig.getNiuApiKey());
        setCaiyunTokenTextField(translateConfig.getCaiyunToken());
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

    public JLabel getTencentSecretIdLabel() {
        return tencentSecretIdLabel;
    }

    public void setTencentSecretIdLabel(JLabel tencentSecretIdLabel) {
        this.tencentSecretIdLabel = tencentSecretIdLabel;
    }

    public JLabel getTencentSecretKeyLabel() {
        return tencentSecretKeyLabel;
    }

    public void setTencentSecretKeyLabel(JLabel tencentSecretKeyLabel) {
        this.tencentSecretKeyLabel = tencentSecretKeyLabel;
    }

    public JTextField getTencentSecretIdTextField() {
        return tencentSecretIdTextField;
    }

    public void setTencentSecretIdTextField(String tencentSecretIdTextField) {
        this.tencentSecretIdTextField.setText(tencentSecretIdTextField);
    }

    public JTextField getTencentSecretKeyTextField() {
        return tencentSecretKeyTextField;
    }

    public void setTencentSecretKeyTextField(String tencentSecretKeyTextField) {
        this.tencentSecretKeyTextField.setText(tencentSecretKeyTextField);
    }

    public JLabel getVolcanoSecretIdLabel() {
        return volcanoSecretIdLabel;
    }

    public void setVolcanoSecretIdLabel(JLabel volcanoSecretIdLabel) {
        this.volcanoSecretIdLabel = volcanoSecretIdLabel;
    }

    public JLabel getVolcanoSecretKeyLabel() {
        return volcanoSecretKeyLabel;
    }

    public void setVolcanoSecretKeyLabel(JLabel volcanoSecretKeyLabel) {
        this.volcanoSecretKeyLabel = volcanoSecretKeyLabel;
    }

    public JTextField getVolcanoSecretIdTextField() {
        return volcanoSecretIdTextField;
    }

    public void setVolcanoSecretIdTextField(String volcanoSecretIdTextField) {
        this.volcanoSecretIdTextField.setText(volcanoSecretIdTextField);
    }

    public JTextField getVolcanoSecretKeyTextField() {
        return volcanoSecretKeyTextField;
    }

    public void setVolcanoSecretKeyTextField(String volcanoSecretKeyTextField) {
        this.volcanoSecretKeyTextField.setText(volcanoSecretKeyTextField);
    }

    public JLabel getXfAppIdLabel() {
        return xfAppIdLabel;
    }

    public void setXfAppIdLabel(JLabel xfAppIdLabel) {
        this.xfAppIdLabel = xfAppIdLabel;
    }

    public JTextField getXfAppIdTextField() {
        return xfAppIdTextField;
    }

    public void setXfAppIdTextField(String xfAppIdTextField) {
        this.xfAppIdTextField.setText(xfAppIdTextField);
    }

    public JLabel getXfApiSecretLabel() {
        return xfApiSecretLabel;
    }

    public void setXfApiSecretLabel(JLabel xfApiSecretLabel) {
        this.xfApiSecretLabel = xfApiSecretLabel;
    }

    public JTextField getXfApiSecretTextField() {
        return xfApiSecretTextField;
    }

    public void setXfApiSecretTextField(String xfApiSecretTextField) {
        this.xfApiSecretTextField.setText(xfApiSecretTextField);
    }

    public JLabel getXfApiKeyLabel() {
        return xfApiKeyLabel;
    }

    public void setXfApiKeyLabel(JLabel xfApiKeyLabel) {
        this.xfApiKeyLabel = xfApiKeyLabel;
    }

    public JTextField getXfApiKeyTextField() {
        return xfApiKeyTextField;
    }

    public void setXfApiKeyTextField(String xfApiKeyTextField) {
        this.xfApiKeyTextField.setText(xfApiKeyTextField);
    }

    public JLabel getGoogleSecretKeyLabel() {
        return googleSecretKeyLabel;
    }

    public void setGoogleSecretKeyLabel(JLabel googleSecretKeyLabel) {
        this.googleSecretKeyLabel = googleSecretKeyLabel;
    }

    public JTextField getGoogleSecretKeyTextField() {
        return googleSecretKeyTextField;
    }

    public void setGoogleSecretKeyTextField(String googleSecretKeyTextField) {
        this.googleSecretKeyTextField.setText(googleSecretKeyTextField);
    }

    public JLabel getMicrosoftKeyLabel() {
        return microsoftKeyLabel;
    }

    public void setMicrosoftKeyLabel(JLabel microsoftKeyLabel) {
        this.microsoftKeyLabel = microsoftKeyLabel;
    }

    public JTextField getMicrosoftKeyTextField() {
        return microsoftKeyTextField;
    }

    public void setMicrosoftKeyTextField(String microsoftKeyTextField) {
        this.microsoftKeyTextField.setText(microsoftKeyTextField);
    }

    public JLabel getNiuApiKeyLabel() {
        return niuApiKeyLabel;
    }

    public void setNiuApiKeyLabel(JLabel niuApiKeyLabel) {
        this.niuApiKeyLabel = niuApiKeyLabel;
    }

    public JTextField getNiuApiKeyTextField() {
        return niuApiKeyTextField;
    }

    public void setNiuApiKeyTextField(String niuApiKeyTextField) {
        this.niuApiKeyTextField.setText(niuApiKeyTextField);
    }

    public JLabel getCaiyunTokenLabel() {
        return caiyunTokenLabel;
    }

    public void setCaiyunTokenLabel(JLabel caiyunTokenLabel) {
        this.caiyunTokenLabel = caiyunTokenLabel;
    }

    public JTextField getCaiyunTokenTextField() {
        return caiyunTokenTextField;
    }

    public void setCaiyunTokenTextField(String caiyunTokenTextField) {
        this.caiyunTokenTextField.setText(caiyunTokenTextField);
    }

}
