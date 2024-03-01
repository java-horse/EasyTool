package easy.settings.doc.template;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class AbstractJavaDocTemplateConfigurable<T extends AbstractJavaDocTemplateSettingView> implements Configurable {

    @Nullable
    @Override
    public JComponent createComponent() {
        return getView().getComponent();
    }

    public abstract T getView();

}
