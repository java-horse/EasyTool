package easy.postfix.convert;

import com.intellij.codeInsight.template.impl.Variable;
import easy.postfix.base.BaseVar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenerateConvertForDst extends GenerateConvert {

    public GenerateConvertForDst(BaseVar varForSet, BaseVar varForGet) {
        super(varForSet, varForGet);
    }

    @Nullable
    @Override
    public Variable[] getTemplateVariables() {
        Variable variable = new Variable("SOURCE", "camelCase(\"sourceObj\")", "sourceObj", true);
        return new Variable[]{variable};
    }

    @NotNull
    @Override
    protected String getSetVal(String getMethodName) {
        return "$SOURCE$." + getMethodName + "()";
    }

}
