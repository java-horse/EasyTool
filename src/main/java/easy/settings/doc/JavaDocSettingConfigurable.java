package easy.settings.doc;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.form.doc.JavaDocSettingView;
import easy.helper.ServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JavaDocSettingConfigurable implements Configurable {

    private static final Logger log = Logger.getInstance(JavaDocSettingConfigurable.class);
    private JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private JavaDocSettingView javaDocSettingView = new JavaDocSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "JavaDoc";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return javaDocSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !StringUtils.equals(javaDocConfig.getAuthor(), javaDocSettingView.getAuthorTextField().getText())
                || !StringUtils.equals(javaDocConfig.getDateFormat(), javaDocSettingView.getAuthorTextField().getText())
                || !StringUtils.equals(javaDocConfig.getMethodReturnType(), String.valueOf(javaDocSettingView.getMethodReturnTypeComboBox().getSelectedItem()))
                || !StringUtils.equals(javaDocConfig.getPropertyCommentType(), String.valueOf(javaDocSettingView.getPropertyCommentTypeComboBox().getSelectedItem()))
                || !StringUtils.equals(javaDocConfig.getPropertyCommentModel(), String.valueOf(javaDocSettingView.getPropertyCommentModelComboBox().getSelectedItem()));
    }

    @Override
    public void reset() {
        javaDocSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        javaDocConfig.setAuthor(javaDocSettingView.getAuthorTextField().getText());
        javaDocConfig.setDateFormat(javaDocSettingView.getDateFormatTextField().getText());
        javaDocConfig.setMethodReturnType(String.valueOf(javaDocSettingView.getMethodReturnTypeComboBox().getSelectedItem()));
        javaDocConfig.setPropertyCommentType(String.valueOf(javaDocSettingView.getPropertyCommentTypeComboBox().getSelectedItem()));
        javaDocConfig.setPropertyCommentModel(String.valueOf(javaDocSettingView.getPropertyCommentModelComboBox().getSelectedItem()));
    }

}
