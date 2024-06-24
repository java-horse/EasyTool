package easy.settings.doc.template;

import com.intellij.openapi.options.ConfigurationException;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.config.doc.JavaDocTemplateConfig;
import easy.form.doc.template.MethodSettingsView;
import easy.helper.ServiceHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;

import java.util.Objects;
import java.util.TreeMap;

public class JavaDocMethodSettingsConfigurable extends AbstractJavaDocTemplateConfigurable<MethodSettingsView> {
    private JavaDocConfig config = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private MethodSettingsView view = new MethodSettingsView(config);


    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "MethodDoc";
    }

    @Override
    public MethodSettingsView getView() {
        return view;
    }

    @Override
    public boolean isModified() {
        JavaDocTemplateConfig templateConfig = config.getJavaDocMethodTemplateConfig();
        if (!Objects.equals(templateConfig.getIsDefault(), view.isDefault())) {
            return true;
        }
        return !Objects.equals(templateConfig.getTemplate(), view.getTemplate());
    }

    @Override
    public void apply() throws ConfigurationException {
        JavaDocTemplateConfig templateConfig = config.getJavaDocMethodTemplateConfig();
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
            if (!temp.startsWith("/**") || !temp.endsWith("*/")) {
                throw new ConfigurationException("模板格式不正确，正确的JavaDoc应该以\"/**\"开头，以\"*/\"结束");
            }
            if (StringUtils.containsAny(temp, "@params", "@param", "@return")) {
                throw new ConfigurationException("模板格式不正确，无需额外配置@params、@param、@return等标识");
            }
        }
    }

    @Override
    public void reset() {
        JavaDocTemplateConfig templateConfig = config.getJavaDocMethodTemplateConfig();
        view.setDefault(BooleanUtils.isTrue(templateConfig.getIsDefault()));
        view.setTemplate(templateConfig.getTemplate());
    }

}
