package easy.doc.variable.impl;

import com.intellij.openapi.application.ApplicationManager;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.doc.variable.VariableGenerator;
import easy.helper.ServiceHelper;

public abstract class AbstractVariableGeneratorImpl implements VariableGenerator {

    @Override
    public JavaDocConfig getConfig() {
        return ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    }

}
