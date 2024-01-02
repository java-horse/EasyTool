package easy.postfix.setter.template;

import easy.postfix.base.AbstractVariableGenerateTemplate;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.setter.GenerateSetter;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGenerateAllSetterTemplate extends AbstractVariableGenerateTemplate {

    private final boolean generateDefaultVal;

    protected AbstractGenerateAllSetterTemplate(boolean generateDefaultVal, String templateName, String example) {
        super(templateName, example);
        this.generateDefaultVal = generateDefaultVal;
    }

    @NotNull
    @Override
    protected GenerateBase getGenerateByVar(BaseVar baseVar) {
        return new GenerateSetter(generateDefaultVal, baseVar);
    }
}
