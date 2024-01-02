package easy.postfix.getter.template;

import easy.postfix.base.AbstractVariableGenerateTemplate;
import easy.postfix.base.BaseVar;
import easy.postfix.getter.GenerateGetter;
import org.jetbrains.annotations.NotNull;

public class GenerateAllGetterTemplate extends AbstractVariableGenerateTemplate {

    public GenerateAllGetterTemplate() {
        super("allget", "Generate Getter");
    }

    @Override
    protected @NotNull GenerateGetter getGenerateByVar(BaseVar baseVar) {
        return new GenerateGetter(baseVar);
    }

}
