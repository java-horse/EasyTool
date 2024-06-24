package easy.form.doc;

import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.helper.ServiceHelper;

import javax.swing.*;

public class JavaDocSettingView {
    private JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();

    private JPanel panel;
    private JPanel commonPanel;
    private JTextField authorTextField;
    private JLabel authorLabel;
    private JTextField dateFormatTextField;
    private JLabel dateFormatLabel;
    private JComboBox methodReturnTypeComboBox;
    private JLabel methodReturnTypeLabel;
    private JComboBox propertyCommentTypeComboBox;
    private JLabel propertyCommentTypeLabel;
    private JComboBox propertyCommentModelComboBox;


    public JavaDocSettingView() {

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

}
