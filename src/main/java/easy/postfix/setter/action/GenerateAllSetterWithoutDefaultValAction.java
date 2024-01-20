package easy.postfix.setter.action;

import easy.enums.PostfixShortCutEnum;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GenerateAllSetterWithoutDefaultValAction extends GenerateAllSetterAction {

    public GenerateAllSetterWithoutDefaultValAction() {
        super(false);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return PostfixShortCutEnum.GENERATE_SETTER_NO_DEFAULT_VAL.getName();
    }

    @NotNull
    @Override
    public String getText() {
        return PostfixShortCutEnum.GENERATE_SETTER_NO_DEFAULT_VAL.getName();
    }

}
