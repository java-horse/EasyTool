package easy.postfix.getter.template;

import easy.postfix.base.AbstractVariableGenerateTemplate;
import easy.postfix.base.BaseVar;
import easy.postfix.getter.GenerateGetter;
import org.jetbrains.annotations.NotNull;

public class GenerateAllGetterNoSuperClassTemplate extends AbstractVariableGenerateTemplate {

    public GenerateAllGetterNoSuperClassTemplate() {
        super("allgetp", "Generate Getter but no super class");
    }

    @Override
    protected @NotNull GenerateGetter getGenerateByVar(BaseVar baseVar) {
        return new GenerateGetter(baseVar, true);
    }
}
