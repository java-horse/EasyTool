package easy.settings.doc.template;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.config.doc.JavaDocTemplateConfig;
import easy.form.doc.template.ClassSettingsView;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;

import java.util.Objects;
import java.util.TreeMap;

public class JavaDocClassSettingConfigurable extends AbstractJavaDocTemplateConfigurable<ClassSettingsView> {
    private JavaDocConfig config = ApplicationManager.getApplication().getService(JavaDocConfigComponent.class).getState();
    private ClassSettingsView view = new ClassSettingsView(config);

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ClassDoc";
    }

    @Override
    public ClassSettingsView getView() {
        return view;
    }

    @Override
    public boolean isModified() {
        JavaDocTemplateConfig templateConfig = config.getJavaDocClassTemplateConfig();
        if (!Objects.equals(templateConfig.getIsDefault(), view.isDefault())) {
            return true;
        }
        return !Objects.equals(templateConfig.getTemplate(), view.getTemplate());
    }

    @Override
    public void apply() throws ConfigurationException {
        JavaDocTemplateConfig templateConfig = config.getJavaDocClassTemplateConfig();
        templateConfig.setIsDefault(view.isDefault());
        templateConfig.setTemplate(view.getTemplate());
        if (MapUtils.isEmpty(templateConfig.getCustomMap())) {
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
        }
    }

    @Override
    public void reset() {
        JavaDocTemplateConfig templateConfig = config.getJavaDocClassTemplateConfig();
        view.setDefault(BooleanUtils.isTrue(templateConfig.getIsDefault()));
        view.setTemplate(templateConfig.getTemplate());
    }

}
