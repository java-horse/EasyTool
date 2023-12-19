package easy.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.icons.EasyIcons;
import easy.util.BundleUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @project: EasyTool
 * @package: easy.form
 * @author: mabin
 * @date: 2023/12/17 11:01:56
 */
public class CommonSettingView {

    private static final Logger log = Logger.getInstance(CommonSettingView.class);

    private CommonConfig commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();

    private JPanel panel;
    private JPanel commonPanel;
    private JLabel swaggerConfirmModelTipsLabel;
    private JLabel swaggerConfirmModelLabel;
    private JCheckBox swaggerConfirmYesCheckBox;
    private JCheckBox swaggerConfirmNoCheckBox;
    private JLabel searchApiTipsLabel;
    private JLabel searchApiLabel;
    private JRadioButton searchApiDefaultIconRadioButton;
    private JRadioButton searchApiCuteIconRadioButton;
    private JLabel translateConfirmInputModelTipsLabel;
    private JLabel translateConfirmInputModelLabel;
    private JCheckBox translateConfirmInputModelYesCheckBox;
    private JCheckBox translateConfirmInputModelNoCheckBox;

    public CommonSettingView() {
        swaggerConfirmYesCheckBox.addChangeListener(e -> swaggerConfirmNoCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        swaggerConfirmNoCheckBox.addChangeListener(e -> swaggerConfirmYesCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        searchApiDefaultIconRadioButton.addChangeListener(e -> searchApiCuteIconRadioButton.setSelected(!((JRadioButton) e.getSource()).isSelected()));
        searchApiCuteIconRadioButton.addChangeListener(e -> searchApiDefaultIconRadioButton.setSelected(!((JRadioButton) e.getSource()).isSelected()));
        translateConfirmInputModelYesCheckBox.addChangeListener(e -> translateConfirmInputModelNoCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        translateConfirmInputModelNoCheckBox.addChangeListener(e -> translateConfirmInputModelYesCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        swaggerConfirmModelTipsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Messages.showMessageDialog(BundleUtil.getI18n("swagger.confirm.model.checkBox.tip.text"), BundleUtil.getI18n("common.doubt.tips"), EasyIcons.ICON.DOUBT);
            }
        });
        searchApiTipsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Messages.showMessageDialog(BundleUtil.getI18n("search.api.icon.tip.text"), BundleUtil.getI18n("common.doubt.tips"), EasyIcons.ICON.DOUBT);
            }
        });
        translateConfirmInputModelTipsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Messages.showMessageDialog(BundleUtil.getI18n("translate.confirm.model.checkBox.tip.text"), BundleUtil.getI18n("common.doubt.tips"), EasyIcons.ICON.DOUBT);
            }
        });
    }

    private void createUIComponents() {
        commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();
    }

    /**
     * 通用设置重置处理
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/12/17 11:47
     */
    public void reset() {
        if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmYesCheckBox())) {
            setSwaggerConfirmYesCheckBox(Boolean.TRUE);
            setSwaggerConfirmNoCheckBox(Boolean.FALSE);
        } else {
            setSwaggerConfirmYesCheckBox(Boolean.FALSE);
            setSwaggerConfirmNoCheckBox(Boolean.TRUE);
        }
        if (Boolean.TRUE.equals(commonConfig.getSearchApiDefaultIconRadioButton())) {
            setSearchApiDefaultIconRadioButton(Boolean.TRUE);
            setSearchApiCuteIconRadioButton(Boolean.FALSE);
        } else {
            setSearchApiDefaultIconRadioButton(Boolean.FALSE);
            setSearchApiCuteIconRadioButton(Boolean.TRUE);
        }
        if (Boolean.TRUE.equals(commonConfig.getTranslateConfirmInputModelYesCheckBox())) {
            setTranslateConfirmInputModelYesCheckBox(Boolean.TRUE);
            setTranslateConfirmInputModelNoCheckBox(Boolean.FALSE);
        } else {
            setTranslateConfirmInputModelYesCheckBox(Boolean.FALSE);
            setTranslateConfirmInputModelNoCheckBox(Boolean.TRUE);
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    public JPanel getCommonPanel() {
        return commonPanel;
    }

    public void setCommonPanel(JPanel commonPanel) {
        this.commonPanel = commonPanel;
    }

    public JLabel getSwaggerConfirmModelTipsLabel() {
        return swaggerConfirmModelTipsLabel;
    }

    public void setSwaggerConfirmModelTipsLabel(JLabel swaggerConfirmModelTipsLabel) {
        this.swaggerConfirmModelTipsLabel = swaggerConfirmModelTipsLabel;
    }

    public JLabel getSwaggerConfirmModelLabel() {
        return swaggerConfirmModelLabel;
    }

    public void setSwaggerConfirmModelLabel(JLabel swaggerConfirmModelLabel) {
        this.swaggerConfirmModelLabel = swaggerConfirmModelLabel;
    }

    public JCheckBox getSwaggerConfirmYesCheckBox() {
        return swaggerConfirmYesCheckBox;
    }

    public void setSwaggerConfirmYesCheckBox(Boolean swaggerConfirmYesCheckBox) {
        this.swaggerConfirmYesCheckBox.setSelected(swaggerConfirmYesCheckBox);
    }

    public JCheckBox getSwaggerConfirmNoCheckBox() {
        return swaggerConfirmNoCheckBox;
    }

    public void setSwaggerConfirmNoCheckBox(Boolean swaggerConfirmNoCheckBox) {
        this.swaggerConfirmNoCheckBox.setSelected(swaggerConfirmNoCheckBox);
    }

    public JLabel getSearchApiTipsLabel() {
        return searchApiTipsLabel;
    }

    public void setSearchApiTipsLabel(JLabel searchApiTipsLabel) {
        this.searchApiTipsLabel = searchApiTipsLabel;
    }

    public JLabel getSearchApiLabel() {
        return searchApiLabel;
    }

    public void setSearchApiLabel(JLabel searchApiLabel) {
        this.searchApiLabel = searchApiLabel;
    }

    public JRadioButton getSearchApiDefaultIconRadioButton() {
        return searchApiDefaultIconRadioButton;
    }

    public void setSearchApiDefaultIconRadioButton(Boolean searchApiDefaultIconRadioButton) {
        this.searchApiDefaultIconRadioButton.setSelected(searchApiDefaultIconRadioButton);
    }

    public JRadioButton getSearchApiCuteIconRadioButton() {
        return searchApiCuteIconRadioButton;
    }

    public void setSearchApiCuteIconRadioButton(Boolean searchApiCuteIconRadioButton) {
        this.searchApiCuteIconRadioButton.setSelected(searchApiCuteIconRadioButton);
    }

    public JLabel getTranslateConfirmInputModelTipsLabel() {
        return translateConfirmInputModelTipsLabel;
    }

    public void setTranslateConfirmInputModelTipsLabel(JLabel translateConfirmInputModelTipsLabel) {
        this.translateConfirmInputModelTipsLabel = translateConfirmInputModelTipsLabel;
    }

    public JLabel getTranslateConfirmInputModelLabel() {
        return translateConfirmInputModelLabel;
    }

    public void setTranslateConfirmInputModelLabel(JLabel translateConfirmInputModelLabel) {
        this.translateConfirmInputModelLabel = translateConfirmInputModelLabel;
    }

    public JCheckBox getTranslateConfirmInputModelYesCheckBox() {
        return translateConfirmInputModelYesCheckBox;
    }

    public void setTranslateConfirmInputModelYesCheckBox(Boolean translateConfirmInputModelYesCheckBox) {
        this.translateConfirmInputModelYesCheckBox.setSelected(translateConfirmInputModelYesCheckBox);
    }

    public JCheckBox getTranslateConfirmInputModelNoCheckBox() {
        return translateConfirmInputModelNoCheckBox;
    }

    public void setTranslateConfirmInputModelNoCheckBox(Boolean translateConfirmInputModelNoCheckBox) {
        this.translateConfirmInputModelNoCheckBox.setSelected(translateConfirmInputModelNoCheckBox);
    }

}
