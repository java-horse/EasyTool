package easy.form.doc;

import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;

import javax.swing.*;

public class JavaDocSettingView {
    private JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();

    private JPanel panel;
    private JPanel commonPanel;
    private JTextField authorTextField;
    private JLabel authorLabel;
    private JTextField dateFormatTextField;
    private JLabel dateFormatLabel;
    private JComboBox<String> methodReturnTypeComboBox;
    private JLabel methodReturnTypeLabel;
    private JComboBox<String> propertyCommentTypeComboBox;
    private JLabel propertyCommentTypeLabel;
    private JComboBox<String> propertyCommentModelComboBox;
    private JCheckBox autoGenerateDocClassCheckBox;
    private JCheckBox autoGenerateDocMethodCheckBox;
    private JCheckBox autoGenerateDocFieldCheckBox;
    private JLabel autoGenerateDocTipLabel;
    private JComboBox<String> coverModelComboBox;
    private JLabel coverModelTipLabel;
    private JCheckBox coverHintPromptCheckBox;


    public JavaDocSettingView() {
        EasyCommonUtil.customLabelTipText(autoGenerateDocTipLabel, "文件保存时自动生成JavaDoc注释(实验性功能, 谨慎开启!)");
        EasyCommonUtil.customLabelTipText(coverModelTipLabel, "JavaDoc注释覆盖模式: 自定义注释模板情况下有效, 默认: 智能合并, 忽略注释表示: JavaDoc注释已存在是不会重新生成");
    }

    private void createUIComponents() {
        javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    }

    public void reset() {
        setAuthorTextField(javaDocConfig.getAuthor());
        setDateFormatTextField(javaDocConfig.getDateFormat());
        setMethodReturnTypeComboBox(javaDocConfig.getMethodReturnType());
        setPropertyCommentTypeComboBox(javaDocConfig.getPropertyCommentType());
        setPropertyCommentModelComboBox(javaDocConfig.getPropertyCommentModel());
        setAutoGenerateDocClassCheckBox(javaDocConfig.getAutoGenerateDocClass());
        setAutoGenerateDocMethodCheckBox(javaDocConfig.getAutoGenerateDocMethod());
        setAutoGenerateDocFieldCheckBox(javaDocConfig.getAutoGenerateDocField());
        setCoverModelComboBox(javaDocConfig.getCoverModel());
        setCoverHintPromptCheckBox(javaDocConfig.getCoverHintPrompt());
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

    public JTextField getAuthorTextField() {
        return authorTextField;
    }

    public void setAuthorTextField(String authorTextField) {
        this.authorTextField.setText(authorTextField);
    }

    public JLabel getAuthorLabel() {
        return authorLabel;
    }

    public void setAuthorLabel(JLabel authorLabel) {
        this.authorLabel = authorLabel;
    }

    public JTextField getDateFormatTextField() {
        return dateFormatTextField;
    }

    public void setDateFormatTextField(String dateFormatTextField) {
        this.dateFormatTextField.setText(dateFormatTextField);
    }

    public JLabel getDateFormatLabel() {
        return dateFormatLabel;
    }

    public void setDateFormatLabel(JLabel dateFormatLabel) {
        this.dateFormatLabel = dateFormatLabel;
    }

    public JComboBox getMethodReturnTypeComboBox() {
        return methodReturnTypeComboBox;
    }

    public void setMethodReturnTypeComboBox(String methodReturnTypeComboBox) {
        this.methodReturnTypeComboBox.setSelectedItem(methodReturnTypeComboBox);
    }

    public JLabel getMethodReturnTypeLabel() {
        return methodReturnTypeLabel;
    }

    public void setMethodReturnTypeLabel(JLabel methodReturnTypeLabel) {
        this.methodReturnTypeLabel = methodReturnTypeLabel;
    }

    public JComboBox getPropertyCommentTypeComboBox() {
        return propertyCommentTypeComboBox;
    }

    public void setPropertyCommentTypeComboBox(String propertyCommentTypeComboBox) {
        this.propertyCommentTypeComboBox.setSelectedItem(propertyCommentTypeComboBox);
    }

    public JLabel getPropertyCommentTypeLabel() {
        return propertyCommentTypeLabel;
    }

    public void setPropertyCommentTypeLabel(JLabel propertyCommentTypeLabel) {
        this.propertyCommentTypeLabel = propertyCommentTypeLabel;
    }

    public JComboBox getPropertyCommentModelComboBox() {
        return propertyCommentModelComboBox;
    }

    public void setPropertyCommentModelComboBox(String propertyCommentModelComboBox) {
        this.propertyCommentModelComboBox.setSelectedItem(propertyCommentModelComboBox);
    }

    public JCheckBox getAutoGenerateDocClassCheckBox() {
        return autoGenerateDocClassCheckBox;
    }

    public void setAutoGenerateDocClassCheckBox(Boolean autoGenerateDocClassCheckBox) {
        this.autoGenerateDocClassCheckBox.setSelected(autoGenerateDocClassCheckBox);
    }

    public JCheckBox getAutoGenerateDocMethodCheckBox() {
        return autoGenerateDocMethodCheckBox;
    }

    public void setAutoGenerateDocMethodCheckBox(Boolean autoGenerateDocMethodCheckBox) {
        this.autoGenerateDocMethodCheckBox.setSelected(autoGenerateDocMethodCheckBox);
    }

    public JCheckBox getAutoGenerateDocFieldCheckBox() {
        return autoGenerateDocFieldCheckBox;
    }

    public void setAutoGenerateDocFieldCheckBox(Boolean autoGenerateDocFieldCheckBox) {
        this.autoGenerateDocFieldCheckBox.setSelected(autoGenerateDocFieldCheckBox);
    }

    public JComboBox<String> getCoverModelComboBox() {
        return coverModelComboBox;
    }

    public void setCoverModelComboBox(String coverModelComboBox) {
        this.coverModelComboBox.setSelectedItem(coverModelComboBox);
    }

    public JCheckBox getCoverHintPromptCheckBox() {
        return coverHintPromptCheckBox;
    }

    public void setCoverHintPromptCheckBox(Boolean coverHintPromptCheckBox) {
        this.coverHintPromptCheckBox.setSelected(coverHintPromptCheckBox);
    }

}
