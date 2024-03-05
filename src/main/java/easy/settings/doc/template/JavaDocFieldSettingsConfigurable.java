package easy.settings.doc.template;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.config.doc.JavaDocTemplateConfig;
import easy.form.doc.template.FieldSettingsView;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;

import java.util.Objects;
import java.util.TreeMap;

public class JavaDocFieldSettingsConfigurable extends AbstractJavaDocTemplateConfigurable<FieldSettingsView> {
    private JavaDocConfig config = ApplicationManager.getApplication().getService(JavaDocConfigComponent.class).getState();
    private FieldSettingsView view = new FieldSettingsView(config);

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "FieldDoc";
    }

    @Override
    public FieldSettingsView getView() {
        return view;
    }

    @Override
    public boolean isModified() {
        JavaDocTemplateConfig templateConfig = config.getJavaDocFieldTemplateConfig();
        if (!Objects.equals(templateConfig.getIsDefault(), view.isDefault())) {
            return true;
        }
        return !Objects.equals(templateConfig.getTemplate(), view.getTemplate());
    }

    @Override
    public void apply() throws ConfigurationException {
        JavaDocTemplateConfig templateConfig = config.getJavaDocFieldTemplateConfig();
        templateConfig.setIsDefault(view.isDefault());
        templateConfig.setTemplate(view.getTemplate());
        if (templateConfig.getCustomMap() == null) {
            templateConfig.setCustomMap(new TreeMap<>());
        }
        if (!view.isDefault()) {
            if (StringUtils.isBlank(view.getTemplate())) {
                throw new ConfigurationException("自定义模板不能为空");
            }
            String temp = StringUtils.strip(view.getTemplate());
            if ((!StringUtils.startsWith(temp, "/**") && !StringUtils.endsWith(temp, "*/"))
                    || !StringUtils.startsWith(temp, "//") || (!StringUtils.startsWith(temp, "/*") && !StringUtils.endsWith(temp, "*/"))) {
                throw new ConfigurationException("模板格式不正确，JavaDoc应该以\"/**\"开头，以\"*/\"结束，普通文本注释应该以\"//\"开头或者\"/*\"开头\"*/\"结尾");
            }
        }
    }

    @Override
    public void reset() {
        JavaDocTemplateConfig templateConfig = config.getJavaDocFieldTemplateConfig();
        view.setDefault(BooleanUtils.isTrue(templateConfig.getIsDefault()));
        view.setTemplate(templateConfig.getTemplate());
    }

}
