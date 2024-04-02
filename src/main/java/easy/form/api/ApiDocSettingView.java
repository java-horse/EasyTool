package easy.form.api;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import easy.api.entity.YApiProjectInfo;
import easy.api.service.YApiService;
import easy.config.api.ApiDocConfig;
import easy.config.api.ApiDocConfigComponent;
import easy.handler.ServiceHelper;
import easy.icons.EasyIcons;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;

public class ApiDocSettingView {

    private ApiDocConfig apiDocConfig = ServiceHelper.getService(ApiDocConfigComponent.class).getState();

    private JPanel panel;
    private JPanel yapiPanel;
    private JTextField yapiServerTextField;
    private JTextField yapiTokenTextField;
    private JButton yapiConnectionButton;
    private JTextField apifoxServerTextField;
    private JTextField apifoxTokenTextField;
    private JLabel yapiServerLabel;
    private JLabel yapiTokenLabel;
    private JPanel apifoxPanel;
    private JLabel apifoxServerLabel;
    private JLabel apifoxTokenLabel;
    private JButton apifoxConnectionButton;
    private JCheckBox yapiEnableCheckBox;
    private JLabel yapiIconLabel;
    private JLabel apifoxIconLabel;
    private JCheckBox apifoxEnableCheckBox;

    public ApiDocSettingView() {
        yapiIconLabel.setIcon(EasyIcons.ICON.YAPI);
        apifoxIconLabel.setIcon(EasyIcons.ICON.APIFOX);
        yapiConnectionButton.setEnabled(false);
        apifoxConnectionButton.setEnabled(false);
        yapiTokenTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                yapiConnectionButton.setEnabled(StringUtils.isNotBlank(yapiTokenTextField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                yapiConnectionButton.setEnabled(StringUtils.isNotBlank(yapiTokenTextField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        yapiConnectionButton.addActionListener(event -> {
            YApiService yApiService = ServiceHelper.getService(YApiService.class);
            YApiProjectInfo yApiProjectInfo = yApiService.getProjectInfo(yapiServerTextField.getText(), yapiTokenTextField.getText());
            if (Objects.isNull(yApiProjectInfo)) {
                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("☣️☣️☣️ YApi连接失败, 请检查相关配置!", MessageType.ERROR, null)
                        .setShowCallout(false)
                        .createBalloon().show(RelativePoint.getSouthOf(yapiConnectionButton), Balloon.Position.above);
            } else {
                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("🎉🎉🎉 YApi连接成功", MessageType.INFO, null)
                        .setShowCallout(false)
                        .createBalloon().show(RelativePoint.getSouthOf(yapiConnectionButton), Balloon.Position.above);
            }
        });
        apifoxTokenTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                apifoxConnectionButton.setEnabled(StringUtils.isNotBlank(apifoxTokenTextField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                apifoxConnectionButton.setEnabled(StringUtils.isNotBlank(apifoxTokenTextField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        apifoxConnectionButton.addActionListener(event -> {
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("ApiFox连接成功", MessageType.INFO, null)
                    .setShowCallout(false)
                    .createBalloon().show(RelativePoint.getSouthOf(apifoxConnectionButton), Balloon.Position.above);
        });
    }

    private void createUIComponents() {
        apiDocConfig = ApplicationManager.getApplication().getService(ApiDocConfigComponent.class).getState();
    }

    public void reset() {
        setYapiServerTextField(apiDocConfig.getYapiServer());
        setYapiTokenTextField(apiDocConfig.getYapiToken());
        setYapiEnableCheckBox(apiDocConfig.getYapiEnable());
        setApifoxServerTextField(apiDocConfig.getApifoxServer());
        setApifoxTokenTextField(apiDocConfig.getApifoxToken());
        setApifoxEnableCheckBox(apiDocConfig.getApifoxEnable());
    }

    public JComponent getComponent() {
        return panel;
    }

    public JPanel getYapiPanel() {
        return yapiPanel;
    }

    public void setYapiPanel(JPanel yapiPanel) {
        this.yapiPanel = yapiPanel;
    }

    public JTextField getYapiServerTextField() {
        return yapiServerTextField;
    }

    public void setYapiServerTextField(String yapiServerTextField) {
        this.yapiServerTextField.setText(yapiServerTextField);
    }

    public JTextField getYapiTokenTextField() {
        return yapiTokenTextField;
    }

    public void setYapiTokenTextField(String yapiTokenTextField) {
        this.yapiTokenTextField.setText(yapiTokenTextField);
    }

    public JTextField getApifoxServerTextField() {
        return apifoxServerTextField;
    }

    public void setApifoxServerTextField(String apifoxServerTextField) {
        this.apifoxServerTextField.setText(apifoxServerTextField);
    }

    public JTextField getApifoxTokenTextField() {
        return apifoxTokenTextField;
    }

    public void setApifoxTokenTextField(String apifoxTokenTextField) {
        this.apifoxTokenTextField.setText(apifoxTokenTextField);
    }

    public JLabel getYapiServerLabel() {
        return yapiServerLabel;
    }

    public void setYapiServerLabel(JLabel yapiServerLabel) {
        this.yapiServerLabel = yapiServerLabel;
    }

    public JLabel getYapiTokenLabel() {
        return yapiTokenLabel;
    }

    public void setYapiTokenLabel(JLabel yapiTokenLabel) {
        this.yapiTokenLabel = yapiTokenLabel;
    }

    public JPanel getApifoxPanel() {
        return apifoxPanel;
    }

    public void setApifoxPanel(JPanel apifoxPanel) {
        this.apifoxPanel = apifoxPanel;
    }

    public JLabel getApifoxServerLabel() {
        return apifoxServerLabel;
    }

    public void setApifoxServerLabel(JLabel apifoxServerLabel) {
        this.apifoxServerLabel = apifoxServerLabel;
    }

    public JLabel getApifoxTokenLabel() {
        return apifoxTokenLabel;
    }

    public void setApifoxTokenLabel(JLabel apifoxTokenLabel) {
        this.apifoxTokenLabel = apifoxTokenLabel;
    }

    public JCheckBox getYapiEnableCheckBox() {
        return yapiEnableCheckBox;
    }

    public void setYapiEnableCheckBox(Boolean yapiEnableCheckBox) {
        this.yapiEnableCheckBox.setSelected(yapiEnableCheckBox);
    }

    public JLabel getYapiIconLabel() {
        return yapiIconLabel;
    }

    public void setYapiIconLabel(JLabel yapiIconLabel) {
        this.yapiIconLabel = yapiIconLabel;
    }

    public JLabel getApifoxIconLabel() {
        return apifoxIconLabel;
    }

    public void setApifoxIconLabel(JLabel apifoxIconLabel) {
        this.apifoxIconLabel = apifoxIconLabel;
    }

    public JCheckBox getApifoxEnableCheckBox() {
        return apifoxEnableCheckBox;
    }

    public void setApifoxEnableCheckBox(Boolean apifoxEnableCheckBox) {
        this.apifoxEnableCheckBox.setSelected(apifoxEnableCheckBox);
    }

}
