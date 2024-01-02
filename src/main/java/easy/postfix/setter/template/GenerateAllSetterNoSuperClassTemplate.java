package easy.postfix.setter.template;

import easy.postfix.base.AbstractVariableGenerateTemplate;
import easy.postfix.base.BaseVar;
import easy.postfix.base.GenerateBase;
import easy.postfix.setter.GenerateSetter;
import org.jetbrains.annotations.NotNull;

public class GenerateAllSetterNoSuperClassTemplate extends AbstractVariableGenerateTemplate {

    public GenerateAllSetterNoSuperClassTemplate() {
        super("allsetp", "Generate Setter but no super class");
    }

    @NotNull
    @Override
    protected GenerateBase getGenerateByVar(BaseVar baseVar) {
        return new GenerateSetter(true, baseVar, true);
    }

}
