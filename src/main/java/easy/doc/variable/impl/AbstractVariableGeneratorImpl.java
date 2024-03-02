package easy.doc.variable.impl;

import com.intellij.openapi.application.ApplicationManager;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.doc.variable.VariableGenerator;

public abstract class AbstractVariableGeneratorImpl implements VariableGenerator {

    @Override
    public JavaDocConfig getConfig() {
        return ApplicationManager.getApplication().getService(JavaDocConfigComponent.class).getState();
    }

}
