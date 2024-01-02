package easy.postfix.setter.template;

import easy.postfix.base.AbstractVariableGenerateTemplate;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.setter.GenerateChain;
import org.jetbrains.annotations.NotNull;

public class GenerateAllSetterWithChainTemplate extends AbstractVariableGenerateTemplate {

    private final boolean generateDefaultVal;

    public GenerateAllSetterWithChainTemplate() {
        super("allsetc", "Generate setter with chain; if you using @Accessors(chain = true)");
        this.generateDefaultVal = true;
    }

    @NotNull
    @Override
    protected GenerateBase getGenerateByVar(BaseVar baseVar) {
        return new GenerateChain(generateDefaultVal, baseVar);
    }
}
